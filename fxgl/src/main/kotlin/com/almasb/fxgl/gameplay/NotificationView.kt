/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.gameplay

import com.almasb.fxgl.app.FXGL
import javafx.animation.Transition
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.input.KeyCode
import javafx.scene.paint.Color
import javafx.util.Duration

/**
 * Represents visual aspect of a notification.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
internal class NotificationView(
        val notification: Notification,
        bgColor: Color,
        private val `in`: Transition,
        private val out: Transition) : Button(notification.message) {

    init {
        styleClass.setAll("fxgl_button")
        alignment = Pos.CENTER
        setOnKeyPressed { e ->
            if (e.code == KeyCode.ENTER) {
                fire()
            }
        }
        setOnAction { e -> hide() }
        font = FXGL.getUIFactory().newFont(12.0)
        style = "-fx-background-color: " + String.format("rgb(%d,%d,%d);",
                (bgColor.red * 255).toInt(),
                (bgColor.green * 255).toInt(),
                (bgColor.blue * 255).toInt())

        `in`.setOnFinished { e -> FXGL.getMasterTimer().runOnceAfter( { hide() }, Duration.seconds(3.0)) }
    }

    fun show() = `in`.play()

    fun hide() = out.play()
}