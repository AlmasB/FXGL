/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.cutscene.dialogue

import javafx.beans.property.SimpleStringProperty
import java.lang.IllegalArgumentException

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */

object DialogueGraphSerializer {

    fun toSerializable(dialogueGraph: DialogueGraph): SerializableGraph {
        val nodesS = dialogueGraph.nodes
                .filter { it.type != DialogueNodeType.CHOICE }
                .map { SerializableTextNode(it.id, it.type, it.text) }

        val choiceNodesS = dialogueGraph.nodes
                .filter { it.type == DialogueNodeType.CHOICE }
                .map {
                    SerializableChoiceNode(it.id, it.type, it.text, (it as ChoiceNode).options.mapValues { it.value.value })
                }

        val edgesS = dialogueGraph.edges.map { SerializableEdge(it.source.id, it.target.id) }
        val choiceEdgesS = dialogueGraph.choiceEdges.map { SerializableChoiceEdge(it.source.id, it.optionID, it.target.id) }

        return SerializableGraph(dialogueGraph.uniqueID, nodesS, choiceNodesS, edgesS, choiceEdgesS)
    }

    fun fromSerializable(sGraph: SerializableGraph): DialogueGraph {
        // TODO: error checks?

        val graph = DialogueGraph(sGraph.uniqueID)
        sGraph.nodes.forEach {
            val node = when (it.type) {
                DialogueNodeType.START -> StartNode(it.text)
                DialogueNodeType.END -> EndNode(it.text)
                DialogueNodeType.TEXT -> TextNode(it.text)
                DialogueNodeType.FUNCTION -> FunctionNode(it.text)
                DialogueNodeType.BRANCH -> BranchNode(it.text)
                else -> throw IllegalArgumentException("Unknown node type: ${it.type}")
            }

            node.id = it.id
            graph.addNode(node)
        }

        sGraph.choiceNodes.forEach {
            val node = ChoiceNode(it.text)
            node.id = it.id

            it.options.forEach { option ->
                node.options[option.key] = SimpleStringProperty(option.value)
            }

            graph.addNode(node)
        }

        sGraph.edges.forEach {
            val source = graph.findNodeById(it.sourceID)!!
            val target = graph.findNodeById(it.targetID)!!

            graph.addEdge(source, target)
        }

        sGraph.choiceEdges.forEach {
            val source = graph.findNodeById(it.sourceID)!!
            val target = graph.findNodeById(it.targetID)!!

            graph.addEdge(source, it.optionID, target)
        }

        return graph
    }
}

data class SerializableTextNode(val id: Int, val type: DialogueNodeType, val text: String)

data class SerializableChoiceNode(val id: Int, val type: DialogueNodeType, val text: String, val options: Map<Int, String>)

data class SerializableEdge(val sourceID: Int, val targetID: Int)

data class SerializableChoiceEdge(val sourceID: Int, val optionID: Int, val targetID: Int)

data class SerializablePoint2D(val x: Double, val y: Double)

data class SerializableGraph(
        val uniqueID: Int,
        val nodes: List<SerializableTextNode>,
        val choiceNodes: List<SerializableChoiceNode>,

        val edges: List<SerializableEdge>,
        val choiceEdges: List<SerializableChoiceEdge>
) {

    val uiMetadata = hashMapOf<Int, SerializablePoint2D>()
}