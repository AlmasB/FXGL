/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.level.tiled

import com.almasb.fxgl.logging.Logger
import com.almasb.fxgl.texture.Texture
import com.almasb.fxgl.texture.getDummyImage
import com.almasb.fxgl.texture.resize
import com.almasb.fxgl.texture.flipHorizontally
import com.almasb.fxgl.texture.flipVertically
import javafx.geometry.Rectangle2D
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

        val w = tileset.tilewidth
        val h = tileset.tileheight

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

            val tempGid = layer.data.get(i)

            // from https://doc.mapeditor.org/en/stable/reference/tmx-map-format/#tile-flipping
            // Bit 32 (31th) is used for storing whether the tile is horizontally flipped,
            // Bit 31 (30th) is used for vertically flipped,
            // Bit 30 (29th) is used for diagonally flipped
            val FLIPPED_HORIZONTALLY_FLAG = 1L shl 31
            val FLIPPED_VERTICALLY_FLAG   = 1L shl 30
            val FLIPPED_DIAGONALLY_FLAG   = 1L shl 29

            val isFlippedHorizontal = tempGid and FLIPPED_HORIZONTALLY_FLAG != 0L
            val isFlippedVertical = tempGid and FLIPPED_VERTICALLY_FLAG != 0L
            val isFlippedDiagonal = tempGid and FLIPPED_DIAGONALLY_FLAG != 0L

            // get rid of the metadata, leaving us with gid
            var gid = (tempGid and (FLIPPED_HORIZONTALLY_FLAG or FLIPPED_VERTICALLY_FLAG or FLIPPED_DIAGONALLY_FLAG).inv()).toInt()

            // empty tile
            if (gid == 0)
                continue

            val tileset = findTileset(gid, map.tilesets)

            // we offset because data is encoded as continuous
            gid -= tileset.firstgid

            // image destination
            val x = i % layer.width
            val y = i / layer.width

            val w = tileset.tilewidth
            val h = tileset.tileheight

            var sourceImage: Image
            var srcx: Int
            var srcy: Int

            if (tileset.isSpriteSheet) {
                sourceImage = loadImage(tileset.image, tileset.transparentcolor, tileset.imagewidth, tileset.imageheight)

                // image source
                val tilex = gid % tileset.columns
                val tiley = gid / tileset.columns

                srcx = tilex * w + tileset.margin + tilex * tileset.spacing
                srcy = tiley * h + tileset.margin + tiley * tileset.spacing

                // If a tile of the sprite sheet needs to be flipped, crop the sub-texture
                if (isFlippedHorizontal or isFlippedVertical or isFlippedDiagonal) {
                    sourceImage = Texture(sourceImage).subTexture(Rectangle2D(srcx.toDouble(), srcy.toDouble(), w.toDouble(), h.toDouble())).image
                    srcx = 0
                    srcy = 0
                }

            } else {

                // tileset is a collection of images
                val tile = tileset.tiles.find { it.id == gid }
                        ?: throw IllegalArgumentException("Tile with id=$gid not found")

                sourceImage = loadImage(tile.image, tile.transparentcolor, tile.imagewidth, tile.imageheight)

                srcx = 0
                srcy = 0
            }

            if (isFlippedHorizontal) {
                sourceImage = flipHorizontally(sourceImage)
            }

            if (isFlippedVertical) {
                sourceImage = flipVertically(sourceImage)
            }

            if (isFlippedDiagonal) {
                log.warning("Diagonally flipped tiles are not currently supported")
            }

            buffer.pixelWriter.setPixels(x * map.tilewidth, y * map.tileheight,
                    w, h, sourceImage.pixelReader,
                    srcx,
                    srcy)
        }

        return ImageView(buffer)
    }

    // NOT FULLY IMPLEMENTED, see https://github.com/AlmasB/FXGL/issues/702
    fun loadViewHex(layerName: String): Node {
        log.debug("Loading view for layer $layerName")

        val layer = map.getLayerByName(layerName)

        val bufferBottom = WritableImage(
                layer.width * map.tilewidth + 100,
                layer.height * map.tileheight + 100
        )

        log.debug("Created buffer with size ${bufferBottom.width}x${bufferBottom.height}")

        val delayedDrawings = arrayListOf<Runnable>()

        var oldY = 0

        for (i in 0 until layer.data.size) {

            var gid = layer.data.get(i).toInt()

            // empty tile
            if (gid == 0)
                continue

            val tileset = findTileset(gid, map.tilesets)

            // we offset because data is encoded as continuous
            gid -= tileset.firstgid

            // image destination
            val x = i % layer.width
            val y = i / layer.width

            val isColumnEven = x % 2 == 0

            val w = tileset.tilewidth
            val h = tileset.tileheight

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

            log.debug("Writing to buffer: dst=${x * map.tilewidth},${y * map.tileheight}, w=$w,h=$h, src=$srcx,$srcy")

            val offsetX = -(map.hexsidelength + 6) * x

            val offsetY = if (map.staggerindex == "even" && isColumnEven
                    || map.staggerindex == "odd" && !isColumnEven)
                map.tileheight / 2
            else
                0

//            buffer.pixelWriter.setPixels(x * map.tilewidth + offsetX, y * map.tileheight + offsetY,
//                    w, h, sourceImage.pixelReader,
//                    srcx,
//                    srcy)

            if (y > oldY) {
                delayedDrawings.forEach { it.run() }
                delayedDrawings.clear()

                oldY = y
            }

            if (map.staggerindex == "odd" && isColumnEven
                    || map.staggerindex == "even" && !isColumnEven) {

                for (dy in 0 until h) {
                    for (dx in 0 until w) {
                        val c = sourceImage.pixelReader.getColor(srcx + dx, srcy + dy)

                        if (c != Color.TRANSPARENT) {
                            bufferBottom.pixelWriter.setColor(
                                    x * map.tilewidth + offsetX + dx,
                                    y * map.tileheight + offsetY + dy,
                                    c
                            )
                        }
                    }
                }
            } else {

                // we need delayed drawings to correctly draw shadows
                delayedDrawings += Runnable {
                    for (dy in 0 until h) {
                        for (dx in 0 until w) {
                            val c = sourceImage.pixelReader.getColor(srcx + dx, srcy + dy)

                            if (c != Color.TRANSPARENT) {
                                bufferBottom.pixelWriter.setColor(
                                        x * map.tilewidth + offsetX + dx,
                                        y * map.tileheight + offsetY + dy,
                                        c
                                )
                            }
                        }
                    }
                }
            }
        }

        return ImageView(bufferBottom)
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

    fun copy(): TilesetLoader = TilesetLoader(map, mapURL)
}