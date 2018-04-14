/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.gameplay.rpg.quest

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.devtools.DeveloperTools
import javafx.animation.ScaleTransition
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.TitledPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.util.Duration

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

        val closeBtn = Button("X")
        closeBtn.font = Font.font(11.0)
        closeBtn.setPrefSize(15.0, 9.0)
        closeBtn.visibleProperty().bind(mainCheckBox.stateProperty().isNotEqualTo(QuestState.ACTIVE))
        closeBtn.setOnAction { close() }

        VBox.setVgrow(closeBtn, Priority.NEVER)

        val hboxRoot = HBox(10.0, FXGL.getUIFactory().newText(quest.name, Color.BLACK, 18.0), closeBtn, hbox)
        hboxRoot.prefWidth = width - mainCheckBox.width*3 + 2

        content = vbox
        graphic = hboxRoot
    }

    private fun close() {
        with(ScaleTransition(Duration.seconds(0.33), this)) {
            fromX = 1.0
            toX = 0.0
            fromY = 1.0
            toY = 0.0
            setOnFinished { DeveloperTools.removeFromParent(this@QuestView) }
            play()
        }
    }
}