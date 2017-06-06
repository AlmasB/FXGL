/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.gameplay.qte

import com.almasb.fxgl.app.FXGL
import javafx.scene.input.KeyCode
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle

/**
 * Represents a single QTE key visible on the screen.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
internal class QTEKey(val keyCode: KeyCode) : StackPane() {

    private val background = Rectangle(64.0, 64.0, Color.BLACK)
    private val text = FXGL.getUIFactory().newText(keyCode.getName(), Color.WHITE, 72.0)

    init {
        background.stroke = Color.BLACK
        background.strokeWidth = 4.0

        val border = Rectangle(72.0, 72.0, null)
        border.arcWidth = 25.0
        border.arcHeight = 25.0
        border.stroke = Color.GRAY
        border.strokeWidth = 6.0

        children.addAll(background, border, text)
    }

    fun lightUp() {
        background.fill = Color.YELLOW
        background.stroke = Color.YELLOW
        text.fill = Color.BLACK
    }
}