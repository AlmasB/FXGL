/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.tools.dialogues

import com.almasb.fxgl.cutscene.dialogue.*
import com.almasb.fxgl.dsl.FXGL
import com.almasb.fxgl.ui.FontType
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.control.TextField
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.Text

/**
 * TODO: refactor repetition
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

class FunctionNodeView(node: DialogueNode = FunctionNode("")) : NodeView(node) {

    val inLink = InLinkPoint(this)
    val outLink = OutLinkPoint(this)

    init {
        textArea.font = FXGL.getUIFactory().newFont(FontType.MONO, 16.0)

        addInPoint(inLink)
        addOutPoint(outLink)
    }
}

class BranchNodeView(node: DialogueNode = BranchNode("")) : NodeView(node) {

    val inLink = InLinkPoint(this)

    init {
        textArea.font = FXGL.getUIFactory().newFont(FontType.MONO, 16.0)

        addInPoint(inLink)

        for (i in 0..1) {

            val field = Text()
            field.text = if (i == 0) "true" else "false"
            field.fill = Color.WHITE
            field.font = Font.font(14.0)

            val outPoint = OutLinkPoint(this)
            outPoint.translateXProperty().bind(widthProperty().add(-25.0))
            outPoint.translateYProperty().bind(textArea.prefHeightProperty().add(48 + i * 28.0))

            outPoint.choiceOptionID = i

            outPoints.add(outPoint)

            addContent(field)

            children.add(outPoint)
        }

        prefHeightProperty().bind(outPoints.last().translateYProperty().add(35.0))

        contentRoot.alignment = Pos.CENTER_RIGHT
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

    private val conditions = arrayListOf<Condition>()

    init {
        addInPoint(InLinkPoint(this))

        val node = this.node as ChoiceNode

        if (node.options.isNotEmpty()) {

            node.options.forEach { i, optionText ->
                val field = TextField(optionText.value)

                val outPoint = OutLinkPoint(this)
                outPoint.translateXProperty().bind(widthProperty().add(-25.0))
                outPoint.translateYProperty().bind(textArea.prefHeightProperty().add(53 + i * 35.0))

                outPoint.choiceOptionID = i
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

                outPoint.choiceOptionID = i
                outPoint.choiceLocalOptionProperty.bind(field.textProperty())

                node.options[i] = SimpleStringProperty().also { it.bindBidirectional(field.textProperty()) }

                val condition = Condition()
                condition.prefWidth = 155.0
                condition.prefHeight = 16.0
                condition.translateX = -160.0
                condition.translateYProperty().bind(outPoint.translateYProperty())

                conditions += condition

                outPoints.add(outPoint)


                addContent(field)

                // TODO: implement
                //children.add(condition)
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
        btnAdd.translateY = 1.0

        val btnRemove = CustomButton("-")
        btnRemove.setOnMouseClicked {
            removeLastOption()
        }

        btnRemove.visibleProperty().bind(Bindings.size(outPoints).greaterThan(2))

        btnRemove.translateX = btnAdd.translateX
        btnRemove.translateYProperty().bind(prefHeightProperty().subtract(44))

        children.addAll(btnAdd, btnRemove)
    }

    private class Condition : StackPane() {
        init {
            styleClass.add("dialogue-editor-condition-view")

            val text = TextField()
            text.font = FXGL.getUIFactory().newFont(FontType.MONO, 12.0)
            text.text = "\$playerHP > 50"

            children.add(text)
        }
    }

    private fun addNewOption() {
        val node = this.node as ChoiceNode
        val nextID = node.lastOptionID + 1

        val field = TextField()
        field.promptText = "Choice $nextID"

        node.options[nextID] = SimpleStringProperty().also { it.bindBidirectional(field.textProperty()) }


        val outPoint = OutLinkPoint(this)
        outPoint.translateXProperty().bind(widthProperty().add(-25.0))
        outPoint.translateYProperty().bind(textArea.prefHeightProperty().add(53 + nextID * 35.0))

        outPoint.choiceOptionID = nextID
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

        node.options.remove(node.lastOptionID)

        prefHeightProperty().bind(outPoints.last().translateYProperty().add(35.0))
    }
}