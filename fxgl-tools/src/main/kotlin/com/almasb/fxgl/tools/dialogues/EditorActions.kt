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