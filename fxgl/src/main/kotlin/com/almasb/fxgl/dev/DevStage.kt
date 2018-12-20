/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.dev

import com.almasb.fxgl.dsl.FXGL
import com.almasb.fxgl.app.ReadOnlyGameSettings
import com.almasb.fxgl.ui.FXGLCheckBox
import javafx.beans.property.SimpleBooleanProperty
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.layout.GridPane
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.stage.Modality
import javafx.stage.Stage

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class DevStage(val settings: ReadOnlyGameSettings) : Stage() {

    init {
        initModality(Modality.NONE)
        //initStyle(StageStyle.UNDECORATED)
        isResizable = false

        scene = Scene(createContent(), 300.0, 600.0)

        scene.stylesheets += FXGL.getAssetLoader().loadCSS(settings.css).externalForm
    }

    private fun createContent(): Pane {
        val vbox = VBox()
        vbox.padding = Insets(15.0)
        vbox.alignment = Pos.TOP_CENTER

        val pane = GridPane()
        pane.hgap = 25.0
        pane.vgap = 10.0

        settings.javaClass.declaredMethods
                .filter { it.name.startsWith("dev") }
                .filter { it.returnType == SimpleBooleanProperty::class.java }
                .forEachIndexed { index, method ->

                    val text = FXGL.getUIFactory().newText(method.name, Color.BLACK, 18.0)
                    val checkBox = FXGLCheckBox()

                    checkBox.selectedProperty().bindBidirectional(method.invoke(settings) as SimpleBooleanProperty)

                    pane.addRow(index, text, checkBox)
                }






        vbox.children.add(pane)

        return vbox
    }
}