/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.texture

import com.almasb.fxgl.core.View
import javafx.geometry.Orientation
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.canvas.Canvas
import javafx.scene.image.Image

/**
 * View that that can be infinitely scrolled either horizontally or vertically.
 * Useful for side-scrolling backgrounds.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
open class ScrollingView
@JvmOverloads constructor(

        /**
         * The full image to be used for scrolling over.
         */
        private val image: Image,

        /**
         * Width of this view.
         */
        viewWidth: Double = image.width,

        /**
         * Height of this view.
         */
        viewHeight: Double = image.height,

        /**
         * The direction of scroll.
         */
        private val orientation: Orientation = Orientation.HORIZONTAL,

) : Parent(), View {

    private val canvas = Canvas(viewWidth, viewHeight)
    private val g = canvas.graphicsContext2D

    var scrollX = 0.0
        set(value) {
            field = value
            redraw()
        }

    var scrollY = 0.0
        set(value) {
            field = value
            redraw()
        }

    init {
        children += canvas

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
        var w = canvas.width
        val h = canvas.height

        val overflowX = scrollX + w > image.width

        if (overflowX) {
            w = image.width - scrollX
        }

        g.drawImage(image, scrollX, scrollY, w, h,
                0.0, 0.0, w, h)

        if (overflowX) {
            g.drawImage(image, 0.0, 0.0, canvas.width - w, h,
                    w, 0.0, canvas.width - w, h)
        }
    }

    private fun redrawY() {
        val w = canvas.width
        var h = canvas.height

        val overflowY = scrollY + h > image.height

        if (overflowY) {
            h = image.height - scrollY
        }

        g.drawImage(image, scrollX, scrollY, w, h,
                0.0, 0.0, w, h)

        if (overflowY) {
            g.drawImage(image, 0.0, 0.0, w, canvas.height - h,
                    0.0, h, w, canvas.height - h)
        }
    }

    override fun onUpdate(tpf: Double) { }

    override fun getNode(): Node {
        return this
    }

    override fun dispose() { }
}
