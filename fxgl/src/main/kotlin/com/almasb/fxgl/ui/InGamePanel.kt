/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ui

import com.almasb.fxgl.animation.Interpolators
import com.almasb.fxgl.app.FXGL
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

        isDisable = true
    }

    fun isOpen() = open

    fun open() {
        if (open)
            return

        open = true
        isDisable = false

        val translation = TranslateTransition(Duration.seconds(0.33), this)
        with(translation) {
            interpolator = Interpolators.EXPONENTIAL.EASE_OUT()
            toX = 0.0
            play()
        }
    }

    fun close() {
        if (!open)
            return

        open = false
        isDisable = true

        val translation = TranslateTransition(Duration.seconds(0.33), this)
        with(translation) {
            interpolator = Interpolators.EXPONENTIAL.EASE_OUT()
            toX = -panelWidth - 4
            play()
        }
    }
}