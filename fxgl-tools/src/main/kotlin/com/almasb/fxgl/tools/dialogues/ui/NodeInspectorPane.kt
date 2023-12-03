/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.tools.dialogues.ui

import com.almasb.fxgl.cutscene.dialogue.DialogueGraph
import com.almasb.fxgl.cutscene.dialogue.DialogueNode
import com.almasb.fxgl.cutscene.dialogue.DialogueNodeType
import com.almasb.fxgl.cutscene.dialogue.DialogueNodeType.*
import com.almasb.fxgl.cutscene.dialogue.FunctionNode
import com.almasb.fxgl.dsl.FXGL
import com.almasb.fxgl.dsl.getUIFactoryService
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.Button
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.GridPane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.text.Font
import javafx.scene.text.Text

class NodeInspectorPane : VBox(5.0) {

    private val nodeProp: ObjectProperty<DialogueNode> = SimpleObjectProperty()

    private val textID = Text()
    private val makeStartNodeButton = Button("Set start node")

    private lateinit var graph: DialogueGraph

    private var node: DialogueNode?
        get() = nodeProp.value
        set(value) { nodeProp.value = value }

    init {
        background = Background(BackgroundFill(Color.color(0.0, 0.0, 0.0, 0.75), null, null))

        textID.font = Font.font(14.0)
        textID.fill = Color.WHITE

        nodeProp.addListener { _, oldNode, newNode ->
            val id = graph.findNodeID(newNode)

            textID.text = "Node id: $id"

            if (newNode.type == FUNCTION) {
                children.setAll(textID, FunctionNodeInspector(newNode as FunctionNode))
            }
        }

        makeStartNodeButton.setOnAction {
            graph.startNodeID = graph.findNodeID(node!!)
        }

        children += textID
        children += makeStartNodeButton
    }

    fun updateSelection(graph: DialogueGraph, node: DialogueNode) {
        this.graph = graph
        this.node = node
    }
}

private class FunctionNodeInspector(node: FunctionNode) : VBox(5.0) {

    init {
        children += generateView(node)
    }

    private fun generateView(component: FunctionNode): GridPane {
        val pane = GridPane()
        pane.hgap = 25.0
        pane.vgap = 10.0

        var index = 0

        val title = FXGL.getUIFactoryService().newText(component.javaClass.simpleName.removeSuffix("Component"), Color.ANTIQUEWHITE, 22.0)

        pane.addRow(index++, title)
        pane.addRow(index++, Rectangle(165.0, 2.0, Color.ANTIQUEWHITE))

        // add property based values
        component.javaClass.methods
                .filter { it.name.endsWith("Property") }
                .sortedBy { it.name }
                .forEach { method ->

                    // val textKey = FXGL.getUIFactoryService().newText(method.name.removeSuffix("Property"), Color.WHITE, 18.0)
                    val value = method.invoke(component)

                    val view = getUIFactoryService().newPropertyView(method.name.removeSuffix("Property"), value)

                    pane.addRow(index++, view)
                }

        pane.addRow(index++, Text(""))

        return pane
    }
}