/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2016 AlmasB (almaslvl@gmail.com)
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