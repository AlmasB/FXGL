/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.tools.dialogues.ui

import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.scene.text.Text

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class ErrorIcon : StackPane() {

    init {
        val bgCircle = Circle(10.0, Color.RED).also {
            it.stroke = Color.WHITE
        }

        val symbol = Text("!").also {
            it.font = Font.font("", FontWeight.BOLD, 14.0)
            it.fill = Color.WHITE
        }

        children.addAll(bgCircle, symbol)
    }
}