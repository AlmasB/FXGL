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
import javafx.beans.binding.BooleanBinding
import javafx.beans.property.IntegerProperty
import javafx.geometry.Pos
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import javafx.util.Duration

/**
 * A single quest objective.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class QuestObjective
@JvmOverloads
constructor(val description: String,
            val valueProperty: IntegerProperty,
            val times: Int = 1,
            val duration: Duration = Duration.ZERO) : HBox(10.0) {

    val successListener: BooleanBinding
    val checkBox = QuestCheckBox()

    init {
        val factory = FXGL.getUIFactory()

        val text = factory.newText("", Color.WHITE, 18.0)
        text.textProperty().bind(valueProperty.asString("%d/$times"))

        val hbox = HBox(checkBox)
        hbox.alignment = Pos.CENTER_RIGHT

        HBox.setHgrow(hbox, Priority.ALWAYS)

        children.addAll(factory.newText(description, Color.WHITE, 18.0), text, hbox)

        successListener = valueProperty.greaterThanOrEqualTo(times)

        successListener.addListener { o, old, isReached ->
            if (isReached) {
                checkBox.setState(QuestState.COMPLETED)
            } else {
                checkBox.setState(QuestState.ACTIVE)
            }
        }

        if (duration !== Duration.ZERO) {
            FXGL.getMasterTimer().runOnceAfter({ checkBox.setState(QuestState.FAILED) }, duration)
        }
    }
}