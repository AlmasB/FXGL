/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.cutscene.dialogue

import com.almasb.fxgl.cutscene.dialogue.DialogueNodeType.*
import com.almasb.fxgl.logging.Logger
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import javafx.beans.property.SimpleStringProperty

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */

/**
 * An equivalent of serialVersionUID.
 * Any changes to the serializable graph data structure need to increment this number by 1.
 */
private const val GRAPH_VERSION = 2

/*
 * We can't use jackson-module-kotlin yet since no module-info.java is provided.
 * So we resort to manually annotating constructors.
 */

/* SERIALIZABLE DATA STRUCTURES BEGIN */

@JsonIgnoreProperties(ignoreUnknown = true)
data class SerializableTextNode
@JsonCreator constructor(
        @JsonProperty("type")
        val type: DialogueNodeType,

        @JsonProperty("text")
        val text: String
) {

    var audio: String = ""
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class SerializableChoiceNode
@JsonCreator constructor(
        @JsonProperty("type")
        val type: DialogueNodeType,

        @JsonProperty("text")
        val text: String,

        @JsonProperty("options")
        val options: Map<Int, String>,

        @JsonProperty("conditions")
        val conditions: Map<Int, String>
) {

    var audio: String = ""
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class SerializableEdge
@JsonCreator constructor(

        @JsonProperty("sourceID")
        val sourceID: Int,

        @JsonProperty("targetID")
        val targetID: Int
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SerializableChoiceEdge
@JsonCreator constructor(

        @JsonProperty("sourceID")
        val sourceID: Int,

        @JsonProperty("optionID")
        val optionID: Int,

        @JsonProperty("targetID")
        val targetID: Int
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SerializablePoint2D
@JsonCreator constructor(

        @JsonProperty("x")
        val x: Double,

        @JsonProperty("y")
        val y: Double
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SerializableGraph
@JsonCreator constructor(
        @JsonProperty("uniqueID")
        val uniqueID: Int,

        @JsonProperty("nodes")
        val nodes: Map<Int, SerializableTextNode>,

        @JsonProperty("choiceNodes")
        val choiceNodes: Map<Int, SerializableChoiceNode>,

        @JsonProperty("edges")
        val edges: List<SerializableEdge>,

        @JsonProperty("choiceEdges")
        val choiceEdges: List<SerializableChoiceEdge>
) {

    var version: Int = GRAPH_VERSION

    val uiMetadata = hashMapOf<Int, SerializablePoint2D>()
}

/* SERIALIZABLE DATA STRUCTURES END */

object DialogueGraphSerializer {

    private val log = Logger.get(javaClass)

    fun toSerializable(dialogueGraph: DialogueGraph): SerializableGraph {
        val nodesS = dialogueGraph.nodes
                .filterValues { it.type != CHOICE }
                .mapValues { (_, n) -> SerializableTextNode(n.type, n.text).also { it.audio = n.audioFileName } }

        val choiceNodesS = dialogueGraph.nodes
                .filterValues { it.type == CHOICE }
                .mapValues { (_, n) ->
                    SerializableChoiceNode(n.type, n.text, (n as ChoiceNode).options.mapValues { it.value.value }, n.conditions.mapValues { it.value.value })
                            .also { it.audio = n.audioFileName }
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
        if (sGraph.version != GRAPH_VERSION) {
            log.warning("Deserializing graph with version=${sGraph.version}. Supported version: $GRAPH_VERSION")
        }

        val graph = DialogueGraph(sGraph.uniqueID)
        sGraph.nodes.forEach { (id, n) ->
            val node = when (n.type) {
                START -> StartNode(n.text)
                END -> EndNode(n.text)
                TEXT -> TextNode(n.text)
                FUNCTION -> FunctionNode(n.text)
                BRANCH -> BranchNode(n.text)
                SUBDIALOGUE -> SubDialogueNode(n.text)
                else -> throw IllegalArgumentException("Unknown node type: ${n.type}")
            }

            node.audioFileNameProperty.value = n.audio

            graph.nodes[id] = node
        }

        sGraph.choiceNodes.forEach { (id, n) ->
            val node = ChoiceNode(n.text)

            n.options.forEach { option ->
                node.options[option.key] = SimpleStringProperty(option.value)
            }

            n.conditions.forEach { option ->
                node.conditions[option.key] = SimpleStringProperty(option.value)
            }

            node.audioFileNameProperty.value = n.audio

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

            graph.addChoiceEdge(source, it.optionID, target)
        }

        return graph
    }
}

