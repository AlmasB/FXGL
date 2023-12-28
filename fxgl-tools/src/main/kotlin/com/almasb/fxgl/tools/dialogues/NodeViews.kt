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
import javafx.collections.ListChangeListener
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.TextField
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.Text

/**
 * All specific node views.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */

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
            outPoint.choiceOptionIDProperty.value = i

            addOutPoint(outPoint)

            outPoint.translateYProperty().bind(textArea.prefHeightProperty().add(48 + i * 28.0))
        }

        prefHeightProperty().bind(outPoints.last().translateYProperty().add(35.0))

        contentRoot.alignment = Pos.CENTER_RIGHT
    }
}

class TextNodeView(node: DialogueNode = TextNode("")) : NodeView(node) {

    // this tells us how far the outPoints should be from internal content
    private val offsetY = 50

    private val textNode = node as TextNode

    //private val optionViewData = hashMapOf<Int, MutableList<Node>>()

    init {
        addInPoint(InLinkPoint(this))
        inPoint?.let {
            it.translateYProperty().unbind()
            it.translateY = 50.0
        }

        textNode.options.addListener(ListChangeListener {
            while (it.next()) {
                it.removed.forEach {

                }

                it.addedSubList.forEach {
                    addOptionView(it)
                }
            }
        })

        // load existing options
        textNode.options.forEach {
            addOptionView(it)
        }

        addButtons()
    }

    private fun addButtons() {
        val btnAdd = CustomButton("+")
        btnAdd.setOnMouseClicked {
            textNode.addOption("")
        }

        btnAdd.translateX = 9.0
        btnAdd.translateY = 1.0

        children.addAll(btnAdd)
    }

    private fun addOptionView(option: Option) {
        val views = ArrayList<Node>()

        val field = TextField(option.text)
        field.promptTextProperty().bind(option.idProperty.asString("Choice %d"))

        option.textProperty.bindBidirectional(field.textProperty())

        val outPoint = OutLinkPoint(this)
        outPoint.translateXProperty().bind(widthProperty().add(-25.0))
        outPoint.translateYProperty().bind(textArea.prefHeightProperty().add(option.idProperty.multiply(35).add(offsetY)))
        outPoint.choiceOptionIDProperty.bind(option.idProperty)
        outPoint.choiceLocalOptionProperty.bind(field.textProperty())

        outPoints.add(outPoint)

        addContent(field)

        children.add(outPoint)

        views += field
        views += outPoint

        // remove option button
        if (option.id > 0) {
            val btnRemove = CustomButton("-")
            btnRemove.setOnMouseClicked {
                textNode.removeOption(option.id)

                outPoints.remove(outPoint)

                views.forEach {
                    children -= it
                    removeContent(it)
                }

                prefHeightProperty().bind(outPoints.last().translateYProperty().add(35.0))
            }

            btnRemove.translateX = 9.0
            btnRemove.translateYProperty().bind(outPoint.translateYProperty().subtract(9))

            children.add(btnRemove)

            views += btnRemove
        }

        prefHeightProperty().bind(outPoint.translateYProperty().add(35.0))
    }

    private fun removeLastOption() {
        contentRoot.children.removeAt(contentRoot.children.size - 1)

        val point = outPoints.removeAt(outPoints.size - 1)
        children -= point

        val lastID = textNode.lastOptionID

        textNode.removeOption(lastID)

        prefHeightProperty().bind(outPoints.last().translateYProperty().add(35.0))
    }
}