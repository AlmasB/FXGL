/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.level.tiled

/**
 * Specification: https://doc.mapeditor.org/en/stable/reference/tmx-map-format/#tmx-tileset-tile
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
data class Tileset(
        var firstgid: Int = 0,
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
        var transparentcolor: String = "",
        var tiles: MutableList<Tile> = arrayListOf()
) {

    /**
     * @return true if tileset is a sprite sheet, false if tileset is a collection of images
     */
    val isSpriteSheet: Boolean
        get() = tiles.isEmpty()
}

data class Tile(
        var id: Int = 0,
        var image: String = "",
        var imagewidth: Int = 0,
        var imageheight: Int = 0,
        var transparentcolor: String = ""
)