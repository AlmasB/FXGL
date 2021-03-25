/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.dev.editor

import com.almasb.fxgl.dsl.FXGL
import com.almasb.fxgl.dsl.getAssetLoader
import com.almasb.fxgl.entity.Entity
import com.almasb.fxgl.ui.FXGLButton
import com.almasb.fxgl.ui.FXGLScrollPane
import javafx.beans.binding.*
import javafx.collections.ObservableList
import javafx.geometry.Insets
import javafx.scene.control.ListView
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.GridPane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.text.Text
import javafx.stage.FileChooser
import java.io.File

/**
 * TODO: how are going to modify each component data, e.g. ViewComponent add new view?
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class EntityInspector : FXGLScrollPane() {

    private val innerBox = VBox(5.0)

    // TODO: experimental
    private val addViewButton = FXGLButton("Add view")

    var entity: Entity? = null
        set(value) {
            field = value

            updateView()
        }

    init {
        background = Background(BackgroundFill(Color.BLACK, null, null))
        innerBox.background = Background(BackgroundFill(Color.BLACK, null, null))
        innerBox.padding = Insets(5.0)

        addViewButton.setOnAction {
            entity?.let {

                val viewComp = it.viewComponent

                val chooser = FileChooser()
                chooser.initialDirectory = File(System.getProperty("user.dir"))
                chooser.title = "Select image"
                chooser.extensionFilters.addAll(FileChooser.ExtensionFilter("Images", "*.png", "*.jpg"))
                val file = chooser.showOpenDialog(null)

                file?.let {
                    viewComp.addChild(getAssetLoader().loadTexture(it.toURI().toURL()))
                }
            }
        }

        maxWidth = 460.0

        content = innerBox
    }

    private fun updateView() {
        innerBox.children.clear()

        if (entity == null)
            return

        // TODO: this is just a placeholder and needs to be updated
        entity!!.components.sortedBy { it.javaClass.simpleName }
                .forEach { comp ->
                    val pane = GridPane()
                    pane.hgap = 25.0
                    pane.vgap = 10.0

                    var index = 0

                    val title = FXGL.getUIFactoryService().newText(comp.javaClass.simpleName.removeSuffix("Component"), Color.ANTIQUEWHITE, 22.0)

                    pane.addRow(index++, title)
                    pane.addRow(index++, Rectangle(165.0, 2.0, Color.ANTIQUEWHITE))

                    comp.javaClass.methods
                            .filter { it.name.endsWith("Property") }
                            .sortedBy { it.name }
                            .forEach { method ->

                                val textKey = FXGL.getUIFactoryService().newText(method.name.removeSuffix("Property"), Color.WHITE, 18.0)

                                val value = method.invoke(comp)
                                val textValue = FXGL.getUIFactoryService().newText("", Color.WHITE, 18.0)

                                when (value) {
                                    is BooleanExpression -> {
                                        textValue.textProperty().bind(value.asString())
                                    }

                                    is IntegerExpression -> {
                                        textValue.textProperty().bind(value.asString())
                                    }

                                    is DoubleExpression -> {
                                        textValue.textProperty().bind(value.asString("%.2f"))
                                    }

                                    is StringExpression -> {
                                        textValue.textProperty().bind(value)
                                    }

                                    is ObjectExpression<*> -> {
                                        textValue.textProperty().bind(value.asString())
                                    }

                                    is ObservableList<*> -> {
                                        // ignore here
                                    }

                                    else -> {
                                        throw IllegalArgumentException("Unknown value type: ${value.javaClass}")
                                    }
                                }

                                if (value is ObservableList<*>) {
                                    pane.addRow(index++, textKey, ListView(value))
                                } else {
                                    pane.addRow(index++, textKey, textValue)
                                }
                            }

                    pane.addRow(index++, Text(""))

                    innerBox.children += pane
                }

        innerBox.children += addViewButton
    }
}