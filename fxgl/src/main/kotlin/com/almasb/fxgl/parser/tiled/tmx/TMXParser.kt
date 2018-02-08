/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.parser.tiled.tmx

import com.almasb.fxgl.parser.tiled.Layer
import com.almasb.fxgl.parser.tiled.TiledMap
import com.almasb.fxgl.parser.tiled.TiledObject
import com.almasb.fxgl.parser.tiled.Tileset
import java.io.InputStream
import javax.xml.namespace.QName
import javax.xml.stream.XMLInputFactory

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class TMXParser {

    fun parse(inputStream: InputStream): TiledMap {
        try {
            // First, create a new XMLInputFactory
            val inputFactory = XMLInputFactory.newInstance()
            val eventReader = inputFactory.createXMLEventReader(inputStream)

            val map = TiledMap()
            val layers = arrayListOf<Layer>()
            val tilesets = arrayListOf<Tileset>()


            // vars

            var currentLayer = Layer()
            var currentTileset = Tileset()


            while (eventReader.hasNext()) {
                var event = eventReader.nextEvent()

                if (event.isStartElement) {
                    val startElement = event.asStartElement()

                    // If we have an item element, we create a new item
                    if (startElement.name.localPart == "map") {

                        map.width = startElement.getAttributeByName(QName("width")).value.toInt()
                        map.height = startElement.getAttributeByName(QName("height")).value.toInt()
                        map.tilewidth = startElement.getAttributeByName(QName("tilewidth")).value.toInt()
                        map.tileheight = startElement.getAttributeByName(QName("tileheight")).value.toInt()
                        map.orientation = startElement.getAttributeByName(QName("orientation")).value
                        map.nextobjectid = startElement.getAttributeByName(QName("nextobjectid")).value.toInt()
                    }

                    if (startElement.name.localPart == "layer") {

                        currentLayer = Layer()
                        currentLayer.type = "tilelayer"
                        currentLayer.name = startElement.getAttributeByName(QName("name")).value
                        currentLayer.width = startElement.getAttributeByName(QName("width")).value.toInt()
                        currentLayer.height = startElement.getAttributeByName(QName("height")).value.toInt()
                    }

                    if (startElement.name.localPart == "data") {

                        // TODO: parse other encodings than csv
                        //startElement.getAttributeByName(QName("encoding"))

                        val data = eventReader.elementText

                        currentLayer.data = data.replace("\n", "").split(",").map { it.toInt() }
                    }

                    if (startElement.name.localPart == "tileset") {

                        currentTileset = Tileset()
                        currentTileset.firstgid = startElement.getAttributeByName(QName("firstgid")).value.toInt()
                        currentTileset.name = startElement.getAttributeByName(QName("name")).value
                        currentTileset.tilewidth = startElement.getAttributeByName(QName("tilewidth")).value.toInt()
                        currentTileset.tileheight = startElement.getAttributeByName(QName("tileheight")).value.toInt()
                        currentTileset.spacing = startElement.getAttributeByName(QName("spacing")).value.toInt()
                        currentTileset.tilecount = startElement.getAttributeByName(QName("tilecount")).value.toInt()
                        currentTileset.columns = startElement.getAttributeByName(QName("columns")).value.toInt()
                    }

                    if (startElement.name.localPart == "image") {
                        currentTileset.image = startElement.getAttributeByName(QName("source")).value
                        currentTileset.imagewidth = startElement.getAttributeByName(QName("width")).value.toInt()
                        currentTileset.imageheight = startElement.getAttributeByName(QName("height")).value.toInt()
                    }

                    if (startElement.name.localPart == "objectgroup") {

                        currentLayer = Layer()
                        currentLayer.type = "objectgroup"
                        currentLayer.name = startElement.getAttributeByName(QName("name")).value



                    }

                    if (startElement.name.localPart == "object") {

                        val obj = TiledObject()
                        obj.type = startElement.getAttributeByName(QName("type"))?.value.orEmpty()
                        obj.id = startElement.getAttributeByName(QName("id")).value.toInt()
                        obj.x = startElement.getAttributeByName(QName("x")).value.toInt()
                        obj.y = startElement.getAttributeByName(QName("y")).value.toInt()
                        obj.width = startElement.getAttributeByName(QName("width"))?.value?.toInt() ?: 0
                        obj.height = startElement.getAttributeByName(QName("height"))?.value?.toInt() ?: 0

                        (currentLayer.objects as MutableList).add(obj)
                    }
                }

                // If we reach the end of an item element, we add it to the list
                if (event.isEndElement) {
                    val endElement = event.asEndElement()
                    if (endElement.name.localPart == "tileset") {
                        tilesets.add(currentTileset)
                    }

                    if (endElement.name.localPart == "layer") {
                        layers.add(currentLayer)
                    }

                    if (endElement.name.localPart == "objectgroup") {
                        layers.add(currentLayer)
                    }
                }
            }

            // DONE

            map.layers = layers
            map.tilesets = tilesets

            return map

        } catch (e: Exception) {
            throw RuntimeException("Cannot parse tmx file: $e")
        }
    }
}