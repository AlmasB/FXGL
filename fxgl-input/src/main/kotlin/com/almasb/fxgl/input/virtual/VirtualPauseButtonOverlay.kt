/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.input.virtual

import com.almasb.fxgl.input.Input
import javafx.scene.Parent
import javafx.scene.input.KeyCode
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.shape.Rectangle
import javafx.scene.shape.StrokeType

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class VirtualPauseButtonOverlay(private val input: Input,
                                private val menuKey: KeyCode,
                                private val isMenuEnabled: Boolean) : Parent() {

    init {
        val bg = Circle(40.0, Color.web("blue", 0.15))
        bg.strokeType = StrokeType.OUTSIDE
        bg.stroke = Color.web("blue", 0.25)
        bg.strokeWidth = 3.0
        bg.centerX = 40.0
        bg.centerY = 40.0

        val rect1 = Rectangle(10.0, 30.0, Color.WHITE)
        rect1.translateX = 25.0
        rect1.translateY = 25.0
        rect1.arcWidth = 10.0
        rect1.arcHeight = 5.0

        val rect2 = Rectangle(10.0, 30.0, Color.WHITE)
        rect2.translateX = 45.0
        rect2.translateY = 25.0
        rect2.arcWidth = 10.0
        rect2.arcHeight = 5.0

        children.addAll(bg, rect1, rect2)

        setOnMousePressed {
            if (isMenuEnabled) {
                input.mockKeyPressEvent(menuKey)
                input.mockKeyReleaseEvent(menuKey)
            } else {
                input.mockKeyPress(menuKey)
                input.mockKeyRelease(menuKey)
            }
        }
    }
}