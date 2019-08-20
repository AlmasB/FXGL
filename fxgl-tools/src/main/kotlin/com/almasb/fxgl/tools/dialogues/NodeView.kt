/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.tools.dialogues

import com.almasb.fxgl.cutscene.dialogue.DialogueNode
import com.almasb.fxgl.dsl.FXGL
import com.almasb.fxgl.cutscene.dialogue.DialogueNodeType.*
import com.almasb.fxgl.tools.dialogues.ui.ExpandableTextArea
import javafx.beans.binding.Bindings
import javafx.collections.FXCollections
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.effect.DropShadow
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.text.Font

/**
 * A generic view of a dialogue node.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
abstract class NodeView(val node: DialogueNode) : Pane() {

    companion object {
        private const val INITIAL_WIDTH = 320.0
        private const val INITIAL_HEIGHT = 100.0

        val colors = mapOf(
                START to Color.LIGHTGREEN.darker(),
                END to Color.DARKCYAN,
                FUNCTION to Color.BLUE,
                CHOICE to Color.GOLD,
                TEXT to Color.DARKGREEN,
                BRANCH to Color.MEDIUMPURPLE
        )
    }

    var outPoints = FXCollections.observableArrayList<OutLinkPoint>()

    /**
     * Only start node does not have an in point.
     */
    var inPoint: InLinkPoint? = null

    protected val contentRoot = VBox(10.0)

    protected val textArea = ExpandableTextArea(INITIAL_WIDTH - 70, INITIAL_HEIGHT - 50)

    val closeButton = CustomButton("\u2715", 16.0)

    init {
        styleClass.add("dialogue-editor-node-view")

        prefWidth = INITIAL_WIDTH
        prefHeight = INITIAL_HEIGHT

        textArea.font = Font.font(14.0)
        textArea.textProperty().bindBidirectional(node.textProperty)

        prefHeightProperty().bind(textArea.prefHeightProperty().add(50.0))

        addContent(textArea)

        val title = Title(node.type.toString().toLowerCase().capitalize(), colors[node.type]
                ?: Color.WHITE)
        title.prefWidthProperty().bind(prefWidthProperty().subtract(4))
        title.translateX = 2.0
        title.translateY = 2.0

        contentRoot.translateX = 35.0
        contentRoot.translateY = 35.0

        closeButton.translateX = prefWidth - 28.0
        closeButton.translateY = 6.0

        effect = DropShadow(10.0, Color.BLACK)

        children.addAll(contentRoot, title, closeButton)
    }

    fun addContent(node: Node) {
        contentRoot.children.add(node)
    }

    fun addInPoint(linkPoint: InLinkPoint) {
        inPoint = linkPoint

        children.add(linkPoint)

        linkPoint.translateX = 10.0
        linkPoint.translateYProperty().bind(heightProperty().divide(2))
    }

    fun addOutPoint(linkPoint: OutLinkPoint) {
        outPoints.add(linkPoint)

        children.add(linkPoint)

        linkPoint.translateXProperty().bind(widthProperty().add(-25))
        linkPoint.translateYProperty().bind(heightProperty().divide(2))
    }

    private class Title(name: String, c: Color) : HBox() {

        init {
            styleClass += "title"

            //style += "-fx-background-color: linear-gradient(from 0% 50% to 100% 50%, rgba(${c.red*255}, ${c.green*255}, ${c.blue*255}, 0.85), transparent);"

            style = "-fx-background-color: rgba(${c.red*255}, ${c.green*255}, ${c.blue*255}, 0.85)"

            DialoguePane.highContrastProperty.addListener { _, _, isContrast ->
                if (isContrast) {
                    val chigh = c.deriveColor(180.0, 1.0, 1.0, 1.0)
                    style = "-fx-background-color: rgba(${chigh.red*255}, ${chigh.green*255}, ${chigh.blue*255}, 0.95)"
                } else {
                    style = "-fx-background-color: rgba(${c.red*255}, ${c.green*255}, ${c.blue*255}, 0.85)"
                }
            }

            val text = FXGL.getUIFactory().newText(name, Color.WHITE, 16.0)

            alignment = Pos.CENTER_LEFT
            padding = Insets(1.0, 15.0, 1.0, 33.0)

            children += text
        }
    }
}

class CustomButton(symbol: String, size: Double = 24.0) : StackPane() {

    init {
        val bg = Rectangle(20.0, 20.0, null)
        val text = FXGL.getUIFactory().newText(symbol, size)

        bg.strokeProperty().bind(
                Bindings.`when`(hoverProperty()).then(Color.WHITE).otherwise(Color.TRANSPARENT)
        )

        children.addAll(bg, text)
    }
}