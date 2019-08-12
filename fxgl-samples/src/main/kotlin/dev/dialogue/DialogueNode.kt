/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package dev.dialogue

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import java.io.IOException
import java.io.Serializable

enum class DialogueNodeType {
    START, END,
    TEXT, CHOICE
}

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
abstract class DialogueNode(val type: DialogueNodeType,
                            text: String) {

    val textProperty: StringProperty = SimpleStringProperty(text)

    val text: String
        get() = textProperty.value

    internal var id = -1
}

class StartNode(text: String) : DialogueNode(DialogueNodeType.START, text)
class EndNode(text: String) : DialogueNode(DialogueNodeType.END, text)

class TextNode(text: String) : DialogueNode(DialogueNodeType.TEXT, text)

class ChoiceNode(text: String) : DialogueNode(DialogueNodeType.CHOICE, text)  {

    val localIDs = mutableListOf<Int>()
    val localOptions = hashMapOf<Int, String>()
}

class DialogueEdge(val source: DialogueNode, val target: DialogueNode)

class DialogueChoiceEdge(val source: ChoiceNode, val localID: Int, val target: DialogueNode)

class DialogueGraph : Serializable {
    private var uniqueID = 0

    val nodes = mutableListOf<DialogueNode>()
    val edges = mutableListOf<DialogueEdge>()
    val choiceEdges = mutableListOf<DialogueChoiceEdge>()

    fun addNode(node: DialogueNode) {
        nodes += node

        if (node.id == -1)
            node.id = uniqueID++

        print()
    }

    fun removeNode(node: DialogueNode) {

    }

    fun addEdge(source: DialogueNode, target: DialogueNode) {
        edges += DialogueEdge(source, target)

        print()
    }

    fun addEdge(source: ChoiceNode, localID: Int, localOption: String, target: DialogueNode) {
        choiceEdges += DialogueChoiceEdge(source, localID, target)

        source.localOptions[localID] = localOption

        print()
    }

    fun removeEdge(source: DialogueNode, target: DialogueNode) {

    }

    fun print() {
        println("Printing graph")

        nodes.forEach {
            println("${it.type}, ${it.id}")
        }

        edges.forEach {
            println("${it.source.id} -> ${it.target.id}")
        }

        choiceEdges.forEach {
            println("Choice: ${it.source.id}, ${it.localID} -> ${it.target.id}")
        }

        println()
    }

    fun findNodeById(id: Int): DialogueNode? {
        return nodes.find { it.id == id }
    }

    fun toSerializable(): SerializableGraph {
        val nodesS = nodes.filter { it.type != DialogueNodeType.CHOICE }.map { SerializableTextNode(it.id, it.type, it.text) }
        val choiceNodesS = nodes.filter { it.type == DialogueNodeType.CHOICE }.map { SerializableChoiceNode(it.id, it.type, it.text, (it as ChoiceNode).localIDs, it.localOptions) }

        val edgesS = edges.map { SerializableEdge(it.source.id, it.target.id) }
        val choiceEdgesS = choiceEdges.map { SerializableChoiceEdge(it.source.id, it.localID, it.target.id) }

        return SerializableGraph(nodesS, choiceNodesS, edgesS, choiceEdgesS)
    }
}


// SERIALIZATION

data class SerializableTextNode(val id: Int, val type: DialogueNodeType, val text: String)

data class SerializableChoiceNode(val id: Int, val type: DialogueNodeType, val text: String, val localIDs: List<Int>, val localOptions: Map<Int, String>)

data class SerializableEdge(val source: Int, val target: Int)

data class SerializableChoiceEdge(val source: Int, val localID: Int, val target: Int)

data class SerializableGraph(
        val nodes: List<SerializableTextNode>,
        val choiceNodes: List<SerializableChoiceNode>,

        val edges: List<SerializableEdge>,
        val choiceEdges: List<SerializableChoiceEdge>
) {

    fun toGraph(): DialogueGraph {
        // TODO: error checks?

        val graph = DialogueGraph()
        nodes.forEach {
            val node = when (it.type) {
                DialogueNodeType.START -> StartNode(it.text)
                DialogueNodeType.END -> EndNode(it.text)
                DialogueNodeType.TEXT -> TextNode(it.text)
                DialogueNodeType.CHOICE -> TODO()
            }

            node.id = it.id
            graph.addNode(node)
        }

        choiceNodes.forEach {
            val node = ChoiceNode(it.text)
            node.id = it.id
            node.localIDs += it.localIDs

            graph.addNode(node)
        }

        edges.forEach {
            val source = graph.findNodeById(it.source)!!
            val target = graph.findNodeById(it.target)!!

            graph.addEdge(source, target)
        }

        choiceEdges.forEach {
            val source = graph.findNodeById(it.source)!!
            val target = graph.findNodeById(it.target)!!

            graph.addEdge(source as ChoiceNode, it.localID, source.localOptions[it.localID]!!, target)
        }

        return graph
    }
}