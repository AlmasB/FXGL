/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.tools.dialogues

import com.almasb.fxgl.core.util.Consumer
import com.almasb.fxgl.core.util.InputPredicates
import com.almasb.fxgl.cutscene.dialogue.DialogueScene
import com.almasb.fxgl.cutscene.dialogue.SerializableGraph
import com.almasb.fxgl.dsl.*
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import javafx.beans.binding.Bindings
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.input.KeyCombination
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.shape.Polygon
import javafx.scene.shape.Rectangle
import javafx.scene.text.Text
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

    private val currentPane: DialoguePane?
        get() = currentTab?.content as? DialoguePane

    init {
        toolbar.setPrefSize(getAppWidth() / 2.0, 30.0)
        toolbar.style = "-fx-background-color: black"
        toolbar.alignment = Pos.CENTER_LEFT



        val menuFile = Menu("")
        menuFile.graphic = Text("File").also { it.fill = Color.WHITE }
        menuFile.style = "-fx-background-color: black"
        menuFile.items.addAll(
                MenuItem("New").also {
                    it.setOnAction { openNewDialog() }
                    it.accelerator = KeyCombination.keyCombination("Shortcut+N")
                },

                MenuItem("Open...").also {
                    it.setOnAction { openLoadDialog() }
                    it.accelerator = KeyCombination.keyCombination("Shortcut+O")
                },

                MenuItem("Save").also {
                    it.setOnAction { onSave() }
                    it.accelerator = KeyCombination.keyCombination("Shortcut+S")
                },

                MenuItem("Save As...").also {
                    it.setOnAction { openSaveAsDialog() }
                    it.accelerator = KeyCombination.keyCombination("Shortcut+ALT+S")
                },

                MenuItem("Save All").also {
                    it.setOnAction {  }
                    it.accelerator = KeyCombination.keyCombination("Shortcut+SHIFT+S")
                },

                MenuItem("Exit").also {
                    it.setOnAction { getGameController().exit() }
                }
        )

        val menuBar = MenuBar()
        menuBar.style = "-fx-background-color: black"
        menuBar.menus.addAll(menuFile)

        toolbar.children += menuBar
        toolbar.children += makeRunButton()

        setPrefSize(FXGL.getAppWidth().toDouble(), FXGL.getAppHeight().toDouble())

        val pane = Pane(tabPane, toolbar)
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

            currentPane?.let {
                FXGL.getCutsceneService().startDialogueScene(it.graph)
            }
        }

        stack.children.addAll(bgRun, btnRun)

        return stack
    }

    private fun openNewDialog() {
        getDisplay().showInputBox("New dialogue name", InputPredicates.ALPHANUM, Consumer { name ->
            val tab = DialogueTab(File("$name.json"))
            tab.content = DialoguePane()

            tabPane.tabs += tab
            tabPane.selectionModel.select(tab)
        })
    }

    private fun onSave() {
        currentPane?.let { pane ->
            val serializedGraph = pane.save()

            val s = mapper.writeValueAsString(serializedGraph)

            Files.writeString(currentTab!!.file.toPath(), s)
        }
    }

    private val mapper = jacksonObjectMapper().also { it.enable(SerializationFeature.INDENT_OUTPUT) }

    private fun openSaveAsDialog() {
        currentPane?.let { pane ->
            val chooser = FileChooser()
            chooser.initialDirectory = File(System.getProperty("user.dir"))
            chooser.initialFileName = currentTab!!.file.name

            chooser.showSaveDialog(scene.window)?.let {
                val serializedGraph = pane.save()

                val s = mapper.writeValueAsString(serializedGraph)

                Files.writeString(it.toPath(), s)
            }
        }
    }

    private fun openLoadDialog() {
        val chooser = FileChooser()
        chooser.initialDirectory = File(System.getProperty("user.dir"))
        chooser.extensionFilters += FileChooser.ExtensionFilter("FXGL dialogue files", "*.json")

        chooser.showOpenDialog(scene.window)?.let {
            val tab = DialogueTab(it)
            val pane = DialoguePane()

            tab.content = pane

            tab.textProperty().bind(
                    Bindings.`when`(pane.isDirtyProperty).then(it.nameWithoutExtension + "*").otherwise(it.nameWithoutExtension)
            )

            tabPane.tabs += tab
            tabPane.selectionModel.select(tab)

            pane.load(mapper.readValue(it, SerializableGraph::class.java))
        }
    }

    private class DialogueTab(val file: File) : Tab(file.nameWithoutExtension)
}