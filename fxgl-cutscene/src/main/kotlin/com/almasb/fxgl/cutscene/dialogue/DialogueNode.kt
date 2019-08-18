/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.cutscene.dialogue

import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import java.io.Serializable

enum class DialogueNodeType {
    START, END, TEXT, CHOICE, FUNCTION, BRANCH
}

/* NODES */

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
sealed class DialogueNode(val type: DialogueNodeType,
                            text: String) {

    val textProperty: StringProperty = SimpleStringProperty(text)

    val text: String
        get() = textProperty.value

    /**
     * The id of this node in a dialogue graph.
     * By default, when it is not in a graph, the value is -1.
     */
    var id = -1
        internal set
}

class StartNode(text: String) : DialogueNode(DialogueNodeType.START, text)

class EndNode(text: String) : DialogueNode(DialogueNodeType.END, text)

class TextNode(text: String) : DialogueNode(DialogueNodeType.TEXT, text)

class FunctionNode(text: String) : DialogueNode(DialogueNodeType.FUNCTION, text)

class BranchNode(text: String) : DialogueNode(DialogueNodeType.BRANCH, text)

class ChoiceNode(text: String) : DialogueNode(DialogueNodeType.CHOICE, text)  {

    // TODO: perhaps hide the map and use addOption / removeOption to be safe
    /**
     * Maps option id to option text.
     * Options start at id 0.
     * These are ids that are local to this choice node.
     */
    val options = hashMapOf<Int, StringProperty>()

    /**
     * Returns last option id present in the options map, or -1 if there are no options.
     */
    val lastOptionID: Int
        get() = options.keys.max() ?: -1
}

/* EDGES */

class DialogueEdge(val source: DialogueNode, val target: DialogueNode)

class DialogueChoiceEdge(val source: DialogueNode, val optionID: Int, val target: DialogueNode)

/* GRAPH */

class DialogueGraph(internal var uniqueID: Int = 0) : Serializable {

    val nodes = mutableListOf<DialogueNode>()
    val edges = mutableListOf<DialogueEdge>()
    val choiceEdges = mutableListOf<DialogueChoiceEdge>()

    /**
     * Adds node to this graph and updates the node's id if it is -1.
     */
    fun addNode(node: DialogueNode) {
        nodes += node

        if (node.id == -1)
            node.id = uniqueID++
    }

    /**
     * Removes the node from this graph, including incident edges.
     * Updates the node's id to -1.
     */
    fun removeNode(node: DialogueNode) {
        nodes -= node

        edges.removeIf { it.source.id == node.id || it.target.id == node.id }
        choiceEdges.removeIf { it.source.id == node.id || it.target.id == node.id }

        node.id = -1
    }

    fun addEdge(source: DialogueNode, target: DialogueNode) {
        edges += DialogueEdge(source, target)
    }

    fun addEdge(source: DialogueNode, localID: Int, target: DialogueNode) {
        choiceEdges += DialogueChoiceEdge(source, localID, target)
    }

    fun removeEdge(source: DialogueNode, target: DialogueNode) {
        edges.removeIf { it.source.id == source.id && it.target.id == target.id }
    }

    /**
     * Remove choice or branch edge
     */
    fun removeEdge(source: DialogueNode, optionID: Int, target: DialogueNode) {
        choiceEdges.removeIf { it.source.id == source.id && it.optionID == optionID && it.target.id == target.id }
    }

    fun findNodeById(id: Int): DialogueNode? {
        return nodes.find { it.id == id }
    }
}

