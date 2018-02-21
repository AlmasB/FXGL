/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.parser.tiled

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.entity.view.EntityView
import com.almasb.fxgl.physics.box2d.collision.AABB
import com.almasb.fxgl.texture.Texture
import javafx.geometry.BoundingBox
import javafx.geometry.Orientation
import javafx.geometry.Rectangle2D
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.image.Image
import javafx.scene.paint.Color
import kotlin.math.max
import kotlin.math.min

/**
 * TODO: multiple layers
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class TiledLayerView(val map: TiledMap, val layer: Layer) : EntityView() {

    private val canvas: Canvas
    private val g: GraphicsContext

    private var image: Image? = null

    private var sx = 0.0
    private var sy = 0.0

    init {
        val viewport = FXGL.getApp().gameScene.viewport

        canvas = Canvas(viewport.width, viewport.height)
        g = canvas.graphicsContext2D



        viewport.xProperty().addListener { _, _, x ->

            if (x.toInt() < 0)
                throw IllegalStateException("Background x cannot be < 0")

            //sx = (x.toDouble()) % image.width
            sx = x.toDouble()
            redraw()
        }

        //translateXProperty().bind()

        viewport.yProperty().addListener { _, _, y ->

            if (y.toInt() < 0)
                throw IllegalStateException("Background y cannot be < 0")

            //sy = (y.toDouble()) % image.height
            sy = y.toDouble()
            redraw()
        }

        //translateYProperty().bind(viewport.yProperty())




        addNode(canvas)

        redraw()
    }

    private fun ddd() {

        //println(sx)

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

            if (image == null)
                image = loadTilesetImage(tileset)

            val sourceImage = image!!

            val dstX = x * w
            val dstY = y * h

            val offsetX = sx.toInt() % w
            val offsetY = sy.toInt() % h

            val area = FXGL.getApp().gameScene.viewport.visibleArea

            //println(area)


            if (area.contains(dstX * 1.0, dstY * 1.0) && area.contains(dstX.toDouble() + w, dstY.toDouble())
                    && area.contains(dstX.toDouble() + w, dstY.toDouble() + h) && area.contains(dstX.toDouble(), dstY.toDouble() + h)) {

                //g.drawImage()
                //println("" + dstX + " $dstY")

                g.getPixelWriter().setPixels(dstX - sx.toInt(), dstY - sy.toInt(),
                        w, h, sourceImage.getPixelReader(),
                        tilex * w + tileset.margin + tilex * tileset.spacing,
                        tiley * h + tileset.margin + tiley * tileset.spacing)
            } else {
                if (offsetX == 0 && offsetY == 0)
                    continue


                // area.contains(dstX.toDouble() + w - 1, dstY.toDouble()) || area.contains(dstX.toDouble() + w - 1, dstY.toDouble() + h - 1) || area.contains(dstX.toDouble(), dstY.toDouble() + h - 1)

                if (area.contains(dstX * 1.0 + 1, dstY * 1.0 + 1)) {

                    val result = overlap(area, Rectangle2D(dstX.toDouble(), dstY.toDouble(), w.toDouble(), h.toDouble()))

                    g.getPixelWriter().setPixels(dstX - sx.toInt(), dstY - sy.toInt(),
                            result.width.toInt(), result.height.toInt(), sourceImage.getPixelReader(),
                            tilex * w + tileset.margin + tilex * tileset.spacing,
                            tiley * h + tileset.margin + tiley * tileset.spacing)

                } else if (area.contains(dstX * 1.0 + w - 1, dstY * 1.0 + 1)) {

                    val result = overlap(area, Rectangle2D(dstX.toDouble(), dstY.toDouble(), w.toDouble(), h.toDouble()))

                    g.getPixelWriter().setPixels(dstX - sx.toInt() + w - result.width.toInt(), dstY - sy.toInt(),
                            result.width.toInt(), result.height.toInt(), sourceImage.getPixelReader(),
                            tilex * w + tileset.margin + tilex * tileset.spacing + w - result.width.toInt(),
                            tiley * h + tileset.margin + tiley * tileset.spacing)



                } else if (area.contains(dstX * 1.0 + 1, dstY * 1.0 + h - 1)) {

                    val result = overlap(area, Rectangle2D(dstX.toDouble(), dstY.toDouble(), w.toDouble(), h.toDouble()))

                    g.getPixelWriter().setPixels(dstX - sx.toInt(), dstY - sy.toInt() + h - result.height.toInt(),
                            result.width.toInt(), result.height.toInt(), sourceImage.getPixelReader(),
                            tilex * w + tileset.margin + tilex * tileset.spacing,
                            tiley * h + tileset.margin + tiley * tileset.spacing + h - result.height.toInt())



                } else if (area.contains(dstX * 1.0 + w - 1, dstY * 1.0 + h - 1)) {

                    val result = overlap(area, Rectangle2D(dstX.toDouble(), dstY.toDouble(), w.toDouble(), h.toDouble()))

                    g.getPixelWriter().setPixels(dstX - sx.toInt() + w - result.width.toInt(), dstY - sy.toInt() + h - result.height.toInt(),
                            result.width.toInt(), result.height.toInt(), sourceImage.getPixelReader(),
                            tilex * w + tileset.margin + tilex * tileset.spacing + w - result.width.toInt(),
                            tiley * h + tileset.margin + tiley * tileset.spacing + h - result.height.toInt())
                }
            }






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
        return tilesets.stream()
                .filter { tileset -> gid >= tileset.firstgid && gid < tileset.firstgid + tileset.tilecount }
                .findAny()
                .orElseThrow { IllegalArgumentException("Tileset for gid=$gid not found") }
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

    private fun redraw() {
        var start = System.nanoTime()

        g.clearRect(0.0, 0.0, canvas.width, canvas.height)

        //println("Clear took: " + (System.nanoTime() - start) / 1000000000.0)

        start = System.nanoTime()
        ddd()

        println("Draw took: " + (System.nanoTime() - start) / 1000000000.0)
//        redrawX()
//        redrawY()
    }

//    private fun redrawX() {
//        var w = canvas.width
//        val h = canvas.height
//
//        val overflowX = sx + w > image.width
//
//        if (overflowX) {
//            w = image.width - sx
//        }
//
//        g.drawImage(image, sx, sy, w, h,
//                0.0, 0.0, w, h)
//
//        if (overflowX) {
//            g.drawImage(image, 0.0, 0.0, canvas.width - w, h,
//                    w, 0.0, canvas.width - w, h)
//        }
//    }
//
//    private fun redrawY() {
//        val w = canvas.width
//        var h = canvas.height
//
//        val overflowY = sy + h > image.height
//
//        if (overflowY) {
//            h = image.height - sy
//        }
//
//        g.drawImage(image, sx, sy, w, h,
//                0.0, 0.0, w, h)
//
//        if (overflowY) {
//            g.drawImage(image, 0.0, 0.0, w, canvas.height - h,
//                    0.0, h, w, canvas.height - h)
//        }
//    }
}