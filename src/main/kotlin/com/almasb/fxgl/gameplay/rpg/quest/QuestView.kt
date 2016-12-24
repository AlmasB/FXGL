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
import javafx.geometry.Pos
import javafx.scene.control.TitledPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.paint.Color

/**
 * View for a quest in the form of a titled pane.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class QuestView(val quest: Quest, width: Double) : TitledPane() {

    init {
        val vbox = VBox()
        vbox.children.addAll(quest.objectives.map { QuestObjectiveView(it) })

        val mainCheckBox = QuestCheckBox()
        mainCheckBox.stroke = Color.BLACK
        mainCheckBox.stateProperty().bind(quest.stateProperty())

        val hbox = HBox(mainCheckBox)
        hbox.alignment = Pos.CENTER_RIGHT

        HBox.setHgrow(hbox, Priority.ALWAYS)

        val hboxRoot = HBox(10.0, FXGL.getUIFactory().newText(quest.name, Color.BLACK, 18.0), hbox)
        hboxRoot.prefWidth = width - mainCheckBox.width*3 + 2

        content = vbox
        graphic = hboxRoot
    }
}