/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.cutscene.dialogue

import com.almasb.fxgl.cutscene.dialogue.DialogueNodeType.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.contains
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
        assertThat(StartNode("").type, `is`(START))
        assertThat(EndNode("").type, `is`(END))
        assertThat(TextNode("").type, `is`(TEXT))
        assertThat(SubDialogueNode("").type, `is`(SUBDIALOGUE))
        assertThat(FunctionNode("").type, `is`(FUNCTION))
        assertThat(BranchNode("").type, `is`(BRANCH))
        assertThat(ChoiceNode("").type, `is`(CHOICE))

        assertThat(StartNode("").toString(), `is`("StartNode"))
        assertThat(EndNode("").toString(), `is`("EndNode"))

        assertThat(StartNode("StartText").text, `is`("StartText"))
        assertThat(EndNode("EndText").text, `is`("EndText"))
    }

    @Test
    fun `Edge toString`() {
        val node1 = TextNode("")
        val node2 = TextNode("")

        assertThat(DialogueEdge(node1, node2).toString(), `is`("TextNode -> TextNode"))

        val node3 = ChoiceNode("")

        assertThat(DialogueChoiceEdge(node3, 0, node2).toString(), `is`("ChoiceNode, 0 -> TextNode"))
    }

    @Test
    fun `Graph start node`() {
        assertThrows<IllegalStateException> {
            graph.startNode
        }

        val start = StartNode("")

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
        val node1 = ChoiceNode("")
        val node2 = TextNode("")

        graph.addNode(node1)
        graph.addNode(node2)

        assertTrue(graph.edges.isEmpty())

        graph.addChoiceEdge(node1, 0, node2)

        assertThat(graph.edges.size, `is`(1))
        assertThat(graph.edges[0].source, `is`<DialogueNode>(node1))
        assertThat(graph.edges[0].target, `is`<DialogueNode>(node2))
        assertThat((graph.edges[0] as DialogueChoiceEdge).optionID, `is`(0))

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
        val choiceEdge = DialogueChoiceEdge(node1, 0, node2)

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
        assertThat((graph.edges[0] as DialogueChoiceEdge).optionID, `is`(0))

        graph.removeEdge(choiceEdge)

        assertTrue(graph.edges.isEmpty())
    }

    @Test
    fun `Copy`() {
        val node1 = ChoiceNode("")
        val node2 = TextNode("")

        graph.addNode(node1)
        graph.addNode(node2)
        graph.addChoiceEdge(node1, 0, node2)

        val copy = graph.copy()

        assertThat(copy.uniqueID, `is`(graph.uniqueID))
        assertThat(copy.nodes, `is`(graph.nodes))
        assertThat(copy.edges, `is`(graph.edges))
    }
}