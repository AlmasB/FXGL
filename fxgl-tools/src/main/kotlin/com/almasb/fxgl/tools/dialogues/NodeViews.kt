/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.tools.dialogues

import com.almasb.fxgl.cutscene.dialogue.*
import com.almasb.fxgl.dsl.FXGL
import com.almasb.fxgl.dsl.getUIFactoryService
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
 * All specific node views.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */

class StartNodeView(node: DialogueNode = StartNode("")) : NodeView(node) {
    init {
        addOutPoint(OutLinkPoint(this))

        addAudioField()
    }
}

class EndNodeView(node: DialogueNode = EndNode("")) : NodeView(node) {
    init {
        addInPoint(InLinkPoint(this))

        addAudioField()
    }
}

class TextNodeView(node: DialogueNode = TextNode("")) : NodeView(node) {
    init {
        addInPoint(InLinkPoint(this))
        addOutPoint(OutLinkPoint(this))

        addAudioField()
    }
}

class SubDialogueNodeView(node: DialogueNode = SubDialogueNode("")) : NodeView(node) {
    init {
        addInPoint(InLinkPoint(this))
        addOutPoint(OutLinkPoint(this))
    }
}

class FunctionNodeView(node: DialogueNode = FunctionNode("")) : NodeView(node) {
    init {
        addInPoint(InLinkPoint(this))
        addOutPoint(OutLinkPoint(this))

        textArea.font = getUIFactoryService().newFont(FontType.MONO, 16.0)
    }
}

class BranchNodeView(node: DialogueNode = BranchNode("")) : NodeView(node) {

    init {
        addInPoint(InLinkPoint(this))

        textArea.font = getUIFactoryService().newFont(FontType.MONO, 16.0)

        for (i in 0..1) {

            val field = Text()
            field.text = if (i == 0) "true" else "false"
            field.fill = Color.WHITE
            field.font = Font.font(14.0)

            addContent(field)

            val outPoint = OutLinkPoint(this)
            outPoint.choiceOptionID = i

            addOutPoint(outPoint)

            outPoint.translateYProperty().bind(textArea.prefHeightProperty().add(48 + i * 28.0))
        }

        prefHeightProperty().bind(outPoints.last().translateYProperty().add(35.0))

        contentRoot.alignment = Pos.CENTER_RIGHT
    }
}








class ChoiceNodeView(node: DialogueNode = ChoiceNode("")) : NodeView(node) {

    // this tells us how far the outPoints should be from internal content
    private val offsetY = 84

    private val conditions = arrayListOf<Condition>()

    init {
        addInPoint(InLinkPoint(this))

        addAudioField()

        val node = this.node as ChoiceNode

        if (node.options.isNotEmpty()) {

            node.options.forEach { i, optionText ->
                val field = TextField(optionText.value)

                val outPoint = OutLinkPoint(this)
                outPoint.translateXProperty().bind(widthProperty().add(-25.0))
                outPoint.translateYProperty().bind(textArea.prefHeightProperty().add(offsetY + i * 35.0))

                outPoint.choiceOptionID = i
                outPoint.choiceLocalOptionProperty.bind(field.textProperty())

                optionText.bindBidirectional(field.textProperty())

                val condition = Condition()
                condition.text.text = node.conditions[i]!!.value
                condition.prefWidth = 155.0
                condition.prefHeight = 16.0
                condition.translateX = -160.0
                condition.translateYProperty().bind(outPoint.translateYProperty().add(-6))

                node.conditions[i]!!.bindBidirectional(condition.text.textProperty())

                conditions += condition

                outPoints.add(outPoint)

                addContent(field)

                children.add(condition)
                children.add(outPoint)
            }


        } else {

            for (i in 0..1) {

                val field = TextField()
                field.promptText = "Choice $i"

                val outPoint = OutLinkPoint(this)
                outPoint.translateXProperty().bind(widthProperty().add(-25.0))
                outPoint.translateYProperty().bind(textArea.prefHeightProperty().add(offsetY + i * 35.0))

                outPoint.choiceOptionID = i
                outPoint.choiceLocalOptionProperty.bind(field.textProperty())

                node.options[i] = SimpleStringProperty().also { it.bindBidirectional(field.textProperty()) }

                val condition = Condition()
                condition.prefWidth = 155.0
                condition.prefHeight = 16.0
                condition.translateX = -160.0
                condition.translateYProperty().bind(outPoint.translateYProperty().add(-6))

                node.conditions[i] = SimpleStringProperty().also { it.bindBidirectional(condition.text.textProperty()) }

                conditions += condition

                outPoints.add(outPoint)


                addContent(field)

                children.add(condition)
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
        val text = TextField()

        init {
            styleClass.add("dialogue-editor-condition-view")

            text.font = FXGL.getUIFactoryService().newFont(FontType.MONO, 12.0)
            text.promptText = "condition"

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
        outPoint.translateYProperty().bind(textArea.prefHeightProperty().add(offsetY + nextID * 35.0))

        outPoint.choiceOptionID = nextID
        outPoint.choiceLocalOptionProperty.bind(field.textProperty())

        val condition = Condition()
        condition.prefWidth = 155.0
        condition.prefHeight = 16.0
        condition.translateX = -160.0
        condition.translateYProperty().bind(outPoint.translateYProperty().add(-6))

        node.conditions[nextID] = SimpleStringProperty().also { it.bindBidirectional(condition.text.textProperty()) }

        conditions += condition

        outPoints.add(outPoint)

        addContent(field)

        children.add(condition)
        children.add(outPoint)

        prefHeightProperty().bind(outPoint.translateYProperty().add(35.0))
    }

    private fun removeLastOption() {
        contentRoot.children.removeAt(contentRoot.children.size - 1)

        val point = outPoints.removeAt(outPoints.size - 1)
        children -= point

        val condition = conditions.removeAt(conditions.size - 1)
        children -= condition

        val node = this.node as ChoiceNode

        val lastID = node.lastOptionID

        node.options.remove(lastID)
        node.conditions.remove(lastID)

        prefHeightProperty().bind(outPoints.last().translateYProperty().add(35.0))
    }
}