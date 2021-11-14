/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.tools.dialogues

import com.almasb.fxgl.cutscene.dialogue.DialogueEdge
import com.almasb.fxgl.cutscene.dialogue.DialogueGraph
import com.almasb.fxgl.cutscene.dialogue.DialogueNode

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */

interface EditorAction {

    fun run()

    fun undo()
}

// possible actions:
// * move node
// * add node
// * remove node (and its incident edges)
// * add edge
// * remove edge

class MoveNodeAction(
        private val nodeView: NodeView,
        private val startX: Double,
        private val startY: Double,
        private val endX: Double,
        private val endY: Double
) : EditorAction {

    override fun run() {
        nodeView.layoutX = endX
        nodeView.layoutY = endY
    }

    override fun undo() {
        nodeView.layoutX = startX
        nodeView.layoutY = startY
    }
}

class AddNodeAction(
        private val graph: DialogueGraph,
        private val node: DialogueNode
) : EditorAction {

    override fun run() {
        graph.addNode(node)
    }

    override fun undo() {
        graph.removeNode(node)
    }

    override fun toString(): String {
        return "AddNode(${node.type})"
    }
}

//class RemoveNodeAction(
//        private val graph: DialogueGraph,
//        private val node: DialogueNode
//) : EditorAction {
//
//    private val edges = arrayListOf<DialogueEdge>()
//
//    override fun run() {
//        graph.edges.filter { it.source === node || it.target === node }
//                .forEach { edges += it }
//
//        graph.removeNode(node)
//    }
//
//    override fun undo() {
//        graph.addNode(node)
//
//        edges.forEach {
//            if (it.target === node) {
//                graph.addEdge()
//
//                graph.addChoiceEdge()
//            }
//        }
//    }
//}

class AddEdgeAction(
        private val graph: DialogueGraph,
        private val source: DialogueNode,
        private val target: DialogueNode
) : EditorAction {

    override fun run() {
        graph.addEdge(source, target)
    }

    override fun undo() {
        graph.removeEdge(source, target)
    }
}

class AddChoiceEdgeAction(
        private val graph: DialogueGraph,
        private val source: DialogueNode,
        private val optionID: Int,
        private val target: DialogueNode
) : EditorAction {

    override fun run() {
        graph.addChoiceEdge(source, optionID, target)
    }

    override fun undo() {
        graph.removeChoiceEdge(source, optionID, target)
    }
}

class RemoveEdgeAction(
        private val graph: DialogueGraph,
        private val source: DialogueNode,
        private val target: DialogueNode
) : EditorAction {

    override fun run() {
        graph.removeEdge(source, target)
    }

    override fun undo() {
        graph.addEdge(source, target)
    }
}

class RemoveChoiceEdgeAction(
        private val graph: DialogueGraph,
        private val source: DialogueNode,
        private val optionID: Int,
        private val target: DialogueNode
) : EditorAction {

    override fun run() {
        graph.removeChoiceEdge(source, optionID, target)
    }

    override fun undo() {
        graph.addChoiceEdge(source, optionID, target)
    }
}