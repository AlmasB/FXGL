/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.cutscene.dialogue

import javafx.beans.property.SimpleStringProperty
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class SerializableDialogueGraphTest {

    @Test
    fun `Serialization to and from`() {
        val choice = TextNode("test choice")
        val function = FunctionNode("test function")
        val end = TextNode("test end")
        val text = TextNode("test text")
        val branch = BranchNode("test branch")
        val subdialogue = TextNode("test subdialogue")

        choice.options[0].textProperty.value = "Option 1"
        choice.options[0].conditionProperty.value = ""
        choice.addOption("Option 2", "hasItem 5000")

        val graph = DialogueGraph()

        // nodes
        graph.addNode(choice)
        graph.addNode(function)
        graph.addNode(end)
        graph.addNode(text)
        graph.addNode(branch)
        graph.addNode(subdialogue)

        // edges
        graph.addEdge(choice, 0, function)
        graph.addEdge(choice, 1, end)
        graph.addEdge(branch, 0, text)
        graph.addEdge(branch, 1, end)
        graph.addEdge(function, end)
        graph.addEdge(text, subdialogue)
        graph.addEdge(subdialogue, end)

        val sGraph = DialogueGraphSerializer.toSerializable(graph)

        assertThat(sGraph.version, greaterThan(0))
        sGraph.textNodes.forEach {
            assertThat(it.value.type, `is`(not(DialogueNodeType.FUNCTION)))
        }

        sGraph.functionNodes.forEach {
            assertThat(it.value.type, `is`(DialogueNodeType.FUNCTION))
        }

        sGraph.version++

        val copy = DialogueGraphSerializer.fromSerializable(sGraph)

        assertThat(copy.uniqueID, `is`(graph.uniqueID))
        assertThat(copy.nodes.mapValues { it.toString() }, `is`(graph.nodes.mapValues { it.toString() }))

        val edges1 = graph.edges.map { it.toString() }
        val edges2 = copy.edges.map { it.toString() }

        assertThat(edges1, containsInAnyOrder(*edges2.toTypedArray()))
    }
}