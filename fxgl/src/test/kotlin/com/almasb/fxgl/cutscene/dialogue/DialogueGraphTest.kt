/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.cutscene.dialogue

import com.almasb.fxgl.cutscene.dialogue.DialogueNodeType.*
import javafx.beans.property.SimpleStringProperty
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class DialogueGraphTest {

    private lateinit var graph: DialogueGraph

    @BeforeEach
    fun setUp() {
        graph = DialogueGraph()
    }

    @Test
    fun `Node types`() {
        assertThat(SubDialogueNode("").type, `is`(SUBDIALOGUE))
        assertThat(FunctionNode("").type, `is`(FUNCTION))
        assertThat(BranchNode("").type, `is`(BRANCH))
        assertThat(TextNode("").type, `is`(TEXT))

        assertThat(TextNode("").toString(), `is`("TextNode"))

        assertThat(TextNode("StartText").text, `is`("StartText"))
    }

    @Test
    fun `Edge toString`() {
        val node1 = TextNode("")
        val node2 = TextNode("")

        assertThat(DialogueEdge(node1, node2).toString(), `is`("TextNode, 0 -> TextNode"))

        val node3 = TextNode("")

        assertThat(DialogueEdge(node3, 1, node2).toString(), `is`("TextNode, 1 -> TextNode"))
    }

    @Test
    fun `Graph start node`() {
        assertThrows<IllegalArgumentException> {
            graph.startNode
        }

        val start = TextNode("")

        graph.addNode(start)

        assertThat(graph.startNode, `is`(start))
    }

    @Test
    fun `Add and remove nodes`() {
        val node = TextNode("")

        assertThat(graph.findNodeID(node), `is`(-1))

        graph.addNode(node)

        assertThat(graph.findNodeID(node), `is`(0))
        assertThat(graph.nodes.values, contains<DialogueNode>(node))

        graph.removeNode(node)

        assertThat(graph.findNodeID(node), `is`(-1))
        assertTrue(graph.nodes.isEmpty())
    }

    @Test
    fun `Add and remove edges`() {
        val node1 = TextNode("")
        val node2 = TextNode("")

        graph.addNode(node1)
        graph.addNode(node2)

        assertTrue(graph.edges.isEmpty())

        graph.addEdge(node1, node2)

        assertThat(graph.edges.size, `is`(1))
        assertThat(graph.edges[0].source, `is`<DialogueNode>(node1))
        assertThat(graph.edges[0].target, `is`<DialogueNode>(node2))

        // reverse won't work since it's a directed graph
        graph.removeEdge(node2, node1)

        assertThat(graph.edges.size, `is`(1))

        graph.removeEdge(node1, node2)

        assertTrue(graph.edges.isEmpty())
    }

    @Test
    fun `Add and remove choice edges`() {
        val node1 = TextNode("")
        val node2 = TextNode("")

        graph.addNode(node1)
        graph.addNode(node2)

        assertTrue(graph.edges.isEmpty())

        graph.addEdge(node1, 0, node2)

        assertThat(graph.edges.size, `is`(1))
        assertThat(graph.edges[0].source, `is`<DialogueNode>(node1))
        assertThat(graph.edges[0].target, `is`<DialogueNode>(node2))
        assertThat(graph.edges[0].optionID, `is`(0))

        // reverse won't work since it's a directed graph
        graph.removeChoiceEdge(node2, 0, node1)

        assertThat(graph.edges.size, `is`(1))

        graph.removeChoiceEdge(node1, 0, node2)

        assertTrue(graph.edges.isEmpty())
    }

    @Test
    fun `Add and remove edges via edge object`() {
        val node1 = TextNode("")
        val node2 = TextNode("")
        val edge = DialogueEdge(node1, node2)
        val choiceEdge = DialogueEdge(node1, 0, node2)

        graph.addNode(node1)
        graph.addNode(node2)
        graph.addEdge(edge)

        assertThat(graph.edges.size, `is`(1))
        assertThat(graph.edges[0].source, `is`<DialogueNode>(node1))
        assertThat(graph.edges[0].target, `is`<DialogueNode>(node2))

        graph.removeEdge(edge)

        assertTrue(graph.edges.isEmpty())

        // choice

        graph.addEdge(choiceEdge)

        assertThat(graph.edges.size, `is`(1))
        assertThat(graph.edges[0].source, `is`<DialogueNode>(node1))
        assertThat(graph.edges[0].target, `is`<DialogueNode>(node2))
        assertThat(graph.edges[0].optionID, `is`(0))

        graph.removeEdge(choiceEdge)

        assertTrue(graph.edges.isEmpty())
    }

    @Test
    fun `Graph contains node`() {
        val node1 = TextNode("")

        assertFalse(graph.containsNode(node1))

        graph.addNode(node1)

        assertTrue(graph.containsNode(node1))
    }

    @Test
    fun `Copy nodes`() {
        listOf(
            SubDialogueNode("TestText"),
            FunctionNode("TestText"),
            BranchNode("TestText"),
            TextNode("TestText")
        ).forEach {
            val copy = it.copy()
            assertThat(it.text, `is`(copy.text))
            assertThat(it.type, `is`(copy.type))
        }
    }

    @Test
    fun `Copy choice options`() {
        val choice = TextNode("Choice")

        choice.addOption("Choice A", "Condition A")
        choice.addOption("Choice B", "Condition B")
        choice.addOption("Choice C", "Condition C")

        val copy = choice.copy()

        assertThat(choice.lastOptionID, `is`(copy.lastOptionID))

        // StringProperty has to be a deep copy, not shallow
        assertThat(choice.options, `is`(not(copy.options)))
        assertThat(choice.options.size, `is`(copy.options.size))

        choice.options.forEachIndexed { index, option ->
            assertThat(option.id, `is`(copy.options[index].id))
            assertThat(option.text, `is`(copy.options[index].text))
            assertThat(option.condition, `is`(copy.options[index].condition))
        }
    }

    @Test
    fun `Copy`() {
        val node1 = TextNode("")
        val node2 = TextNode("")

        graph.addNode(node1)
        graph.addNode(node2)
        graph.addEdge(node1, 0, node2)

        val copy = graph.copy()

        assertThat(copy.uniqueID, `is`(graph.uniqueID))
        assertThat(copy.startNodeID, `is`(graph.startNodeID))
        assertThat(copy.nodes, `is`(graph.nodes))
        assertThat(copy.edges, `is`(graph.edges))
    }
}