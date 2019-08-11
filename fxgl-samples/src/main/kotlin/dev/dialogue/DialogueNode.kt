/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package dev.dialogue

import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty

enum class DialogueNodeType {
    START, END,
    TEXT, CHOICE
}

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
abstract class DialogueNode(val type: DialogueNodeType,
                            text: String) {

    val textProperty: StringProperty = SimpleStringProperty(text)

    val text: String
        get() = textProperty.value

    internal var id = -1
}

class StartNode(text: String) : DialogueNode(DialogueNodeType.START, text)
class EndNode(text: String) : DialogueNode(DialogueNodeType.END, text)

class TextNode(text: String) : DialogueNode(DialogueNodeType.TEXT, text)

class ChoiceNode(text: String, val options: List<DialogueNode>) : DialogueNode(DialogueNodeType.CHOICE, text) {

}

class DialogueEdge(val source: DialogueNode, val target: DialogueNode)

class DialogueGraph {
    private var uniqueID = 0

    val nodes = mutableListOf<DialogueNode>()
    val edges = mutableListOf<DialogueEdge>()

    fun addNode(node: DialogueNode) {
        nodes += node
        node.id = uniqueID++

        print()
    }

    fun removeNode(node: DialogueNode) {

    }

    fun addEdge(source: DialogueNode, target: DialogueNode) {
        edges += DialogueEdge(source, target)

        print()
    }

    fun removeEdge(source: DialogueNode, target: DialogueNode) {

    }

    fun print() {
        println("Printing graph")

        nodes.forEach {
            println("${it.type}, ${it.id}")
        }

        edges.forEach {
            println("${it.source.id} -> ${it.target.id}")
        }

        println()
    }
}