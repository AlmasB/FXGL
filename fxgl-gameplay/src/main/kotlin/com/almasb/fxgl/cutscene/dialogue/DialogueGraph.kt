/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.cutscene.dialogue

import com.almasb.fxgl.core.Copyable
import com.almasb.fxgl.core.collection.PropertyMap
import com.almasb.fxgl.cutscene.dialogue.DialogueNodeType.*
import javafx.beans.property.IntegerProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.collections.FXCollections

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */

enum class DialogueNodeType {
    TEXT, SUBDIALOGUE, CHOICE, FUNCTION, BRANCH
}

/**
 * The context in which a dialogue is running.
 * For example, a single NPC could be a context.
 */
fun interface DialogueContext {

    /**
     * @return property map that is local to the dialogue context
     */
    fun properties(): PropertyMap
}

/* NODES */

sealed class DialogueNode(
        val type: DialogueNodeType,
        text: String
) : Copyable<DialogueNode> {

    val textProperty: StringProperty = SimpleStringProperty(text)

    val text: String
        get() = textProperty.value

    val audioFileNameProperty: StringProperty = SimpleStringProperty("")

    val audioFileName: String
        get() = audioFileNameProperty.value

    override fun toString(): String {
        return javaClass.simpleName
    }
}

class TextNode(text: String) : DialogueNode(TEXT, text) {
    override fun copy(): TextNode = TextNode(text)
}

class SubDialogueNode(text: String) : DialogueNode(SUBDIALOGUE, text) {
    override fun copy(): SubDialogueNode = SubDialogueNode(text)
}

class FunctionNode(text: String) : DialogueNode(FUNCTION, text) {

    /**
     * Number of times this function can be called.
     * Once the number of calls for this function has been reached, the function is skipped.
     * Default value: -1 for unlimited.
     */
    val numTimesProperty: IntegerProperty = SimpleIntegerProperty(-1)

    val numTimes: Int
        get() = numTimesProperty.value

    override fun copy(): FunctionNode {
        val copy = FunctionNode(text)
        copy.numTimesProperty.value = numTimesProperty.value
        return copy
    }
}

class BranchNode(text: String) : DialogueNode(BRANCH, text) {
    override fun copy(): BranchNode = BranchNode(text)
}

class ChoiceNode(text: String) : DialogueNode(CHOICE, text)  {

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
        get() = options.keys.maxOrNull() ?: -1

    fun addOption(text: String): Int {
        return addOption(text, "")
    }

    /**
     * Adds a new option and a new condition associated with the option.
     * @return id of the new option
     */
    fun addOption(text: String, condition: String): Int {
        val id = lastOptionID + 1

        options[id] = SimpleStringProperty(text)
        conditions[id] = SimpleStringProperty(condition)

        return id
    }

    override fun copy(): ChoiceNode {
        val copy = ChoiceNode(text)
        options.forEach { (k, v) ->
            copy.options[k] = SimpleStringProperty(v.value)
        }
        conditions.forEach { (k, v) ->
            copy.conditions[k] = SimpleStringProperty(v.value)
        }
        return copy
    }
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

    val startNodeIDProperty: IntegerProperty = SimpleIntegerProperty(0)

    var startNodeID: Int
        get() = startNodeIDProperty.value
        set(value) { startNodeIDProperty.value = value }

    val startNode: DialogueNode
        get() = getNodeByID(startNodeID)

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

    fun addEdge(edge: DialogueEdge) {
        edges += edge
    }

    fun removeEdge(edge: DialogueEdge) {
        edges -= edge
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

    fun containsNode(node: DialogueNode): Boolean = node in nodes.values

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

    fun appendGraph(source: DialogueNode, target: DialogueNode, graph: DialogueGraph) {
//        val start = graph.startNode
//        val endNodes = graph.nodes.values.filter { it.type == END }
//
//        // convert start and end nodes into text nodes and add them to this graph
//
//        val newStart = TextNode(start.text)
//        val newEndNodes = endNodes.map { TextNode(it.text) }
//
//        addNode(newStart)
//        newEndNodes.forEach { addNode(it) }
//
//        // add the rest of the nodes "as is" to this graph
//        graph.nodes.values
//                .minus(start)
//                .minus(endNodes)
//                .forEach { addNode(it) }
//
//        // add the "internal" graph edges to this graph
//        graph.edges
//                .filter { containsNode(it.source) && containsNode(it.target) }
//                .forEach {
//                    if (it is DialogueChoiceEdge) {
//                        addChoiceEdge(it.source, it.optionID, it.target)
//                    } else {
//                        addEdge(it.source, it.target)
//                    }
//                }
//
//        // add the "external" graph edges
//        // form new chain source -> start -> ... -> endNodes -> target
//
//        addEdge(source, newStart)
//        newEndNodes.forEach { addEdge(it, target) }
//
//        addEdge(newStart, graph.nextNode(start)!!)
//        newEndNodes.forEach { endNode ->
//            graph.edges
//                    .filter { it.target.type == END }
//                    .forEach {
//                        if (it is DialogueChoiceEdge) {
//                            addChoiceEdge(it.source, it.optionID, endNode)
//                        } else {
//                            addEdge(it.source, endNode)
//                        }
//                    }
//        }
    }

    /**
     * @return a shallow copy of the graph (i.e. nodes and edges are the same references as in this graph)
     */
    fun copy(): DialogueGraph {
        val copy = DialogueGraph(uniqueID)
        copy.startNodeID = startNodeID
        copy.nodes.putAll(nodes)
        copy.edges.addAll(edges)
        return copy
    }
}

