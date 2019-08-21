/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.tools.dialogues

import com.almasb.fxgl.core.util.Consumer
import com.almasb.fxgl.core.util.InputPredicates
import com.almasb.fxgl.cutscene.dialogue.SerializableGraph
import com.almasb.fxgl.dsl.*
import com.almasb.fxgl.tools.dialogues.ui.FXGLContextMenu
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import javafx.beans.binding.Bindings
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.shape.Polygon
import javafx.scene.shape.Rectangle
import javafx.stage.FileChooser
import java.io.File
import java.nio.file.Files

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class MainUI : BorderPane() {

    private val toolbar = HBox(15.0)
    private val tabPane = TabPane()

    private val currentTab: DialogueTab?
        get() = tabPane.selectionModel.selectedItem as? DialogueTab

    init {
        toolbar.prefWidthProperty().bind(
                Bindings.`when`(Bindings.isNotEmpty(tabPane.tabs)).then(getAppWidth() / 2.0).otherwise(getAppWidth())
        )
        toolbar.prefHeight = 30.0
        toolbar.style = "-fx-background-color: black"
        toolbar.alignment = Pos.CENTER_LEFT


        val contextMenu = FXGLContextMenu()
        contextMenu.addItem("New") { openNewDialog() }
        contextMenu.addItem("Open...") { openLoadDialog() }
        contextMenu.addItem("Save") { currentTab?.let { onSave(it) } }
        contextMenu.addItem("Save As...") { currentTab?.let { openSaveAsDialog(it) } }
        contextMenu.addItem("Save All") { onSaveAll() }
        contextMenu.addItem("Exit") { getGameController().exit() }


        val pane = Pane(tabPane, toolbar)

        val menuFile = Menu("")
        menuFile.graphic = getUIFactory().newText("File").also {
            it.setOnMouseClicked {
                contextMenu.show(pane, 0.0, toolbar.prefHeight)
            }
        }
        menuFile.style = "-fx-background-color: black"

        val menuPreferences = Menu("")
        menuPreferences.graphic = getUIFactory().newText("Preferences").also {
            it.setOnMouseClicked {
                openPreferencesDialog()
            }
        }
        menuPreferences.style = "-fx-background-color: black"

        val menuBar = MenuBar()
        menuBar.style = "-fx-background-color: black"
        menuBar.menus.addAll(menuFile, menuPreferences)

        toolbar.children += menuBar
        toolbar.children += makeRunButton()

        setPrefSize(FXGL.getAppWidth().toDouble(), FXGL.getAppHeight().toDouble())

        pane.style = "-fx-background-color: gray"
        pane.setPrefSize(FXGL.getAppWidth().toDouble(), FXGL.getAppHeight().toDouble())

        center = pane
    }

    private fun makeRunButton(): Node {
        val stack = StackPane()

        val bgRun = Rectangle(18.0, 18.0, null)
        bgRun.strokeProperty().bind(
                Bindings.`when`(stack.hoverProperty()).then(Color.WHITE).otherwise(Color.TRANSPARENT)
        )
        bgRun.fillProperty().bind(
                Bindings.`when`(stack.pressedProperty()).then(Color.color(0.5, 0.5, 0.5, 0.95)).otherwise(Color.TRANSPARENT)
        )

        val btnRun = Polygon(
                0.0, 0.0,
                13.0, 6.5,
                0.0, 13.0
        )
        btnRun.fill = Color.LIGHTGREEN

        stack.setOnMouseClicked {
            stack.requestFocus()

            currentTab?.pane?.let {
                FXGL.getCutsceneService().startDialogueScene(it.graph)
            }
        }

        stack.children.addAll(bgRun, btnRun)

        return stack
    }

    private fun openPreferencesDialog() {
        val alert = Alert(Alert.AlertType.WARNING)
        alert.contentText = "Preferences NOT IMPLEMENTED yet"
        alert.show()
    }

    private fun openNewDialog() {
        getDisplay().showInputBox("New dialogue name", InputPredicates.ALPHANUM, Consumer { name ->
            val tab = DialogueTab(File("$name.json"), DialoguePane())

            tabPane.tabs += tab
            tabPane.selectionModel.select(tab)
        })
    }

    private fun onSave(tab: DialogueTab) {
        val serializedGraph = tab.pane.save()

        val s = mapper.writeValueAsString(serializedGraph)

        Files.writeString(tab.file.toPath(), s)
    }

    private fun onSaveAll() {
        tabPane.tabs
                .map { it as DialogueTab }
                .forEach { onSave(it) }
    }

    private val mapper = jacksonObjectMapper().also { it.enable(SerializationFeature.INDENT_OUTPUT) }

    private fun openSaveAsDialog(tab: DialogueTab) {
        val chooser = FileChooser()
        chooser.initialDirectory = File(System.getProperty("user.dir"))
        chooser.initialFileName = tab.file.name + ".json"

        chooser.showSaveDialog(scene.window)?.let {
            val serializedGraph = tab.pane.save()

            val s = mapper.writeValueAsString(serializedGraph)

            Files.writeString(it.toPath(), s)
        }
    }

    private fun openLoadDialog() {
        val chooser = FileChooser()
        chooser.initialDirectory = File(System.getProperty("user.dir"))
        chooser.extensionFilters += FileChooser.ExtensionFilter("FXGL dialogue files", "*.json")

        chooser.showOpenDialog(scene.window)?.let {
            val tab = DialogueTab(it, DialoguePane())

            tabPane.tabs += tab
            tabPane.selectionModel.select(tab)

            tab.pane.load(mapper.readValue(it, SerializableGraph::class.java))
        }
    }

    private class DialogueTab(val file: File,
                              val pane: DialoguePane) : Tab(file.nameWithoutExtension) {
        init {
            content = pane

            textProperty().bind(
                    Bindings.`when`(pane.isDirtyProperty).then(file.nameWithoutExtension + "*").otherwise(file.nameWithoutExtension)
            )
        }
    }
}