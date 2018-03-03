/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.input.view

import javafx.scene.input.MouseButton
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.paint.CycleMethod
import javafx.scene.paint.LinearGradient
import javafx.scene.paint.Stop
import javafx.scene.shape.Line
import javafx.scene.shape.Rectangle

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class MouseButtonView
@JvmOverloads constructor(button: MouseButton,
                          color: Color = Color.ORANGE,
                          size: Double = 24.0) : Pane() {

    init {
        val border = Rectangle(size, size * 1.5)
        border.fill = Color.BLACK
        border.stroke = color
        border.strokeWidth = size / 7
        border.arcWidth = size / 1.5
        border.arcHeight = size / 1.5

        val borderTop = Rectangle(size, size * 1.5)
        borderTop.fill = null
        borderTop.stroke = color
        borderTop.strokeWidth = size / 7
        borderTop.arcWidth = size / 1.5
        borderTop.arcHeight = size / 1.5

        val line1 = Line(size / 2, 0.0, size / 2, size / 5)
        line1.stroke = color
        line1.strokeWidth = size / 7

        val ellipse = Rectangle(size / 6, size / 6 * 1.5)
        ellipse.fill = null
        ellipse.stroke = color
        ellipse.strokeWidth = size / 10
        ellipse.arcWidth = size / 1.5
        ellipse.arcHeight = size / 1.5
        ellipse.translateX = size / 2 - size / 6 / 2
        ellipse.translateY = size / 5

        val line2 = Line(size / 2, size / 5 * 2.75, size / 2, size / 5 * 5)
        line2.stroke = LinearGradient(0.5, 0.0, 0.5, 1.0, true, CycleMethod.NO_CYCLE, Stop(0.0, color), Stop(0.75, Color.BLACK))
        line2.strokeWidth = size / 7

        children.addAll(border, line1, line2, ellipse, borderTop)

        when(button) {
            MouseButton.PRIMARY -> {
                val highlight = Rectangle(size / 2.5, size / 6 * 3.5)
                highlight.fill = LinearGradient(0.5, 0.0, 0.5, 1.0, true, CycleMethod.NO_CYCLE, Stop(0.0, Color.BLACK), Stop(0.25, color), Stop(0.8, color), Stop(0.9, Color.BLACK))
                highlight.arcWidth = size / 4
                highlight.arcHeight = size / 4
                highlight.translateX = size / 20
                highlight.translateY = size / 8

                children.add(1, highlight)
            }

            MouseButton.SECONDARY -> {
                val highlight = Rectangle(size / 2.5, size / 6 * 3.5)
                highlight.fill = LinearGradient(0.5, 0.0, 0.5, 1.0, true, CycleMethod.NO_CYCLE, Stop(0.0, Color.BLACK), Stop(0.25, color), Stop(0.8, color), Stop(0.9, Color.BLACK))
                highlight.arcWidth = size / 4
                highlight.arcHeight = size / 4
                highlight.translateX = size - size / 20 - highlight.width
                highlight.translateY = size / 8

                children.add(1, highlight)
            }

            MouseButton.MIDDLE -> {
                // https://github.com/AlmasB/FXGL/issues/488
            }

            else -> {
                throw IllegalArgumentException("View for $button type is not supported")
            }
        }
    }
}