/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.tools.dialogues

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