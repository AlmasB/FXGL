/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.cutscene.dialogue

import org.hamcrest.MatcherAssert.*
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class SerializableDialogueGraphTest {

    @Test
    fun `Serialization to and from`() {
        val start = StartNode("test start")
        val choice = ChoiceNode("test choice")
        val function = FunctionNode("test function")
        val end = EndNode("test end")

        val graph = DialogueGraph()
        graph.addNode(start)
        graph.addNode(choice)
        graph.addNode(function)
        graph.addNode(end)

        graph.addEdge(start, choice)
        graph.addChoiceEdge(choice, 0, function)
        graph.addChoiceEdge(choice, 1, end)
        graph.addEdge(function, end)

        val sGraph = DialogueGraphSerializer.toSerializable(graph)

        val copy = DialogueGraphSerializer.fromSerializable(sGraph)

        assertThat(copy.uniqueID, `is`(graph.uniqueID))
        assertThat(copy.nodes.mapValues { it.toString() }, `is`(graph.nodes.mapValues { it.toString() }))

        val edges1 = graph.edges.map { it.toString() }
        val edges2 = copy.edges.map { it.toString() }

        assertThat(edges1, containsInAnyOrder(*edges2.toTypedArray()))
    }
}