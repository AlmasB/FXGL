/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.gameplay.rpg.quest

import com.almasb.fxgl.app.FXGL
import javafx.geometry.Pos
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.paint.Color

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class QuestObjectiveView(val questObjective: QuestObjective) : HBox(10.0) {

    init {
        val factory = FXGL.getUIFactory()

        val text = factory.newText("", Color.BLACK, 18.0)
        if (questObjective.times != 1) {
            text.textProperty().bind(questObjective.valueProperty.asString("%d/${questObjective.times}"))
        }

        val checkBox = QuestCheckBox()
        checkBox.stateProperty().bind(questObjective.stateProperty())

        val hbox = HBox(checkBox)
        hbox.alignment = Pos.CENTER_RIGHT

        HBox.setHgrow(hbox, Priority.ALWAYS)

        children.addAll(factory.newText(questObjective.description, Color.BLACK, 18.0), text, hbox)
    }
}