/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.level.tiled

import com.almasb.fxgl.texture.Texture
import com.almasb.fxgl.texture.getDummyImage
import com.almasb.fxgl.texture.resize
import com.almasb.sslogger.Logger
import javafx.scene.Node
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import java.lang.Exception
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

        val w = map.tilewidth
        val h = map.tileheight

        val buffer = WritableImage(w, h)

        val sourceImage: Image
        val srcx: Int
        val srcy: Int

        if (tileset.isSpriteSheet) {
            // image source
            val tilex = gid % tileset.columns
            val tiley = gid / tileset.columns

            sourceImage = loadImage(tileset.image, tileset.transparentcolor, tileset.imagewidth, tileset.imageheight)

            srcx = tilex * w + tileset.margin + tilex * tileset.spacing
            srcy = tiley * h + tileset.margin + tiley * tileset.spacing
        } else {
            // tileset is a collection of images
            val tile = tileset.tiles.find { it.id == gid }
                    ?: throw IllegalArgumentException("Tile with id=$gid not found")

            sourceImage = loadImage(tile.image, tile.transparentcolor, tile.imagewidth, tile.imageheight)

            srcx = 0
            srcy = 0
        }

        buffer.pixelWriter.setPixels(0, 0,
                w, h, sourceImage.pixelReader,
                srcx,
                srcy)

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

            // image destination
            val x = i % layer.width
            val y = i / layer.width

            val w = map.tilewidth
            val h = map.tileheight

            val sourceImage: Image
            val srcx: Int
            val srcy: Int

            if (tileset.isSpriteSheet) {
                sourceImage = loadImage(tileset.image, tileset.transparentcolor, tileset.imagewidth, tileset.imageheight)

                // image source
                val tilex = gid % tileset.columns
                val tiley = gid / tileset.columns

                srcx = tilex * w + tileset.margin + tilex * tileset.spacing
                srcy = tiley * h + tileset.margin + tiley * tileset.spacing
            } else {

                // tileset is a collection of images
                val tile = tileset.tiles.find { it.id == gid }
                        ?: throw IllegalArgumentException("Tile with id=$gid not found")

                sourceImage = loadImage(tile.image, tile.transparentcolor, tile.imagewidth, tile.imageheight)

                srcx = 0
                srcy = 0
            }

            log.debug("Writing to buffer: dst=${x * w},${y * h}, w=$w,h=$h, src=$srcx,$srcy")

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

    private fun loadImage(tilesetImageName: String, transparentcolor: String, w: Int, h: Int): Image {
        val imageName = tilesetImageName.substring(tilesetImageName.lastIndexOf("/") + 1)

        if (imageName in imageCache) {
            return imageCache[imageName]!!
        }

        val image = try {
            val ext = mapURL.toExternalForm().substringBeforeLast("/") + "/"

            val stream = URL(ext + imageName).openStream()

            var img = if (transparentcolor.isEmpty())
                Image(stream)
            else
                Texture(Image(stream)).transparentColor(Color.web(transparentcolor)).image

            stream.close()

            if (img.isError) {
                log.warning("${ext + imageName} cannot be loaded")
                img = resize(getDummyImage(), w, h)
            }

            img

        } catch (e: Exception) {
            log.warning("$imageName cannot be loaded using mapURL=$mapURL", e)

            resize(getDummyImage(), w, h)
        }

        imageCache[imageName] = image

        return image
    }
}