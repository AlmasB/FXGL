/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.parser.tiled

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.core.math.FXGLMath
import com.almasb.fxgl.entity.view.EntityView
import com.almasb.fxgl.texture.ColoredTexture
import javafx.geometry.Rectangle2D
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import kotlin.math.max
import kotlin.math.min

/**
 * WIP: do NOT use
 *
 *
 * https://github.com/AlmasB/FXGL/issues/474
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class TiledLayerView(val map: TiledMap, val layer: Layer) : EntityView() {

    private var image: Image? = null

    private val buffer: WritableImage

    private val transparentBuffer: Image

    private var sx = 0.0
    private var sy = 0.0

    // visible box
    private val box = Box(0, 0, FXGL.getAppWidth(), FXGL.getAppHeight())

    init {
        val viewport = FXGL.getApp().gameScene.viewport

        buffer = WritableImage(box.w, box.h)
        transparentBuffer = ColoredTexture(box.w, box.h, Color.TRANSPARENT).image

        viewport.xProperty().addListener { _, _, x ->

            if (x.toInt() < 0)
                throw IllegalStateException("Background x cannot be < 0")

            sx = x.toDouble()
            redraw()
        }

        viewport.yProperty().addListener { _, _, y ->

            if (y.toInt() < 0)
                throw IllegalStateException("Background y cannot be < 0")

            sy = y.toDouble()
            redraw()
        }

        addNode(ImageView(buffer))

        redraw()
    }

    private fun redraw() {

        val area = FXGL.getApp().gameScene.viewport.visibleArea
        val writer = buffer.pixelWriter

        box.set(area)

        for (i in 0 until layer.data.size) {

            var gid = layer.data.get(i)

            // empty tile, https://github.com/AlmasB/FXGL/issues/474
//            if (gid == 0)
//                continue

            val tileset = if (gid == 0) map.tilesets[0] else findTileset(gid, map.tilesets)

            if (gid != 0) {
                // we offset because data is encoded as continuous
                gid -= tileset.firstgid
            }

            // image source
            val tilex = if (gid == 0) 0 else gid % tileset.columns
            val tiley = if (gid == 0) 0 else gid / tileset.columns

            // image destination
            val x = i % layer.width
            val y = i / layer.width

            val w = map.tilewidth
            val h = map.tileheight

            if (image == null)
                image = loadTilesetImage(tileset)

            val sourceImage = if (gid == 0) transparentBuffer else image!!

            val dstX = x * w
            val dstY = y * h

            val offsetX = sx.toInt() % w
            val offsetY = sy.toInt() % h

            try {

                if (area.contains(dstX * 1.0, dstY * 1.0) && area.contains(dstX.toDouble() + w, dstY.toDouble())
                        && area.contains(dstX.toDouble() + w, dstY.toDouble() + h) && area.contains(dstX.toDouble(), dstY.toDouble() + h)) {

                    writer.setPixels(dstX - sx.toInt(), dstY - sy.toInt(),
                            w, h, sourceImage.pixelReader,
                            tilex * w + tileset.margin + tilex * tileset.spacing,
                            tiley * h + tileset.margin + tiley * tileset.spacing)
                } else {
                    if (offsetX == 0 && offsetY == 0)
                        continue

                    if (area.contains(dstX * 1.0 + 0, dstY * 1.0 + 0)) {

                        val result = overlap(area, Rectangle2D(dstX.toDouble(), dstY.toDouble(), w.toDouble(), h.toDouble()))

                        //println(dstX - sx.toInt()) // -1
                        //println(dstY - sy.toInt()) // 0

                        writer.setPixels(dstX - sx.toInt(), dstY - sy.toInt(),
                                result.width.toInt(), result.height.toInt(), sourceImage.pixelReader,
                                tilex * w + tileset.margin + tilex * tileset.spacing,
                                tiley * h + tileset.margin + tiley * tileset.spacing)

                    } else if (area.contains(dstX * 1.0 + w - 0, dstY * 1.0 + 0)) {

                        val result = overlap(area, Rectangle2D(dstX.toDouble(), dstY.toDouble(), w.toDouble(), h.toDouble()))

                        writer.setPixels(dstX - sx.toInt() + w - result.width.toInt(), dstY - sy.toInt(),
                                result.width.toInt(), result.height.toInt(), sourceImage.pixelReader,
                                tilex * w + tileset.margin + tilex * tileset.spacing + w - result.width.toInt(),
                                tiley * h + tileset.margin + tiley * tileset.spacing)

                    } else if (area.contains(dstX * 1.0 + 0, dstY * 1.0 + h - 0)) {

                        val result = overlap(area, Rectangle2D(dstX.toDouble(), dstY.toDouble(), w.toDouble(), h.toDouble()))

                        writer.setPixels(dstX - sx.toInt(), dstY - sy.toInt() + h - result.height.toInt(),
                                result.width.toInt(), result.height.toInt(), sourceImage.pixelReader,
                                tilex * w + tileset.margin + tilex * tileset.spacing,
                                tiley * h + tileset.margin + tiley * tileset.spacing + h - result.height.toInt())

                    } else if (area.contains(dstX * 1.0 + w - 0, dstY * 1.0 + h - 0)) {

                        val result = overlap(area, Rectangle2D(dstX.toDouble(), dstY.toDouble(), w.toDouble(), h.toDouble()))

                        writer.setPixels(dstX - sx.toInt() + w - result.width.toInt(), dstY - sy.toInt() + h - result.height.toInt(),
                                result.width.toInt(), result.height.toInt(), sourceImage.pixelReader,
                                tilex * w + tileset.margin + tilex * tileset.spacing + w - result.width.toInt(),
                                tiley * h + tileset.margin + tiley * tileset.spacing + h - result.height.toInt())
                    }
                }
            } catch (e: Exception) {
                println(e.message)
            }


        }
    }

    private fun redraw2() {
        val area = FXGL.getApp().gameScene.viewport.visibleArea
        val writer = buffer.pixelWriter

        box.set(area)

        for (i in 0 until layer.data.size) {

            var gid = layer.data.get(i)

            val tileset = if (gid == 0) map.tilesets[0] else findTileset(gid, map.tilesets)

            if (gid != 0) {
                // we offset because data is encoded as continuous
                gid -= tileset.firstgid
            }

            // image source
            val tilex = if (gid == 0) 0 else gid % tileset.columns
            val tiley = if (gid == 0) 0 else gid / tileset.columns

            // image destination
            val x = i % layer.width
            val y = i / layer.width

            val w = map.tilewidth
            val h = map.tileheight

            if (image == null)
                image = loadTilesetImage(tileset)

            val sourceImage = if (gid == 0) transparentBuffer else image!!

            val dstX = x * w
            val dstY = y * h

            val offsetX = box.x % w
            val offsetY = box.y % h

            val imageBox = Box(dstX, dstY, w, h)

            try {

                if (box.overlapsFully(imageBox)) {
                    writer.setPixels(dstX - box.x, dstY - box.y,
                            w, h, sourceImage.pixelReader,
                            tilex * w + tileset.margin + tilex * tileset.spacing,
                            tiley * h + tileset.margin + tiley * tileset.spacing)
                } else {

                    if (box.contains(dstX, dstY)) {

                        val result = box.overlap(imageBox)

                        writer.setPixels(dstX - box.x, dstY - box.y,
                                result.w, result.h, sourceImage.pixelReader,
                                tilex * w + tileset.margin + tilex * tileset.spacing,
                                tiley * h + tileset.margin + tiley * tileset.spacing)

                    } else if (box.contains(dstX + w, dstY)) {

                        val result = box.overlap(imageBox)

                        writer.setPixels(dstX - box.x + w - result.w, dstY - box.y,
                                result.w, result.h, sourceImage.pixelReader,
                                tilex * w + tileset.margin + tilex * tileset.spacing + w - result.w,
                                tiley * h + tileset.margin + tiley * tileset.spacing)

                    } else if (box.contains(dstX + w, dstY + h)) {

                        val result = box.overlap(imageBox)

                        writer.setPixels(dstX - box.x + w - result.w, dstY - box.y + h - result.h,
                                result.w, result.h, sourceImage.pixelReader,
                                tilex * w + tileset.margin + tilex * tileset.spacing + w - result.w,
                                tiley * h + tileset.margin + tiley * tileset.spacing + h - result.h)

                    } else if (box.contains(dstX, dstY + h)) {

                        val result = box.overlap(imageBox)

                        writer.setPixels(dstX - box.x, dstY - box.y + h - result.h,
                                result.w, result.h, sourceImage.pixelReader,
                                tilex * w + tileset.margin + tilex * tileset.spacing,
                                tiley * h + tileset.margin + tiley * tileset.spacing + h - result.h)
                    }
                }
            } catch (e: Exception) {
                println(e.message)
            }














//            if (area.contains(dstX * 1.0, dstY * 1.0) && area.contains(dstX.toDouble() + w, dstY.toDouble())
//                    && area.contains(dstX.toDouble() + w, dstY.toDouble() + h) && area.contains(dstX.toDouble(), dstY.toDouble() + h)) {
//
//                writer.setPixels(dstX - sx.toInt(), dstY - sy.toInt(),
//                        w, h, sourceImage.pixelReader,
//                        tilex * w + tileset.margin + tilex * tileset.spacing,
//                        tiley * h + tileset.margin + tiley * tileset.spacing)
//            } else {
//                if (offsetX == 0 && offsetY == 0)
//                    continue
//
//                if (area.contains(dstX * 1.0 + 0, dstY * 1.0 + 0)) {
//
//                    val result = overlap(area, Rectangle2D(dstX.toDouble(), dstY.toDouble(), w.toDouble(), h.toDouble()))
//
//                    writer.setPixels(dstX - sx.toInt(), dstY - sy.toInt(),
//                            result.width.toInt(), result.height.toInt(), sourceImage.pixelReader,
//                            tilex * w + tileset.margin + tilex * tileset.spacing,
//                            tiley * h + tileset.margin + tiley * tileset.spacing)
//
//                } else if (area.contains(dstX * 1.0 + w - 0, dstY * 1.0 + 0)) {
//
//                    val result = overlap(area, Rectangle2D(dstX.toDouble(), dstY.toDouble(), w.toDouble(), h.toDouble()))
//
//                    writer.setPixels(dstX - sx.toInt() + w - result.width.toInt(), dstY - sy.toInt(),
//                            result.width.toInt(), result.height.toInt(), sourceImage.pixelReader,
//                            tilex * w + tileset.margin + tilex * tileset.spacing + w - result.width.toInt(),
//                            tiley * h + tileset.margin + tiley * tileset.spacing)
//
//                } else if (area.contains(dstX * 1.0 + 0, dstY * 1.0 + h - 0)) {
//
//                    val result = overlap(area, Rectangle2D(dstX.toDouble(), dstY.toDouble(), w.toDouble(), h.toDouble()))
//
//                    writer.setPixels(dstX - sx.toInt(), dstY - sy.toInt() + h - result.height.toInt(),
//                            result.width.toInt(), result.height.toInt(), sourceImage.pixelReader,
//                            tilex * w + tileset.margin + tilex * tileset.spacing,
//                            tiley * h + tileset.margin + tiley * tileset.spacing + h - result.height.toInt())
//
//                } else if (area.contains(dstX * 1.0 + w - 0, dstY * 1.0 + h - 0)) {
//
//                    val result = overlap(area, Rectangle2D(dstX.toDouble(), dstY.toDouble(), w.toDouble(), h.toDouble()))
//
//                    writer.setPixels(dstX - sx.toInt() + w - result.width.toInt(), dstY - sy.toInt() + h - result.height.toInt(),
//                            result.width.toInt(), result.height.toInt(), sourceImage.pixelReader,
//                            tilex * w + tileset.margin + tilex * tileset.spacing + w - result.width.toInt(),
//                            tiley * h + tileset.margin + tiley * tileset.spacing + h - result.height.toInt())
//                }
//            }


        }
    }

    private fun overlap(rect1: Rectangle2D, rect2: Rectangle2D): Rectangle2D {
        val x = max(rect1.minX, rect2.minX)
        val y = max(rect1.minY, rect2.minY)

        return Rectangle2D(
                x,
                y,
                min(rect1.maxX, rect2.maxX) - x,
                min(rect1.maxY, rect2.maxY) - y
                )
    }

    private fun findTileset(gid: Int, tilesets: List<Tileset>): Tileset {
        for (tileset in tilesets) {
            if (gid >= tileset.firstgid && gid < tileset.firstgid + tileset.tilecount) {
                return tileset;
            }
        }

        throw IllegalArgumentException("Tileset for gid=$gid not found");
    }

    private fun loadTilesetImage(tileset: Tileset): Image {
        var imageName = tileset.image
        imageName = imageName.substring(imageName.lastIndexOf("/") + 1)

        return if (tileset.transparentcolor.isEmpty())
            FXGL.getAssetLoader().loadTexture(imageName).image
        else
            FXGL.getAssetLoader().loadTexture(imageName,
                    Color.web(tileset.transparentcolor)).image
    }

    class Box(var x: Int, var y: Int, val w: Int, val h: Int) {

        fun set(rect: Rectangle2D) {
            x = FXGLMath.floor(rect.minX)
            y = FXGLMath.floor(rect.minY)
        }

        fun contains(x1: Int, y1: Int): Boolean {
            return x1 >= x && x1 <= x + w
                    && y1 >= y && y1 <= y + h
        }

        fun overlapsFully(box: Box): Boolean {
            return contains(box.x, box.y) &&
                    contains(box.x, box.y + box.h) &&
                    contains(box.x + w, box.y) &&
                    contains(box.x + w, box.y + box.h)
        }

        fun overlaps(box: Box): Boolean {
            return contains(box.x, box.y) ||
                    contains(box.x, box.y + box.h) ||
                    contains(box.x + w, box.y) ||
                    contains(box.x + w, box.y + box.h)
        }

        fun overlap(box: Box): Box {
            val x1 = max(x, box.x)
            val y1 = max(y, box.y)

            return Box(x1, y1,
                    min(x + w, box.x + box.w) - x1,
                    min(y + h, box.y + box.h) - y1
            )
        }
    }
}