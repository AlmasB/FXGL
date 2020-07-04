/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.cutscene.dialogue

import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.collections.FXCollections
import java.io.Serializable

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */

enum class DialogueNodeType {
    START, END, TEXT, CHOICE, FUNCTION, BRANCH
}

interface FunctionCallHandler {

    fun handle(functionName: String, args: Array<String>): Any
}

/* NODES */

sealed class DialogueNode(
        val type: DialogueNodeType,
        text: String
) {

    val textProperty: StringProperty = SimpleStringProperty(text)

    val text: String
        get() = textProperty.value

    override fun toString(): String {
        return javaClass.simpleName
    }
}

class StartNode(text: String) : DialogueNode(DialogueNodeType.START, text)

class EndNode(text: String) : DialogueNode(DialogueNodeType.END, text)

class TextNode(text: String) : DialogueNode(DialogueNodeType.TEXT, text)

class FunctionNode(text: String) : DialogueNode(DialogueNodeType.FUNCTION, text)

class BranchNode(text: String) : DialogueNode(DialogueNodeType.BRANCH, text)

class ChoiceNode(text: String) : DialogueNode(DialogueNodeType.CHOICE, text)  {

    /**
     * Maps option id to option text.
     * Options start at id 0.
     * These are ids that are local to this choice node.
     */
    val options = hashMapOf<Int, StringProperty>()

    /**
     * Maps option id to option condition.
     * Options start at id 0.
     * These are ids that are local to this choice node.
     */
    val conditions = hashMapOf<Int, StringProperty>()

    /**
     * Returns last option id present in the options map, or -1 if there are no options.
     */
    val lastOptionID: Int
        get() = options.keys.max() ?: -1
}

/* EDGES */

open class DialogueEdge(val source: DialogueNode, val target: DialogueNode) {

    override fun toString(): String {
        return "$source -> $target"
    }
}

class DialogueChoiceEdge(source: DialogueNode, val optionID: Int, target: DialogueNode) : DialogueEdge(source, target) {
    override fun toString(): String {
        return "$source, $optionID -> $target"
    }
}

/* GRAPH */

/**
 * A simplified graph implementation with minimal integrity checks.
 */
class DialogueGraph(

        /**
         * Counter for node ids in this graph.
         */
        internal var uniqueID: Int = 0
) {

    val nodes = FXCollections.observableMap(hashMapOf<Int, DialogueNode>())
    val edges = FXCollections.observableArrayList<DialogueEdge>()

    val startNode: StartNode
        get() = nodes.values.find { it.type == DialogueNodeType.START } as? StartNode
                ?: throw IllegalStateException("No start node in this graph.")

    /**
     * Adds node to this graph.
     */
    fun addNode(node: DialogueNode) {
        nodes[uniqueID++] = node
    }

    /**
     * Removes the node from this graph, including incident edges.
     */
    fun removeNode(node: DialogueNode) {
        val id = findNodeID(node)

        nodes.remove(id)

        edges.removeIf { it.source === node || it.target === node }
    }

    /**
     * Adds a dialogue edge between [source] and [target].
     */
    fun addEdge(source: DialogueNode, target: DialogueNode) {
        edges += DialogueEdge(source, target)
    }

    /**
     * Adds a choice dialog edge between [source] and [target].
     */
    fun addChoiceEdge(source: DialogueNode, optionID: Int, target: DialogueNode) {
        // TODO: source has to be ChoiceNode, otherwise it does not make sense
        edges += DialogueChoiceEdge(source, optionID, target)
    }

    /**
     * Removes a dialogue edge between [source] and [target].
     */
    fun removeEdge(source: DialogueNode, target: DialogueNode) {
        edges.removeIf { it.source === source && it.target === target }
    }

    /**
     * Remove a choice dialogue edge between [source] and [target].
     */
    fun removeChoiceEdge(source: DialogueNode, optionID: Int, target: DialogueNode) {
        edges.removeIf { it is DialogueChoiceEdge
                && it.source === source
                && it.optionID == optionID
                && it.target === target
        }
    }

    /**
     * @return node id in this graph or -1 if node is not in this graph
     */
    fun findNodeID(node: DialogueNode): Int {
        for ((id, n) in nodes) {
            if (n === node)
                return id
        }

        return -1
    }

    fun getNodeByID(id: Int): DialogueNode {
        return nodes[id] ?: throw IllegalArgumentException("Graph does not contain a node with id $id")
    }

    fun nextNode(node: DialogueNode): DialogueNode? {
        return edges.find { it.source === node }?.target
    }

    fun nextNode(node: DialogueNode, optionID: Int): DialogueNode? {
        return edges.find { it is DialogueChoiceEdge && it.source === node && it.optionID == optionID }?.target
    }
}

