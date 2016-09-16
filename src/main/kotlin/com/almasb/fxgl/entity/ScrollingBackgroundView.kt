/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2016 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.almasb.fxgl.entity

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.texture.Texture
import javafx.geometry.Orientation
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.image.Image

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class ScrollingBackgroundView
@JvmOverloads constructor(texture: Texture,
                          val orientation: Orientation = Orientation.HORIZONTAL,
                          renderLayer: RenderLayer = RenderLayer.BACKGROUND) : EntityView(renderLayer) {

    private val canvas: Canvas
    private val g: GraphicsContext

    private val image: Image

    private var sx = 0.0
    private var sy = 0.0

    init {
        image = texture.image

        val viewport = FXGL.getApp().gameScene.viewport

        canvas = Canvas(viewport.width, viewport.height)
        g = canvas.graphicsContext2D

        if (orientation == Orientation.HORIZONTAL) {
            translateXProperty().addListener { obs, old, x ->

                if (x.toInt() < 0)
                    throw IllegalStateException("Background x cannot be < 0")

                sx = x.toDouble() % image.width
                redraw()
            }

            translateXProperty().bind(viewport.xProperty())
        } else {
            translateYProperty().addListener { obs, old, y ->

                if (y.toInt() < 0)
                    throw IllegalStateException("Background y cannot be < 0")

                sy = y.toDouble() % image.height
                redraw()
            }

            translateYProperty().bind(viewport.yProperty())
        }

        addNode(canvas)

        redraw()

        //FXGL.getMasterTimer().addUpdateListener { redraw() }
    }

    private fun redraw() {
        println("redraw $sx $sy")

        g.clearRect(0.0, 0.0, canvas.width, canvas.height)

        if (orientation == Orientation.HORIZONTAL) {
            redrawX()
        } else {
            redrawY()
        }

//        var w = canvas.width
//        var h = canvas.height
//
//        val overflowX = sx + w > image.width
//        val overflowY = sy + h > image.height
//
//        if (overflowX) {
//            w = image.width - sx
//        }
//
//        if (overflowY) {
//            h = image.height - sy
//        }
//
//        g.drawImage(image, sx, sy, w, h,
//                0.0, 0.0, w, h)
//
//        if (overflowX || overflowY) {
//            g.drawImage(image, 0.0, 0.0, canvas.width - w, canvas.height - h,
//                    w, h, canvas.width - w, canvas.height - h)
//        }
    }

    private fun redrawX() {
        var w = canvas.width
        var h = canvas.height

        val overflowX = sx + w > image.width

        if (overflowX) {
            w = image.width - sx
        }

        g.drawImage(image, sx, sy, w, h,
                0.0, 0.0, w, h)

        if (overflowX) {
            g.drawImage(image, 0.0, 0.0, canvas.width - w, h,
                    w, 0.0, canvas.width - w, h)
        }
    }

    private fun redrawY() {
        var w = canvas.width
        var h = canvas.height

        val overflowY = sy + h > image.height

        if (overflowY) {
            h = image.height - sy
        }

        g.drawImage(image, sx, sy, w, h,
                0.0, 0.0, w, h)

        if (overflowY) {
            g.drawImage(image, 0.0, 0.0, w, canvas.height - h,
                    0.0, h, w, canvas.height - h)
        }
    }
}