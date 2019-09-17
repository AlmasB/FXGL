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
}