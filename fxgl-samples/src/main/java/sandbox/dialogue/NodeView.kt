/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.dialogue

import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.shape.Line
import jfxtras.scene.control.window.Window

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
abstract class NodeView : Window() {

    // TODO:
    val dialogueNode: DialogueNode? = null

    var outPoints = arrayListOf<OutLinkPoint>()

    var inPoints = arrayListOf<InLinkPoint>()

    init {

        isResizableWindow = false
    }

    fun connect(fromNodeView: NodeView, outLinkPoint: OutLinkPoint): Line {

        val line = Line()
        line.startXProperty().bind(fromNodeView.layoutXProperty().add(outLinkPoint.translateXProperty().add(10)))
        line.startYProperty().bind(fromNodeView.layoutYProperty().add(outLinkPoint.translateYProperty().add(10)))

        line.endXProperty().bind(this.layoutXProperty())
        line.endYProperty().bind(this.layoutYProperty())

        line.stroke = Color.WHITE

        return line
    }
}