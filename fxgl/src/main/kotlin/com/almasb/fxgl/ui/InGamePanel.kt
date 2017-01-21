/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
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

package com.almasb.fxgl.ui

import com.almasb.fxgl.app.FXGL
import javafx.animation.Interpolator
import javafx.animation.TranslateTransition
import javafx.geometry.HorizontalDirection
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.shape.StrokeLineCap
import javafx.scene.shape.StrokeLineJoin
import javafx.scene.shape.StrokeType
import javafx.util.Duration

/**
 * An in-game semi-transparent panel with in/out animations.
 * Can be used to display various UI elements, e.g. game score, inventory,
 * player stats, etc.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class InGamePanel(val direction: HorizontalDirection = HorizontalDirection.LEFT) : Pane() {

    private val panelWidth: Double
    private val panelHeight: Double

    private var open = false

    init {
        panelWidth = FXGL.getSettings().width.toDouble() / 3
        panelHeight = FXGL.getSettings().height.toDouble() - 8

        val outerBorder = Rectangle(panelWidth, panelHeight,
                Color.color(0.5, 0.5, 0.5, 0.5)
        )
        with(outerBorder) {
            strokeWidth = 4.0
            stroke = Color.color(0.2, 0.2, 0.2, 0.7)
            strokeLineCap = StrokeLineCap.BUTT
            strokeLineJoin = StrokeLineJoin.ROUND
            strokeType = StrokeType.OUTSIDE
        }

        translateX = -panelWidth - 4
        translateY = 4.0
        children.addAll(outerBorder)
    }

    fun isOpen() = open

    fun open() {
        if (open)
            return

        open = true

        val translation = TranslateTransition(Duration.seconds(0.33), this)
        with(translation) {
            interpolator = Interpolator.EASE_BOTH
            toX = 0.0
            play()
        }
    }

    fun close() {
        if (!open)
            return

        open = false

        val translation = TranslateTransition(Duration.seconds(0.33), this)
        with(translation) {
            interpolator = Interpolator.EASE_BOTH
            toX = -panelWidth - 4
            play()
        }
    }
}