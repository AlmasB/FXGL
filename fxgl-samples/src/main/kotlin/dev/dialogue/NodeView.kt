/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package dev.dialogue

import com.almasb.fxgl.dsl.FXGL
import dev.dialogue.DialogueNodeType.*
import javafx.collections.FXCollections
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.effect.DropShadow
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
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
                START to Color.LIGHTGREEN.brighter(),
                END to Color.LIGHTGREEN.darker(),
                FUNCTION to Color.BLUE,
                CHOICE to Color.GOLD,
                TEXT to Color.DARKGREEN,
                BRANCH to Color.MEDIUMPURPLE
        )
    }

    // TODO: only one in point?
    var outPoints = FXCollections.observableArrayList<OutLinkPoint>()

    var inPoints = FXCollections.observableArrayList<InLinkPoint>()

    protected val contentRoot = VBox(10.0)

    protected val textArea = ExpandableTextArea(INITIAL_WIDTH - 70, INITIAL_HEIGHT - 50)

    init {
        styleClass.add("dialogue-editor-node-view")

        prefWidth = INITIAL_WIDTH
        prefHeight = INITIAL_HEIGHT

        textArea.font = Font.font(14.0)
        textArea.textProperty().bindBidirectional(node.textProperty)

        prefHeightProperty().bind(textArea.prefHeightProperty().add(50.0))

        addContent(textArea)

        val title = Title(node.type.toString().toLowerCase().capitalize(), colors[node.type] ?: Color.WHITE)
        title.prefWidthProperty().bind(prefWidthProperty().subtract(4))
        title.translateX = 2.0
        title.translateY = 2.0

        contentRoot.translateX = 35.0
        contentRoot.translateY = 35.0

        effect = DropShadow(10.0, Color.BLACK)

        children.addAll(contentRoot, title)
    }

    fun addContent(node: Node) {
        contentRoot.children.add(node)
    }

    fun addInPoint(linkPoint: InLinkPoint) {
        inPoints.add(linkPoint)

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

            style += "-fx-background-color: rgba(${c.red*255}, ${c.green*255}, ${c.blue*255}, 0.85)"

            val text = FXGL.getUIFactory().newText(name, Color.WHITE, 16.0)

            alignment = Pos.CENTER_LEFT
            padding = Insets(1.0, 15.0, 1.0, 33.0)

            children += text
        }
    }
}