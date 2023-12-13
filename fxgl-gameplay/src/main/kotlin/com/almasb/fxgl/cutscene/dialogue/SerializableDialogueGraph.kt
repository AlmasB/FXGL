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

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */

/**
 * An equivalent of serialVersionUID.
 * Any changes to the serializable graph data structure need to increment this number by 1.
 */
private const val GRAPH_VERSION = 3

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
        val text: String,

        @JsonProperty("options")
        val options: List<SerializableOption>
) {

    var audio: String = ""
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class SerializableFunctionNode
@JsonCreator constructor(
        @JsonProperty("type")
        val type: DialogueNodeType,

        @JsonProperty("text")
        val text: String,

        @JsonProperty("numTimes")
        val numTimes: Int
) {

    var audio: String = ""
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class SerializableEdge
@JsonCreator constructor(

        @JsonProperty("sourceID")
        val sourceID: Int,

        @JsonProperty("optionID")
        val optionID: Int,

        @JsonProperty("targetID")
        val targetID: Int
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SerializableOption
@JsonCreator constructor(
        @JsonProperty("id")
        val id: Int,

        @JsonProperty("text")
        val text: String,

        @JsonProperty("condition")
        val condition: String
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

        @JsonProperty("startNodeID")
        val startNodeID: Int,

        @JsonProperty("textNodes")
        val textNodes: Map<Int, SerializableTextNode>,

        @JsonProperty("functionNodes")
        val functionNodes: Map<Int, SerializableFunctionNode>,

        @JsonProperty("edges")
        val edges: List<SerializableEdge>
) {

    var version: Int = GRAPH_VERSION

    val uiMetadata = hashMapOf<Int, SerializablePoint2D>()
}

/* SERIALIZABLE DATA STRUCTURES END */

object DialogueGraphSerializer {

    private val log = Logger.get(javaClass)

    fun toSerializable(graph: DialogueGraph): SerializableGraph {
        val textNodes = graph.nodes
                .filterValues { it.type == TEXT || it.type == BRANCH }
                .mapValues { (_, n) ->
                    val options: List<SerializableOption> = if (n is TextNode) n.options.toSerializable() else emptyList()

                    SerializableTextNode(n.type, n.text, options).also { it.audio = n.audioFileName }
                }

        val functionNodes = graph.nodes
                .filterValues { it.type == FUNCTION }
                .mapValues { (_, n) ->
                    val node = (n as FunctionNode)

                    SerializableFunctionNode(n.type, n.text, node.numTimes).also { it.audio = n.audioFileName }
                }

        val edges = graph.edges
                .map { SerializableEdge(graph.findNodeID(it.source), it.optionID, graph.findNodeID(it.target)) }

        return SerializableGraph(graph.uniqueID, graph.startNodeID, textNodes, functionNodes, edges)
    }

    private fun List<Option>.toSerializable(): List<SerializableOption> {
        return this.map { SerializableOption(it.id, it.text, it.condition) }
    }

    private fun List<SerializableOption>.toDeserializable(): List<Option> {
        return this.map { Option(it.id, it.text, it.condition) }
    }

    fun fromSerializable(sGraph: SerializableGraph): DialogueGraph {
        if (sGraph.version != GRAPH_VERSION) {
            log.warning("Deserializing graph with version=${sGraph.version}. Supported version: $GRAPH_VERSION")
        }

        val graph = DialogueGraph(sGraph.uniqueID)
        graph.startNodeID = sGraph.startNodeID

        sGraph.textNodes.forEach { (id, n) ->
            val node = when (n.type) {
                TEXT -> TextNode(n.text).also { it.options.setAll(n.options.toDeserializable()) }
                BRANCH -> BranchNode(n.text)
                SUBDIALOGUE -> SubDialogueNode(n.text)
                else -> throw IllegalArgumentException("Unknown node type: ${n.type}")
            }

            node.audioFileNameProperty.value = n.audio

            graph.nodes[id] = node
        }

        sGraph.functionNodes.forEach { (id, n) ->
            val node = FunctionNode(n.text)
            node.numTimesProperty.value = n.numTimes

            node.audioFileNameProperty.value = n.audio

            graph.nodes[id] = node
        }

        sGraph.edges.forEach {
            val source = graph.getNodeByID(it.sourceID)
            val target = graph.getNodeByID(it.targetID)

            graph.addEdge(source, it.optionID, target)
        }

        return graph
    }
}

