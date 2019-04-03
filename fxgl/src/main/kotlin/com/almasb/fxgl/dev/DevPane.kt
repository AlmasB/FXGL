/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.dev

import com.almasb.fxgl.app.GameScene
import com.almasb.fxgl.app.ReadOnlyGameSettings
import com.almasb.fxgl.dsl.FXGL
import com.almasb.fxgl.ui.FXGLCheckBox
import com.almasb.fxgl.ui.InGamePanel
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.Accordion
import javafx.scene.control.ColorPicker
import javafx.scene.control.TitledPane
import javafx.scene.layout.*
import javafx.scene.paint.Color

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class DevPane(private val scene: GameScene, val settings: ReadOnlyGameSettings) {

    private val panel = InGamePanel(350.0, scene.height)

    val isOpen: Boolean
        get() = panel.isOpen

    init {
        panel.styleClass.add("dev-pane")

        val acc =  Accordion(
                TitledPane("Dev vars", createContentDevVars()),
                TitledPane("Game vars", createContentGameVars())
        )
        acc.prefWidth = 350.0

        panel.children += acc

        scene.addUINode(panel)
    }

    private fun createContentDevVars(): Pane {
        val vbox = VBox()
        vbox.padding = Insets(15.0)
        vbox.alignment = Pos.TOP_CENTER

        val pane = GridPane()
        pane.hgap = 25.0
        pane.vgap = 10.0

        settings.javaClass.declaredMethods
                .filter { it.name.startsWith("dev") }
                .sortedBy { it.name }
                .forEachIndexed { index, method ->

                    when (method.returnType) {
                        SimpleBooleanProperty::class.java -> {
                            val text = FXGL.getUIFactory().newText(method.name, Color.WHITE, 18.0)
                            val checkBox = FXGLCheckBox()

                            checkBox.selectedProperty().bindBidirectional(method.invoke(settings) as SimpleBooleanProperty)

                            pane.addRow(index, text, checkBox)
                        }

                        SimpleObjectProperty::class.java -> {
                            if (method.name.toLowerCase().contains("color")) {
                                val text = FXGL.getUIFactory().newText(method.name, Color.WHITE, 18.0)
                                val colorPicker = ColorPicker()

                                colorPicker.valueProperty().bindBidirectional(method.invoke(settings) as SimpleObjectProperty<Color>)

                                pane.addRow(index, text, colorPicker)
                            }

                        }
                        else -> {}
                    }
                }

        vbox.children.add(pane)

        return vbox
    }

    private fun createContentGameVars(): Parent {
        val vbox = VBox()
        vbox.padding = Insets(15.0)
        vbox.alignment = Pos.TOP_CENTER

        val pane = GridPane()
        pane.hgap = 25.0
        pane.vgap = 10.0

        FXGL.getGameState().properties.keys().forEachIndexed { index, key ->
            val textKey = FXGL.getUIFactory().newText(key, Color.WHITE, 18.0)

            val value = FXGL.getGameState().properties.getValue(key) as Any

            val textValue = FXGL.getUIFactory().newText(value.toString(), Color.WHITE, 18.0)

            pane.addRow(index, textKey, textValue)
        }

        vbox.children.add(pane)

        return vbox
    }

    fun open() {
        panel.open()
    }

    fun close() {
        panel.close()
    }
}