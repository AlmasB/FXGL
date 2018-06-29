/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.parser.tiled

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

/**
 * JSON map format from the Tiled map editor.
 * Tiled version: 1.0.1
 * Specification: http://docs.mapeditor.org/en/latest/reference/json-map-format/
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@JsonIgnoreProperties(ignoreUnknown = true)
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
               var version: Int = 0,
               var tiledversion: String = "",
               var type: String = "",
               var infinite: Boolean = false) {

    fun getLayerByName(name: String) = layers.firstOrNull { it.name == name }
            ?: throw IllegalArgumentException("Layer with name=$name not found")
}