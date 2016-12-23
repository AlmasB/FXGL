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
import javafx.beans.binding.Binding
import javafx.beans.binding.Bindings
import javafx.beans.binding.BooleanBinding
import javafx.beans.binding.When
import javafx.beans.property.SimpleBooleanProperty
import javafx.collections.FXCollections
import javafx.geometry.NodeOrientation
import javafx.geometry.Pos
import javafx.scene.control.TitledPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import java.util.concurrent.Callable


/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class QuestPane(width: Double, height: Double, vararg initialQuests: Quest) : VBox() {

    private val quests = FXCollections.observableArrayList<Quest>(*initialQuests)

    init {
        prefWidth = width
        prefHeight = height

        quests.forEach { quest ->
            val vbox = VBox()
            vbox.children.addAll(quest.objectives)

            val mainCheckBox = QuestCheckBox()
            mainCheckBox.stroke = Color.BLACK

            val failedBinding = quest.objectives.map { it.checkBox.stateProperty() }
                    .foldRight(Bindings.createBooleanBinding(Callable { false }), { state, binding ->
                        state.isEqualTo(QuestState.FAILED).or(binding)
                    })

            val completedBinding = quest.objectives.map { it.checkBox.stateProperty() }
                    .foldRight(Bindings.createBooleanBinding(Callable { true }), { state, binding ->
                        state.isEqualTo(QuestState.COMPLETED).and(binding)
                    })

//            val activeBinding = quest.objectives.map { it.checkBox.stateProperty() }
//                    .foldRight(Bindings.createBooleanBinding(Callable { true }), { state, binding ->
//                        state.isEqualTo(QuestState.COMPLETED).and(binding)
//                    })


            val intermediateBinding = Bindings.`when`(completedBinding).then(QuestState.COMPLETED).otherwise(QuestState.ACTIVE)
            val finalBinding = Bindings.`when`(failedBinding).then(QuestState.FAILED).otherwise(intermediateBinding)

            mainCheckBox.stateProperty().bind(finalBinding)

            val hbox = HBox(mainCheckBox)
            hbox.alignment = Pos.CENTER_RIGHT

            HBox.setHgrow(hbox, Priority.ALWAYS)

            val hboxRoot = HBox(10.0, FXGL.getUIFactory().newText(quest.name, Color.BLACK, 18.0), hbox)
            hboxRoot.prefWidth = width - mainCheckBox.width*3 + 2

            val pane = TitledPane("", vbox)
            pane.graphic = hboxRoot

            children.add(pane)
        }
    }

    fun addQuest(quest: Quest) {
        quests.add(quest)
    }
}