/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.tools.dialogues

import com.almasb.fxgl.dsl.FXGL
import com.almasb.fxgl.dsl.getAppWidth
import com.almasb.fxgl.dsl.getUIFactoryService
import com.almasb.fxgl.dsl.getbp
import com.almasb.fxgl.scene.SubScene
import com.almasb.fxgl.tools.dialogues.DialogueEditorVars.IS_SHOW_AUDIO_LINES
import com.almasb.fxgl.tools.dialogues.DialogueEditorVars.IS_SNAP_TO_GRID
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.ColorPicker
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle


/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class PreferencesSubScene : SubScene() {

    private val vbox = VBox(5.0)

    init {
        vbox.prefWidth = 800.0
        vbox.padding = Insets(15.0)
        vbox.alignment = Pos.TOP_CENTER

        addSnapToGrid()
        addShowAudioLines()
        addColorOptions()
        addCloseButton()

        val bgOuter = Rectangle(820.0, 620.0, Color.color(0.0, 0.0, 0.0, 0.25))
        bgOuter.arcWidth = 20.0
        bgOuter.arcHeight = 20.0

        val bgInner = Rectangle(800.0, 600.0)
        bgInner.arcWidth = 20.0
        bgInner.arcHeight = 20.0
        bgInner.stroke = Color.WHITE

        val stack = StackPane(bgOuter, bgInner, vbox)
        stack.translateX = getAppWidth() / 2.0 - bgOuter.width / 2.0
        stack.translateY = 100.0

        contentRoot.children += stack
    }

    private fun addSnapToGrid() {
        val cbSnapToGrid = getUIFactoryService().newCheckBox()
        cbSnapToGrid.selectedProperty().bindBidirectional(getbp(IS_SNAP_TO_GRID))

        vbox.children += HBox(5.0, getUIFactoryService().newText("Snap to grid: "), cbSnapToGrid)
    }

    private fun addShowAudioLines() {
        val cbSnapToGrid = getUIFactoryService().newCheckBox()
        cbSnapToGrid.selectedProperty().bindBidirectional(getbp(IS_SHOW_AUDIO_LINES))

        vbox.children += HBox(5.0, getUIFactoryService().newText("Show audio lines: "), cbSnapToGrid)
    }

    private fun addColorOptions() {
        NodeView.colors.forEach {

            val picker = ColorPicker(it.value.value)
            picker.valueProperty().bindBidirectional(it.value)

            val hbox = HBox(5.0, getUIFactoryService().newText("Color ${it.key}"), picker)

            vbox.children += hbox
        }
    }

    private fun addCloseButton() {
        val btnClose = getUIFactoryService().newButton("Close")
        btnClose.setOnAction { FXGL.getSceneService().popSubScene() }

        vbox.children += btnClose
    }
}