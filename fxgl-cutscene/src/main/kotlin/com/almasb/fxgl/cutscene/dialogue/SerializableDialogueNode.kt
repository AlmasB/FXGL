/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.cutscene.dialogue

import javafx.beans.property.SimpleStringProperty

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */

data class SerializableTextNode(val type: DialogueNodeType, val text: String)

data class SerializableChoiceNode(val type: DialogueNodeType, val text: String, val options: Map<Int, String>)

data class SerializableEdge(val sourceID: Int, val targetID: Int)

data class SerializableChoiceEdge(val sourceID: Int, val optionID: Int, val targetID: Int)

data class SerializablePoint2D(val x: Double, val y: Double)

data class SerializableGraph(
        val uniqueID: Int,
        val nodes: Map<Int, SerializableTextNode>,
        val choiceNodes: Map<Int, SerializableChoiceNode>,

        val edges: List<SerializableEdge>,
        val choiceEdges: List<SerializableChoiceEdge>
) {

    val uiMetadata = hashMapOf<Int, SerializablePoint2D>()
}

object DialogueGraphSerializer {

    fun toSerializable(dialogueGraph: DialogueGraph): SerializableGraph {
        val nodesS = dialogueGraph.nodes
                .filterValues { it.type != DialogueNodeType.CHOICE }
                .mapValues { (_, n) -> SerializableTextNode(n.type, n.text) }

        val choiceNodesS = dialogueGraph.nodes
                .filterValues { it.type == DialogueNodeType.CHOICE }
                .mapValues { (_, n) ->
                    SerializableChoiceNode(n.type, n.text, (n as ChoiceNode).options.mapValues { it.value.value })
                }

        val edgesS = dialogueGraph.edges
                .filter { it !is DialogueChoiceEdge }
                .map { SerializableEdge(dialogueGraph.findNodeID(it.source), dialogueGraph.findNodeID(it.target)) }

        val choiceEdgesS = dialogueGraph.edges
                .filterIsInstance<DialogueChoiceEdge>()
                .map { SerializableChoiceEdge(dialogueGraph.findNodeID(it.source), it.optionID, dialogueGraph.findNodeID(it.target)) }

        return SerializableGraph(dialogueGraph.uniqueID, nodesS, choiceNodesS, edgesS, choiceEdgesS)
    }

    fun fromSerializable(sGraph: SerializableGraph): DialogueGraph {
        val graph = DialogueGraph(sGraph.uniqueID)
        sGraph.nodes.forEach { (id, n) ->
            val node = when (n.type) {
                DialogueNodeType.START -> StartNode(n.text)
                DialogueNodeType.END -> EndNode(n.text)
                DialogueNodeType.TEXT -> TextNode(n.text)
                DialogueNodeType.FUNCTION -> FunctionNode(n.text)
                DialogueNodeType.BRANCH -> BranchNode(n.text)
                else -> throw IllegalArgumentException("Unknown node type: ${n.type}")
            }

            graph.nodes[id] = node
        }

        sGraph.choiceNodes.forEach { (id, n) ->
            val node = ChoiceNode(n.text)

            n.options.forEach { option ->
                node.options[option.key] = SimpleStringProperty(option.value)
            }

            graph.nodes[id] = node
        }

        sGraph.edges.forEach {
            val source = graph.getNodeByID(it.sourceID)
            val target = graph.getNodeByID(it.targetID)

            graph.addEdge(source, target)
        }

        sGraph.choiceEdges.forEach {
            val source = graph.getNodeByID(it.sourceID)
            val target = graph.getNodeByID(it.targetID)

            graph.addEdge(source, it.optionID, target)
        }

        return graph
    }
}

