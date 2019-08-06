/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.level.tiled

/**
 * Specification: https://github.com/bjorn/tiled/wiki/JSON-Map-Format
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
data class Tileset(var firstgid: Int = 0,
              var image: String = "",
              var name: String = "",
              var tilewidth: Int = 0,
              var tileheight: Int = 0,
              var imagewidth: Int = 0,
              var imageheight: Int = 0,
              var margin: Int = 0,
              var spacing: Int = 0,
              var columns: Int = 0,
              var tilecount: Int = 0,
              var transparentcolor: String = "") {
}