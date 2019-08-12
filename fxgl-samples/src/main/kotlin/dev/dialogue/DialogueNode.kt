/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package dev.dialogue

import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import java.io.IOException
import java.io.Serializable

enum class DialogueNodeType {
    START, END,
    TEXT, CHOICE
}

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
abstract class DialogueNode(var type: DialogueNodeType,
                            text: String) : Serializable {

    constructor() : this(DialogueNodeType.START, "")

    @Transient
    val textProperty: StringProperty = SimpleStringProperty(text)

    val text: String
        get() = textProperty.value

    internal var id = -1

    @Throws(IOException::class)
    private fun writeObject(out: java.io.ObjectOutputStream) {
        out.writeInt(id)
        out.writeObject(text)
        out.writeObject(type)
    }

    @Throws(IOException::class, ClassNotFoundException::class)
    private fun readObject(`in`: java.io.ObjectInputStream) {
        id = `in`.readInt()
        textProperty.value = `in`.readObject() as String
        type = `in`.readObject() as DialogueNodeType
    }
}

class StartNode(text: String) : DialogueNode(DialogueNodeType.START, text)
class EndNode(text: String) : DialogueNode(DialogueNodeType.END, text)

class TextNode(text: String) : DialogueNode(DialogueNodeType.TEXT, text)

class ChoiceNode(text: String) : DialogueNode(DialogueNodeType.CHOICE, text)  {

    val localIDs = mutableListOf<Int>()
}

class DialogueEdge(val source: DialogueNode, val target: DialogueNode) : Serializable

class DialogueChoiceEdge(val source: ChoiceNode, val localID: Int, val target: DialogueNode) : Serializable

class DialogueGraph : Serializable {
    private var uniqueID = 0

    val nodes = mutableListOf<DialogueNode>()
    val edges = mutableListOf<DialogueEdge>()
    val choiceEdges = mutableListOf<DialogueChoiceEdge>()

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

    fun addEdge(source: ChoiceNode, localID: Int, target: DialogueNode) {
        choiceEdges += DialogueChoiceEdge(source, localID, target)

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

        choiceEdges.forEach {
            println("Choice: ${it.source.id}, ${it.localID} -> ${it.target.id}")
        }

        println()
    }
}