/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package dev.dialogue

import com.almasb.fxgl.dsl.FXGL
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.Button
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
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

        prefHeightProperty().bind(outPoints.last().translateYProperty().add(35.0))

        addButtons()
    }

    private fun addButtons() {
        val btnAdd = CustomButton("+")
        btnAdd.setOnMouseClicked {
            addNewOption()
        }

        btnAdd.translateX = 9.0
        btnAdd.translateY = 5.0

        val btnRemove = CustomButton("-")
        btnRemove.setOnMouseClicked {
            removeLastOption()
        }

        btnRemove.visibleProperty().bind(Bindings.size(outPoints).greaterThan(2))

        btnRemove.translateX = btnAdd.translateX
        btnRemove.translateYProperty().bind(prefHeightProperty().subtract(39.5))

        children.addAll(btnAdd, btnRemove)
    }

    private class CustomButton(symbol: String) : StackPane() {

        init {
            val bg = Rectangle(20.0, 20.0, null)
            val text = FXGL.getUIFactory().newText(symbol, 24.0)

            bg.strokeProperty().bind(
                    Bindings.`when`(hoverProperty()).then(Color.WHITE).otherwise(Color.TRANSPARENT)
            )

            children.addAll(bg, text)
        }
    }

    private fun addNewOption() {
        val node = this.node as ChoiceNode
        val nextID = node.localIDs.last() + 1

        val field = TextField()
        field.promptText = "Choice $nextID"

        node.localIDs += nextID
        node.localOptions[nextID] = SimpleStringProperty().also { it.bindBidirectional(field.textProperty()) }


        val outPoint = OutLinkPoint(this)
        outPoint.translateXProperty().bind(widthProperty().add(-25.0))
        outPoint.translateYProperty().bind(textArea.prefHeightProperty().add(53 + nextID * 35.0))

        outPoint.choiceLocalID = nextID
        outPoint.choiceLocalOptionProperty.bind(field.textProperty())

        outPoints.add(outPoint)

        addContent(field)

        children.add(outPoint)

        prefHeightProperty().bind(outPoint.translateYProperty().add(35.0))
    }

    private fun removeLastOption() {
        contentRoot.children.removeAt(contentRoot.children.size - 1)
        val point = outPoints.removeAt(outPoints.size - 1)
        children -= point

        val node = this.node as ChoiceNode

        val lastID = node.localIDs.last()
        node.localIDs -= lastID
        node.localOptions.remove(lastID)

        prefHeightProperty().bind(outPoints.last().translateYProperty().add(35.0))
    }
}