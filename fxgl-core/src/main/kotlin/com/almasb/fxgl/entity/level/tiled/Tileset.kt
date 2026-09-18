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
     * Tilesets are either a sprite sheet (one atlas image) or a collection of images
     * (each [Tile] owns its image). Tiled still emits `<tile>` entries on sprite sheets
     * when tiles have custom properties; those metadata-only entries do not replace the
     * tileset image. Classification therefore follows the independently parsed tileset
     * image source, not whether [tiles] is empty. [image] is a non-null [String], so
     * emptiness is tested with [String.isNotEmpty] rather than a null check.
     *
     * @return true if tileset is a sprite sheet, false if tileset is a collection of images
     */
    val isSpriteSheet: Boolean
        get() = image.isNotEmpty()
}

data class Tile(
        var id: Int = 0,
        var image: String = "",
        var imagewidth: Int = 0,
        var imageheight: Int = 0,
        var transparentcolor: String = ""
)