/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.tools.dialogues

import com.almasb.fxgl.cutscene.dialogue.DialogueGraphSerializer
import com.almasb.fxgl.cutscene.dialogue.DialogueScene
import com.almasb.fxgl.cutscene.dialogue.SerializableGraph
import com.almasb.fxgl.cutscene.dialogue.SerializablePoint2D
import com.almasb.fxgl.dsl.*
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

    init {
        toolbar.setPrefSize(getAppWidth() / 2.0, 30.0)
        toolbar.style = "-fx-background-color: black"
        toolbar.alignment = Pos.CENTER_LEFT

        val itemSave = MenuItem("Save")
        itemSave.setOnAction {
            openSaveDialog()
        }

        val menuFile = Menu("")
        menuFile.graphic = Text("File").also { it.fill = Color.WHITE }
        menuFile.style = "-fx-background-color: black"
        menuFile.items.addAll(itemSave, MenuItem("Load").also { it.setOnAction { openLoadDialog() } })

        val menuBar = MenuBar()
        menuBar.style = "-fx-background-color: black"
        menuBar.menus.addAll(menuFile)

        toolbar.children += menuBar


        toolbar.children += makeRunButton()

//        toolbar.children += getUIFactory().newText("High contrast")
//
//        toolbar.children += CheckBox().also {
//            it.selectedProperty().bindBidirectional(DialoguePane.highContrastProperty)
//        }



        setPrefSize(FXGL.getAppWidth().toDouble(), FXGL.getAppHeight().toDouble())







        for (i in 1..3) {
            val tab = Tab("healer_dialogue")
            tab.content = DialoguePane()

            tabPane.tabs += tab
        }


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

            val dialoguePane = tabPane.selectionModel.selectedItem.content as DialoguePane

            DialogueScene(getGameController(), getAppWidth(), getAppHeight()).start(dialoguePane.graph)
        }

        stack.children.addAll(bgRun, btnRun)

        return stack
    }


    private val mapper = jacksonObjectMapper()

    private fun openSaveDialog() {
        val chooser = FileChooser()
        chooser.initialDirectory = File(System.getProperty("user.dir"))
        chooser.initialFileName = "dialogue_graph.json"

//        chooser.showSaveDialog(scene.window)?.let {
//            mapper.enable(SerializationFeature.INDENT_OUTPUT)
//
//            val serializedGraph = DialogueGraphSerializer.toSerializable(graph)
//
//            nodeViews.children.map { it as NodeView }.forEach {
//                serializedGraph.uiMetadata[graph.findNodeID(it.node)] = SerializablePoint2D(it.layoutX, it.layoutY)
//            }
//
//            val s = mapper.writeValueAsString(serializedGraph)
//
//            Files.writeString(it.toPath(), s)
//        }
    }

    private fun openLoadDialog() {
        val chooser = FileChooser()
        chooser.initialDirectory = File(System.getProperty("user.dir"))
        chooser.initialFileName = "dialogue_graph.json"

        chooser.showOpenDialog(scene.window)?.let {
            load(mapper.readValue(it, SerializableGraph::class.java))
        }
    }

    private fun load(serializedGraph: SerializableGraph) {
//        graph = DialogueGraphSerializer.fromSerializable(serializedGraph)
//
//        nodeViews.children.clear()
//        edgeViews.children.clear()
//
//        graph.nodes.forEach { (id, node) ->
//            val x = serializedGraph.uiMetadata[id]?.x ?: 100.0
//            val y = serializedGraph.uiMetadata[id]?.y ?: 100.0
//
//            onAdded(node, x, y)
//        }
//
//        graph.edges.forEach { edge ->
//            onAdded(edge)
//        }
//
//        initGraphListeners()
    }
}