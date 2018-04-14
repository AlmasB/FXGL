/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ai

import com.almasb.fxgl.app.FXGL
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.text.Text

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class AIBubble : StackPane() {

    private val message = SimpleStringProperty()

    init {

        val bg = Rectangle(150.0, 30.0)
        with(bg) {
            arcWidth = 25.0
            arcHeight = 25.0
            fill = Color.AQUA
            opacity = 0.5
        }

        val text = Text()
        with(text) {
            font = FXGL.getUIFactory().newFont(14.0)
            textProperty().bind(message)
        }

        translateY = -30.0
        alignment = Pos.CENTER
        children.addAll(bg, text)
    }

    fun setMessage(message: String) {
        this.message.value = message
    }
}