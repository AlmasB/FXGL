/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.parser.tiled

import com.almasb.fxgl.core.logging.Logger
import javafx.scene.paint.Color
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
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

private const val TILED_VERSION_LATEST = "1.1.2"

class TMXParser {

    private val log = Logger.get(TMXParser::class.java)

    fun parse(inputStream: InputStream): TiledMap {
        try {
            val inputFactory = XMLInputFactory.newInstance()
            val eventReader = inputFactory.createXMLEventReader(inputStream)

            val map = TiledMap()
            val layers = arrayListOf<Layer>()
            val tilesets = arrayListOf<Tileset>()

            // vars

            var currentLayer = Layer()
            var currentTileset = Tileset()
            var currentObject = TiledObject()

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
                            currentObject = TiledObject()
                            parseObject(currentLayer, currentObject, start)
                        }

                        "property" -> {
                            parseObjectProperty(currentObject, start)
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

        } catch (e: Exception) {
            throw RuntimeException("Cannot parse tmx file: $e")
        }
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

                        InflaterInputStream(ByteArrayInputStream(bytes)).use {
                            it.copyTo(baos)
                        }

                        bytes = baos.toByteArray()
                    }

                    "gzip" -> {
                        val baos = ByteArrayOutputStream()

                        GZIPInputStream(ByteArrayInputStream(bytes)).use {
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
        obj.width = start.getInt("width")
        obj.height = start.getInt("height")
        obj.gid = start.getInt("gid")

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
                throw RuntimeException("Unknown property type: $propType for $propName")
            }
        }
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

private fun StartElement.getString(attrName: String): String {
    return this.getAttributeByName(QName(attrName))?.value.orEmpty()
}