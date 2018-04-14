/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.view

import com.almasb.fxgl.app.FXGL
import javafx.geometry.Orientation
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class ParallaxBackgroundView
@JvmOverloads constructor(private val textures: List<ParallaxTexture>,
                          val orientation: Orientation = Orientation.HORIZONTAL) : EntityView() {

    private val canvas: Canvas
    private val g: GraphicsContext

    init {
        val viewport = FXGL.getApp().gameScene.viewport

        canvas = Canvas(viewport.width, viewport.height)
        g = canvas.graphicsContext2D

        if (orientation == Orientation.HORIZONTAL) {
            translateXProperty().addListener { _, _, x ->

                if (x.toInt() < 0)
                    throw IllegalStateException("Background x cannot be < 0")

                textures.forEach { it.sx = x.toDouble() * it.speed % it.texture.image.width }

                redraw()
            }

            translateXProperty().bind(viewport.xProperty())
        } else {
            translateYProperty().addListener { _, _, y ->

                if (y.toInt() < 0)
                    throw IllegalStateException("Background y cannot be < 0")

                textures.forEach { it.sy = y.toDouble() * it.speed % it.texture.image.height }

                redraw()
            }

            translateYProperty().bind(viewport.yProperty())
        }

        addNode(canvas)

        redraw()
    }

    private fun redraw() {
        g.clearRect(0.0, 0.0, canvas.width, canvas.height)

        if (orientation == Orientation.HORIZONTAL) {
            redrawX()
        } else {
            redrawY()
        }
    }

    private fun redrawX() {
        textures.forEach {
            var w = canvas.width
            val h = canvas.height

            val overflowX = it.sx + w > it.image.width

            if (overflowX) {
                w = it.image.width - it.sx
            }

            g.drawImage(it.image, it.sx, it.sy, w, h,
                    0.0, 0.0, w, h)

            if (overflowX) {
                g.drawImage(it.image, 0.0, 0.0, canvas.width - w, h,
                        w, 0.0, canvas.width - w, h)
            }
        }
    }

    private fun redrawY() {
        textures.forEach {
            val w = canvas.width
            var h = canvas.height

            val overflowY = it.sy + h > it.image.height

            if (overflowY) {
                h = it.image.height - it.sy
            }

            g.drawImage(it.image, it.sx, it.sy, w, h,
                    0.0, 0.0, w, h)

            if (overflowY) {
                g.drawImage(it.image, 0.0, 0.0, w, canvas.height - h,
                        0.0, h, w, canvas.height - h)
            }
        }
    }
}