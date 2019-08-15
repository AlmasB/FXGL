/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package dev.dialogue

import com.almasb.fxgl.dsl.FXGL
import javafx.scene.Node
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.Font
import sandbox.cutscene.MouseGestures

/**
 * A generic view of a dialogue node.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
abstract class NodeView(val node: DialogueNode) : Pane() {

    private val initialWidth = 320.0
    private val initialHeight = 100.0

    var outPoints = arrayListOf<OutLinkPoint>()

    var inPoints = arrayListOf<InLinkPoint>()

    private val contentRoot = VBox(10.0)

    protected val textArea = ExpandableTextArea(initialWidth - 70, initialHeight - 50)

    init {
        styleClass.add("dialogue-editor-node-view")

        prefWidth = initialWidth
        prefHeight = initialHeight

        textArea.font = Font.font(14.0)
        textArea.textProperty().bindBidirectional(node.textProperty)

        prefHeightProperty().bind(textArea.prefHeightProperty().add(50.0))

        val title = FXGL.getUIFactory().newText(node.type.toString().toLowerCase().capitalize(),
                Color.WHITE, 16.0)

        addContent(title)
        addContent(textArea)

        contentRoot.translateX = 35.0
        contentRoot.translateY = 10.0

        children.addAll(contentRoot)
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
}