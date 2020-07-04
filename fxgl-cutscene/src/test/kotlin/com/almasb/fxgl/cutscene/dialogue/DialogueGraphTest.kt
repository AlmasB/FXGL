/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.cutscene.dialogue

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.contains
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

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
}