/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.input.view

import com.almasb.fxgl.app.FXGL
import javafx.scene.input.KeyCode
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class KeyView
@JvmOverloads constructor(keyCode: KeyCode,
                          color: Color = Color.ORANGE,
                          size: Double = 24.0) : StackPane() {

    private val text = FXGL.getUIFactory().newText(keyCode.getName(), color, size - 2)
    private val background = Rectangle(size * 0.95, size * 1.2, Color.BLACK)
    private val border = Rectangle(size * 1.01, size * 1.25, null)

    init {
        border.arcWidth = size / 4
        border.arcHeight = size / 4
        border.stroke = color
        border.strokeWidth = size / 11

        // handle special cases
        when (keyCode) {
            KeyCode.CONTROL -> {
                background.width = text.layoutBounds.width * 1.34
                border.width = text.layoutBounds.width * 1.36
            }

            KeyCode.ALT -> {
                background.width = text.layoutBounds.width * 1.2
                border.width = text.layoutBounds.width * 1.26
            }

            KeyCode.SHIFT -> {
                text.text = "\u21E7   "
                background.width = size * 1.6
                border.width = size * 1.66
            }

            KeyCode.SPACE -> {
                text.text = "\u2423"
                background.width = size * 1.9
                border.width = size * 1.96
            }

            KeyCode.DOWN -> {
                text.text = "\u2193"
            }

            KeyCode.UP -> {
                text.text = "\u2191"
            }

            KeyCode.LEFT -> {
                text.text = "\u2190"
            }

            KeyCode.RIGHT -> {
                text.text = "\u2192"
            }

            else -> {}
        }

        children.addAll(background, border, text)
    }

    fun setKeyColor(color: Color) {
        text.fill = color
        border.stroke = color
    }

    fun setBackgroundColor(color: Color) {
        background.fill = color
    }
}