/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
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

package com.almasb.fxgl.gameplay.rpg.quest

import com.almasb.fxgl.app.FXGL
import javafx.animation.FillTransition
import javafx.animation.ParallelTransition
import javafx.animation.RotateTransition
import javafx.beans.binding.BooleanBinding
import javafx.beans.property.IntegerProperty
import javafx.geometry.Pos
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.util.Duration

/**
 * A single quest objective.
 * TODO: timer?
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class QuestObjective(val description: String, val valueProperty: IntegerProperty, val times: Int = 1) : HBox(10.0) {

    companion object {
        private val COLOR_SUCCESS = Color.GREEN.brighter().brighter().brighter()
        private val COLOR_FAIL = Color.DARKRED
    }

    private val checkRect = Rectangle(18.0, 18.0, null)
    private val successListener: BooleanBinding

    init {
        with(checkRect) {
            arcWidth = 12.0
            arcHeight = 12.0
            stroke = Color.WHITESMOKE
            strokeWidth = 1.0
        }

        val factory = FXGL.getUIFactory()

        val text = factory.newText("", Color.WHITE, 18.0)
        text.textProperty().bind(valueProperty.asString("%d/$times"))

        val hboxLeft = HBox(10.0, factory.newText(description, Color.WHITE, 18.0), text)
        hboxLeft.alignment = Pos.CENTER_LEFT

        val hbox = HBox(checkRect)
        hbox.alignment = Pos.CENTER_RIGHT

        HBox.setHgrow(hbox, Priority.ALWAYS)

        children.addAll(hboxLeft, hbox)

        successListener = valueProperty.greaterThanOrEqualTo(times)

        successListener.addListener { o, old, isReached ->
            if (isReached)
                onSuccess()
        }
    }

    private fun onSuccess() {
        val fill = FillTransition(Duration.seconds(0.35), checkRect, Color.TRANSPARENT, COLOR_SUCCESS)
        val rotation = RotateTransition(Duration.seconds(0.35), checkRect)
        rotation.toAngle = 180.0

        ParallelTransition(fill, rotation).play()
    }

    private fun onFail() {
        val fill = FillTransition(Duration.seconds(0.35), checkRect, Color.TRANSPARENT, COLOR_FAIL)
        val rotation = RotateTransition(Duration.seconds(0.35), checkRect)
        rotation.toAngle = -180.0

        ParallelTransition(fill, rotation).play()
    }
}