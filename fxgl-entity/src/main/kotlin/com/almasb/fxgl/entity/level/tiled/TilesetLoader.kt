/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.level.tiled

import com.almasb.fxgl.texture.Texture
import com.almasb.sslogger.Logger
import javafx.scene.Node
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import java.net.URL

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class TilesetLoader(private val map: TiledMap, private val mapURL: URL) {

    private val log = Logger.get<TilesetLoader>()

    private val imageCache = hashMapOf<String, Image>()

    fun loadView(gidArg: Int, isFlippedHorizontal: Boolean, isFlippedVertical: Boolean): Node {
        var gid = gidArg

        val tileset = findTileset(gid, map.tilesets)

        // we offset because data is encoded as continuous
        gid -= tileset.firstgid

        // image source
        val tilex = gid % tileset.columns
        val tiley = gid / tileset.columns

        val w = map.tilewidth
        val h = map.tileheight

        val buffer = WritableImage(w, h)

        val sourceImage = loadTilesetImage(tileset)

        buffer.pixelWriter.setPixels(0, 0,
                w, h, sourceImage.pixelReader,
                tilex * w + tileset.margin + tilex * tileset.spacing,
                tiley * h + tileset.margin + tiley * tileset.spacing)

        return ImageView(buffer).also {
            it.scaleX = if (isFlippedHorizontal) -1.0 else 1.0
            it.scaleY = if (isFlippedVertical) -1.0 else 1.0
        }
    }

    fun loadView(layerName: String): Node {
        log.debug("Loading view for layer $layerName")

        val layer = map.getLayerByName(layerName)

        val buffer = WritableImage(
                layer.width * map.tilewidth,
                layer.height * map.tileheight
        )

        log.debug("Created buffer with size ${buffer.width}x${buffer.height}")

        for (i in 0 until layer.data.size) {

            var gid = layer.data.get(i)

            // empty tile
            if (gid == 0)
                continue

            val tileset = findTileset(gid, map.tilesets)

            // we offset because data is encoded as continuous
            gid -= tileset.firstgid

            // image source
            val tilex = gid % tileset.columns
            val tiley = gid / tileset.columns

            // image destination
            val x = i % layer.width
            val y = i / layer.width

            val w = map.tilewidth
            val h = map.tileheight

            val sourceImage = loadTilesetImage(tileset)

            val srcx = tilex * w + tileset.margin + tilex * tileset.spacing
            val srcy = tiley * h + tileset.margin + tiley * tileset.spacing

            log.debug("Writing to buffer: dst=${x*w},${y*h}, w=$w,h=$h, src=$srcx,$srcy")

            buffer.pixelWriter.setPixels(x * w, y * h,
                    w, h, sourceImage.pixelReader,
                    srcx,
                    srcy)
        }

        return ImageView(buffer)
    }

    /**
     * Finds tileset where gid is located.
     *
     * @param gid tile id
     * @param tilesets all tilesets
     * @return tileset
     */
    private fun findTileset(gid: Int, tilesets: List<Tileset>): Tileset {
        for (tileset in tilesets) {
            if (gid >= tileset.firstgid && gid < tileset.firstgid + tileset.tilecount) {
                return tileset
            }

        }
        throw IllegalArgumentException("Tileset for gid=$gid not found")
    }

    private fun loadTilesetImage(tileset: Tileset): Image {
        var imageName = tileset.image
        imageName = imageName.substring(imageName.lastIndexOf("/") + 1)

        if (imageName in imageCache) {
            return imageCache[imageName]!!
        }

        val ext = mapURL.toExternalForm().substringBeforeLast("/") + "/"

        val image = if (tileset.transparentcolor.isEmpty())
            Image(ext + imageName)
        else
            Texture(Image(ext + imageName)).transparentColor(Color.web(tileset.transparentcolor)).image

        if (image.isError)
            throw IllegalArgumentException("${ext + imageName} cannot be loaded")

        imageCache[imageName] = image

        return image
    }
}