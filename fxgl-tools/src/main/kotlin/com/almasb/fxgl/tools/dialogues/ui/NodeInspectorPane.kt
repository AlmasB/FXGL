/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.tools.dialogues.ui

import com.almasb.fxgl.cutscene.dialogue.ChoiceNode
import com.almasb.fxgl.cutscene.dialogue.DialogueGraph
import com.almasb.fxgl.cutscene.dialogue.DialogueNode
import com.almasb.fxgl.cutscene.dialogue.DialogueNodeType.*
import com.almasb.fxgl.cutscene.dialogue.FunctionNode
import com.almasb.fxgl.dsl.FXGL
import com.almasb.fxgl.dsl.getUIFactoryService
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.TextField
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.text.Font
import javafx.scene.text.Text

class NodeInspectorPane : VBox(5.0) {

    private val nodeProp: ObjectProperty<DialogueNode> = SimpleObjectProperty()

    private val makeStartNodeButton = Button("Set start node")

    private lateinit var graph: DialogueGraph

    private var node: DialogueNode?
        get() = nodeProp.value
        set(value) { nodeProp.value = value }

    init {
        background = Background(BackgroundFill(Color.color(0.0, 0.0, 0.0, 0.75), null, null))

        nodeProp.addListener { _, oldNode, newNode ->
            val id = graph.findNodeID(newNode)

            children.setAll(DialogueNodeInspector(id, newNode), makeStartNodeButton)
        }

        makeStartNodeButton.setOnAction {
            graph.startNodeID = graph.findNodeID(node!!)
        }
    }

    fun updateSelection(graph: DialogueGraph, node: DialogueNode) {
        this.graph = graph
        this.node = node
    }
}

// TODO: a generic inspector view
private class DialogueNodeInspector(val id: Int, node: DialogueNode) : VBox(5.0) {

    init {
        children += generateView(node)
    }

    // TODO: if NodeView of node changed, then regenerate UI
    private fun generateView(node: DialogueNode): GridPane {
        val pane = GridPane()
        pane.hgap = 25.0
        pane.vgap = 10.0
        pane.padding = Insets(0.0, 10.0, 0.0, 10.0)

        val textID = Text("Node id: $id")
        textID.font = Font.font(14.0)
        textID.fill = Color.WHITE




        var index = 0

        // TODO: turn into generic inspector view API
        val title = FXGL.getUIFactoryService().newText(node.javaClass.simpleName.removeSuffix("Node"), Color.ANTIQUEWHITE, 22.0)

        pane.addRow(index++, title)
        pane.addRow(index++, Rectangle(165.0, 2.0, Color.ANTIQUEWHITE))

        pane.addRow(index++, textID)

        // add property based values
        node.javaClass.methods
                .filter { it.name.endsWith("Property") }
                .sortedBy { it.name }
                .forEach { method ->
                    val value = method.invoke(node)

                    val view = getUIFactoryService().newPropertyView(method.name.removePrefix("get").removeSuffix("Property"), value)

                    val box = view as HBox
                    val stack1 = box.children[0] as StackPane
                    val stack2 = box.children[1] as StackPane

                    stack1.alignment = Pos.CENTER_LEFT
                    stack2.prefWidth = StackPane.USE_COMPUTED_SIZE

                    pane.addRow(index++, view)
                }

        pane.addRow(index++, Text(""))

        pane.addRow(index++, Rectangle(165.0, 2.0, Color.ANTIQUEWHITE))

        if (node.type == CHOICE) {
            val choiceNode = node as ChoiceNode

            choiceNode.conditions.forEach { (id, condition) ->
                val view = getUIFactoryService().newPropertyView("Condition $id", condition)

                pane.addRow(index++, view)
            }
        }

        return pane
    }
}