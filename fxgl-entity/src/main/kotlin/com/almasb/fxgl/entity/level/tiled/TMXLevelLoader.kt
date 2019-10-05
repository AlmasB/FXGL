/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.level.tiled

import com.almasb.fxgl.entity.Entity
import com.almasb.fxgl.entity.GameWorld
import com.almasb.fxgl.entity.SpawnData
import com.almasb.fxgl.entity.level.Level
import com.almasb.fxgl.entity.level.LevelLoader
import com.almasb.fxgl.entity.level.LevelLoadingException
import com.almasb.sslogger.Logger
import javafx.scene.paint.Color
import javafx.scene.shape.Polygon
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.URL
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*
import java.util.zip.GZIPInputStream
import java.util.zip.InflaterInputStream
import javax.xml.namespace.QName
import javax.xml.stream.XMLInputFactory
import javax.xml.stream.events.StartElement

/**
 * TMX Format version 1.1 reference: http://docs.mapeditor.org/en/latest/reference/tmx-map-format/
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */

private const val TILED_VERSION_LATEST = "1.2.3"

class TMXLevelLoader : LevelLoader {

    private val log = Logger.get<TMXLevelLoader>()

    override fun load(url: URL, world: GameWorld): Level {
        try {
            val map = url.openStream().use { parse(it) }

            log.debug("Parsed raw map: $map")

            val tilesetLoader = TilesetLoader(map, url)

            val tileLayerEntities = createTileLayerEntities(map, tilesetLoader)

            val objectEntities = createObjectLayerEntities(map, tilesetLoader, world)

            val level = Level(map.width * map.tilewidth, map.height * map.tileheight, tileLayerEntities + objectEntities)

            map.properties.forEach { key, value ->

                if (value is Float) {
                    level.properties.setValue(key, value.toDouble())
                } else {
                    level.properties.setValue(key, value)
                }
            }

            return level

        } catch (e: Exception) {
            log.warning("Parse error", e)
            e.printStackTrace()
            throw LevelLoadingException("${e.message}", e)
        }
    }

    private fun createTileLayerEntities(map: TiledMap, tilesetLoader: TilesetLoader): List<Entity> {
        log.debug("Creating tile layer entities")

        return map.layers.filter { it.type == "tilelayer" }
                .map { layer ->
                    Entity().also { it.viewComponent.addChild(tilesetLoader.loadView(layer.name)) }
                }
    }

    private fun createObjectLayerEntities(map: TiledMap, tilesetLoader: TilesetLoader, world: GameWorld): List<Entity> {
        log.debug("Creating object layer entities")

        return map.layers.filter { it.type == "objectgroup" }
                .flatMap { it.objects }
                .map { tiledObject ->
                    val data = SpawnData(
                            tiledObject.x.toDouble(),
                            // it appears that if object has non-zero gid then its y is flipped
                            (tiledObject.y - if (tiledObject.gid == 0) 0 else tiledObject.height).toDouble()
                    )

                    // make data available when inside factory's spawn methods
                    data.run {
                        put("name", tiledObject.name)
                        put("type", tiledObject.type)
                        put("width", tiledObject.width)
                        put("height", tiledObject.height)
                        put("rotation", tiledObject.rotation)
                        put("id", tiledObject.id)
                        put("gid", tiledObject.gid)

                        tiledObject.properties.forEach {
                            put(it.key, it.value)
                        }
                    }

                    // we populate the entity properties in case the factory didn't make use of them
                    world.create(tiledObject.type, data).also { e ->
                        data.data.forEach {
                            e.setProperty(it.key, it.value)
                        }

                        e.setPosition(data.x, data.y)
                        e.rotation = tiledObject.rotation.toDouble()

                        // non-zero gid means view is read from the tileset
                        if (tiledObject.gid != 0) {
                            e.viewComponent.addChild(tilesetLoader.loadView(tiledObject.gid, tiledObject.isFlippedHorizontal, tiledObject.isFlippedVertical))
                        }
                    }
                }
    }

    fun parse(inputStream: InputStream): TiledMap {
        val inputFactory = XMLInputFactory.newInstance()
        val eventReader = inputFactory.createXMLEventReader(inputStream, "UTF-8")

        val map = TiledMap()
        val layers = arrayListOf<Layer>()
        val tilesets = arrayListOf<Tileset>()

        // vars

        var currentLayer = Layer()
        var currentTileset = Tileset()
        var currentObject = TiledObject()

        var mapPropertiesFinished = false

        while (eventReader.hasNext()) {
            val event = eventReader.nextEvent()

            if (event.isStartElement) {
                val start = event.asStartElement()

                when (start.name.localPart) {
                    "map" -> { parseMap(map, start) }

                    "tileset" -> {
                        currentTileset = Tileset()
                        parseTileset(currentTileset, start)
                    }

                    "image" -> {
                        parseImage(currentTileset, start)
                    }

                    "layer" -> {
                        currentLayer = Layer()
                        parseTileLayer(currentLayer, start)
                    }

                    "data" -> {
                        parseData(currentLayer, eventReader.elementText, start)
                    }

                    "objectgroup" -> {
                        currentLayer = Layer()
                        parseObjectGroupLayer(currentLayer, start)
                    }

                    "object" -> {
                        // I'm making an assumption that map properties are defined before objects
                        // If there are no objects in the map this flag is irrelevant anyway
                        // as there can be no confusion between map and object properties
                        mapPropertiesFinished = true

                        currentObject = TiledObject()
                        parseObject(currentLayer, currentObject, start)
                    }

                    "property" -> {
                        if (mapPropertiesFinished) {
                            parseObjectProperty(currentObject, start)
                        } else {
                            parseMapProperty(map, start)
                        }
                    }

                    "polygon" -> {
                        parseObjectPolygon(currentObject, start)
                    }
                }
            }

            if (event.isEndElement) {
                val endElement = event.asEndElement()

                when (endElement.name.localPart) {
                    "tileset" -> { tilesets.add(currentTileset) }
                    "layer", "objectgroup" -> { layers.add(currentLayer) }
                }
            }
        }

        map.layers = layers
        map.tilesets = tilesets

        return map
    }

    private fun parseMap(map: TiledMap, start: StartElement) {
        map.width = start.getInt("width")
        map.height = start.getInt("height")
        map.tilewidth = start.getInt("tilewidth")
        map.tileheight = start.getInt("tileheight")
        map.nextobjectid = start.getInt("nextobjectid")

        map.type = "map"
        map.version = 1
        map.infinite = start.getInt("infinite") == 1
        map.backgroundcolor = start.getString("backgroundcolor")
        map.orientation = start.getString("orientation")
        map.renderorder = start.getString("renderorder")
        map.tiledversion = start.getString("tiledversion")

        if (map.tiledversion != TILED_VERSION_LATEST) {
            log.warning("TiledMap generated from ${map.tiledversion}. Supported version: ${TILED_VERSION_LATEST}. Some features may not be parsed fully.")
        }
    }

    private fun parseTileset(tileset: Tileset, start: StartElement) {
        tileset.firstgid = start.getInt("firstgid")
        tileset.name = start.getString("name")
        tileset.tilewidth = start.getInt("tilewidth")
        tileset.tileheight = start.getInt("tileheight")
        tileset.spacing = start.getInt("spacing")
        tileset.tilecount = start.getInt("tilecount")
        tileset.columns = start.getInt("columns")
    }

    private fun parseImage(tileset: Tileset, start: StartElement) {
        tileset.image = start.getString("source")
        tileset.imagewidth = start.getInt("width")
        tileset.imageheight = start.getInt("height")
        tileset.transparentcolor = start.getString("trans")
    }

    private fun parseTileLayer(layer: Layer, start: StartElement) {
        layer.type = "tilelayer"
        layer.name = start.getString("name")
        layer.width = start.getInt("width")
        layer.height = start.getInt("height")
        layer.opacity = start.getFloat("opacity")
        layer.visible = start.getInt("visible") == 1
    }

    private fun parseData(layer: Layer, data: String, start: StartElement) {
        when (start.getString("encoding")) {
            "csv" -> {
                layer.data = data.replace("\n", "").split(",").map { it.toInt() }
            }

            "base64" -> {
                var bytes = Base64.getDecoder().decode(data.trim())

                when (start.getString("compression")) {
                    "zlib" -> {
                        val baos = ByteArrayOutputStream()

                        InflaterInputStream(bytes.inputStream()).use {
                            it.copyTo(baos)
                        }

                        bytes = baos.toByteArray()
                    }

                    "gzip" -> {
                        val baos = ByteArrayOutputStream()

                        GZIPInputStream(bytes.inputStream()).use {
                            it.copyTo(baos)
                        }

                        bytes = baos.toByteArray()
                    }
                }

                val ints = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asIntBuffer()

                val intArray = IntArray(ints.limit())
                ints.get(intArray)

                layer.data = intArray.toList()
            }
        }
    }

    private fun parseObjectGroupLayer(layer: Layer, start: StartElement) {
        layer.type = "objectgroup"
        layer.name = start.getString("name")
    }

    private fun parseObject(layer: Layer, obj: TiledObject, start: StartElement) {
        obj.name = start.getString("name")
        obj.type = start.getString("type")
        obj.id = start.getInt("id")
        obj.x = start.getInt("x")
        obj.y = start.getInt("y")
        obj.rotation = start.getFloat("rotation")
        obj.width = start.getInt("width")
        obj.height = start.getInt("height")

        // gid is stored as UInt, so parsing as int gives incorrect representation
        val gidUInt = start.getUInt("gid")

        // from https://doc.mapeditor.org/en/stable/reference/tmx-map-format/#tile-flipping
        // Bit 32 (31th) is used for storing whether the tile is horizontally flipped,
        // Bit 31 (30th) is used for the vertically flipped
        val FLIPPED_HORIZONTALLY_FLAG = (1 shl 31).toUInt()
        val FLIPPED_VERTICALLY_FLAG   = (1 shl 30).toUInt()
        val FLIPPED_DIAGONALLY_FLAG   = (1 shl 29).toUInt()

        obj.isFlippedHorizontal = gidUInt and FLIPPED_HORIZONTALLY_FLAG != 0.toUInt()
        obj.isFlippedVertical = gidUInt and FLIPPED_VERTICALLY_FLAG != 0.toUInt()

        // get rid of the metadata, leaving us with gid
        val gid = (gidUInt and (FLIPPED_HORIZONTALLY_FLAG or FLIPPED_VERTICALLY_FLAG or FLIPPED_DIAGONALLY_FLAG).inv()).toInt()

        obj.gid = gid

        (layer.objects as MutableList).add(obj)
    }

    private fun parseObjectProperty(obj: TiledObject, start: StartElement) {
        val propName = start.getString("name")
        val propType = start.getString("type")

        (obj.propertytypes as MutableMap)[propName] = propType

        (obj.properties as MutableMap)[propName] = when (propType) {
            "int" -> {
                start.getInt("value")
            }

            "bool" -> {
                start.getBoolean("value")
            }

            "float" -> {
                start.getFloat("value")
            }

            "string", "" -> {
                start.getString("value")
            }

            "color" -> {
                start.getColor("value")
            }

            else -> {
                throw IllegalArgumentException("Unknown property type: $propType for $propName")
            }
        }
    }

    private fun parseMapProperty(map: TiledMap, start: StartElement) {
        val propName = start.getString("name")
        val propType = start.getString("type")

        (map.propertytypes as MutableMap)[propName] = propType

        (map.properties as MutableMap)[propName] = when (propType) {
            "int" -> {
                start.getInt("value")
            }

            "bool" -> {
                start.getBoolean("value")
            }

            "float" -> {
                start.getFloat("value")
            }

            "string", "" -> {
                start.getString("value")
            }

            "color" -> {
                start.getColor("value")
            }

            else -> {
                throw IllegalArgumentException("Unknown property type: $propType for $propName")
            }
        }
    }

    private fun parseObjectPolygon(obj: TiledObject, start: StartElement) {
        val data = start.getString("points")

        val points = data.split(" +".toRegex())
                .flatMap { it.split(",") }
                .map { it.toDouble() }
                .toDoubleArray()

        // https://github.com/AlmasB/FXGL/issues/575
        val polygon = Polygon(*points)

        (obj.properties as MutableMap)["polygon"] = polygon
    }
}

// these retrieve the value if exists or return a default

private fun StartElement.getColor(attrName: String): Color {
    return Color.web(this.getString(attrName))
}

private fun StartElement.getBoolean(attrName: String): Boolean {
    return this.getString(attrName).toBoolean()
}

private fun StartElement.getInt(attrName: String): Int {
    return this.getString(attrName).toIntOrNull() ?: this.getFloat(attrName).toInt()
}

private fun StartElement.getFloat(attrName: String): Float {
    return this.getString(attrName).toFloatOrNull() ?: 0.0f
}

private fun StartElement.getUInt(attrName: String): UInt {
    return this.getString(attrName).toUIntOrNull() ?: 0.toUInt()
}

private fun StartElement.getString(attrName: String): String {
    return this.getAttributeByName(QName(attrName))?.value.orEmpty()
}