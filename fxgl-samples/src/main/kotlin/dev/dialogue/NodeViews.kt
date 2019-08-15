/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package dev.dialogue

import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.text.Font

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class TextNodeView(node: DialogueNode = TextNode("")) : NodeView(node) {

    val inLink = InLinkPoint(this)
    val outLink = OutLinkPoint(this)

    init {
        addInPoint(inLink)
        addOutPoint(outLink)
    }
}

class StartNodeView(node: DialogueNode = StartNode("")) : NodeView(node) {

    val outLink = OutLinkPoint(this)

    init {
        addOutPoint(outLink)
    }
}

class EndNodeView(node: DialogueNode = EndNode("")) : NodeView(node) {

    init {
        val inLink = InLinkPoint(this)
        addInPoint(inLink)
    }
}

class ChoiceNodeView(node: DialogueNode = ChoiceNode("")) : NodeView(node) {

    init {
        addInPoint(InLinkPoint(this))

        val node = this.node as ChoiceNode

        if (node.localOptions.isNotEmpty()) {

            node.localOptions.forEach { i, optionText ->
                val field = TextField(optionText.value)

                val outPoint = OutLinkPoint(this)
                outPoint.translateXProperty().bind(widthProperty().add(-25.0))
                outPoint.translateYProperty().bind(textArea.prefHeightProperty().add(53 + i * 35.0))

                outPoint.choiceLocalID = i
                outPoint.choiceLocalOptionProperty.bind(field.textProperty())

                optionText.bindBidirectional(field.textProperty())


                outPoints.add(outPoint)

                addContent(field)

                children.add(outPoint)
            }


        } else {

            for (i in 0..1) {

                val field = TextField()
                field.promptText = "Choice $i"

                val outPoint = OutLinkPoint(this)
                outPoint.translateXProperty().bind(widthProperty().add(-25.0))
                outPoint.translateYProperty().bind(textArea.prefHeightProperty().add(53 + i * 35.0))

                outPoint.choiceLocalID = i
                outPoint.choiceLocalOptionProperty.bind(field.textProperty())

                node.localIDs += i
                node.localOptions[i] = SimpleStringProperty().also { it.bindBidirectional(field.textProperty()) }


                outPoints.add(outPoint)


                addContent(field)

                children.add(outPoint)
            }
        }

        prefHeightProperty().bind(children[children.size - 1].translateYProperty().add(35.0))
    }
}