/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.tools.dialogues

import com.almasb.fxgl.cutscene.dialogue.*
import com.almasb.fxgl.dsl.getUIFactoryService
import com.almasb.fxgl.ui.FontType
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.control.TextField
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.Text

/**
 * All specific node views.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */

class TextNodeView(node: DialogueNode = TextNode("")) : NodeView(node) {
    init {
        addInPoint(InLinkPoint(this))
        addOutPoint(OutLinkPoint(this))
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
    private val offsetY = 50

    private val choiceNode = node as ChoiceNode

    init {
        addInPoint(InLinkPoint(this))

        if (choiceNode.options.isNotEmpty()) {
            // load existing options
            choiceNode.options.keys.forEach { id ->
                addOptionView(id)
            }
        } else {
            // add the first two options
            repeat(2) {
                addNewOption()
            }
        }

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

    private fun addNewOption() {
        val nextID = choiceNode.addOption("")

        addOptionView(nextID)
    }

    private fun addOptionView(id: Int) {
        val prop = choiceNode.options[id]!!

        val field = TextField(prop.value)
        field.promptText = "Choice $id"

        prop.bindBidirectional(field.textProperty())

        val outPoint = OutLinkPoint(this)
        outPoint.translateXProperty().bind(widthProperty().add(-25.0))
        outPoint.translateYProperty().bind(textArea.prefHeightProperty().add(offsetY + id * 35.0))
        outPoint.choiceOptionID = id
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

        val lastID = choiceNode.lastOptionID

        choiceNode.options.remove(lastID)
        choiceNode.conditions.remove(lastID)

        prefHeightProperty().bind(outPoints.last().translateYProperty().add(35.0))
    }
}