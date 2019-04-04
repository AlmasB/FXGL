/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ui

import com.almasb.fxgl.animation.Interpolators
import javafx.animation.TranslateTransition
import javafx.geometry.HorizontalDirection
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.util.Duration

/**
 * An in-game semi-transparent panel with in/out animations.
 * Can be used to display various UI elements, e.g. game score, inventory,
 * player stats, etc.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class InGamePanel(val panelWidth: Double,
                  val panelHeight: Double,
                  val direction: HorizontalDirection = HorizontalDirection.LEFT) : Pane() {

    var isOpen = false
        private set

    init {
        translateX = -panelWidth - 4

        val bg = Rectangle(panelWidth, panelHeight, Color.color(0.0, 0.0, 0.0, 0.85))

        children += bg

        isDisable = true
    }

    fun open() {
        if (isOpen)
            return

        isOpen = true
        isDisable = false

        val translation = TranslateTransition(Duration.seconds(0.33), this)
        with(translation) {
            interpolator = Interpolators.EXPONENTIAL.EASE_OUT()
            toX = 0.0
            play()
        }
    }

    fun close() {
        if (!isOpen)
            return

        isOpen = false
        isDisable = true

        val translation = TranslateTransition(Duration.seconds(0.33), this)
        with(translation) {
            interpolator = Interpolators.EXPONENTIAL.EASE_OUT()
            toX = -panelWidth - 4
            play()
        }
    }
}