/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.tools.dialogues

import com.almasb.fxgl.dsl.*
import com.almasb.fxgl.scene.SubScene
import javafx.geometry.Insets
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.paint.Color

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class PreferencesSubScene : SubScene() {

    init {
        val vbox = VBox(5.0)
        vbox.prefWidth = 300.0
        vbox.translateX = getAppWidth() / 2.0 - vbox.prefWidth / 2.0
        vbox.translateY = getAppHeight() / 2.0
        vbox.padding = Insets(15.0)
        vbox.background = Background(BackgroundFill(Color.BLACK, null, null))

        val cbSnapToGrid = getUIFactoryService().newCheckBox()
        cbSnapToGrid.selectedProperty().bindBidirectional(getbp("isSnapToGrid"))

        val btnClose = getUIFactoryService().newButton("Close")
        btnClose.setOnAction { FXGL.getSceneService().popSubScene() }

        vbox.children += HBox(5.0, getUIFactoryService().newText("Snap to grid: "), cbSnapToGrid)
        vbox.children += btnClose
        contentRoot.children += vbox
    }
}