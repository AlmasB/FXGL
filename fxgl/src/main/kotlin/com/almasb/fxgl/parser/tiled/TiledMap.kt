/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.parser.tiled

/**
 * JSON map format from the Tiled map editor.
 * Specification: https://github.com/bjorn/tiled/wiki/JSON-Map-Format
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class TiledMap(var width: Int = 0,
               var height: Int = 0,
               var tilewidth: Int = 0,
               var tileheight: Int = 0,
               var orientation: String = "",
               var layers: List<Layer> = arrayListOf(),
               var tilesets: List<Tileset> = arrayListOf(),
               var backgroundcolor: String = "",
               var renderorder: String = "",
               var nextobjectid: Int = 0,
               var version: Int = 0) {

    fun getLayerByName(name: String) = layers.filter { it.name == name }.firstOrNull()
            ?: throw IllegalArgumentException("Layer with name=$name not found")
}