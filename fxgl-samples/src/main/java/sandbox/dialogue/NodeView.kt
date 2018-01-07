/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.dialogue

import com.almasb.fxgl.app.FXGL
import javafx.geometry.Insets
import javafx.geometry.Point2D
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.shape.CubicCurve
import javafx.scene.shape.Line
import javafx.scene.shape.QuadCurve
import jfxtras.scene.control.window.Window

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
abstract class NodeView(var w: Double = 150.0,
                        var h: Double = 100.0) : Pane() {

    // TODO:
    val dialogueNode: DialogueNode? = null

    var outPoints = arrayListOf<LinkPoint>()

    var inPoints = arrayListOf<LinkPoint>()

    private val mouseGestures = MouseGestures()

    val contentRoot = VBox(10.0)

    init {

        //isResizableWindow = false

        setPrefSize(w, h)
        //setMaxSize(150.0, 100.0)

        background = Background(BackgroundFill(Color.color(0.25, 0.25, 0.25, 0.75), null, null))

        //hbox.alignment = Pos.CENTER

        //children.add(hbox)

        this.styleClass.add("dialog-border")

        mouseGestures.makeDraggable(this)

        children.add(FXGL.getUIFactory().newText(javaClass.simpleName, Color.WHITE, 24.0))

        contentRoot.translateX = 35.0
        contentRoot.translateY = 10.0

        children.add(contentRoot)
    }

    fun addContent(node: Node) {
        contentRoot.children.add(node)
    }

    fun addInPoint(linkPoint: LinkPoint) {
        inPoints.add(linkPoint)

        children.add(linkPoint)

        linkPoint.translateX = 10.0
        linkPoint.translateYProperty().bind(heightProperty().divide(2))
    }

    fun addOutPoint(linkPoint: LinkPoint) {
        outPoints.add(linkPoint)

        children.add(linkPoint)

        linkPoint.translateXProperty().bind(widthProperty().add(-25))
        linkPoint.translateYProperty().bind(heightProperty().divide(2))
    }

    fun connect(fromNodeView: NodeView, outLinkPoint: OutLinkPoint, inLinkPoint: InLinkPoint): Node {

        val curve = CubicCurve()
        with(curve) {
            startXProperty().bind(fromNodeView.layoutXProperty().add(outLinkPoint.translateXProperty().add(10)))
            startYProperty().bind(fromNodeView.layoutYProperty().add(outLinkPoint.translateYProperty().add(10)))


            controlX1Property().bind(startXProperty().add(endXProperty()).divide(2))
            controlY1Property().bind(startYProperty())

            controlX2Property().bind(startXProperty().add(endXProperty()).divide(2))
            controlY2Property().bind(endYProperty())

            strokeWidth = 2.0
            stroke = Color.color(0.9, 0.9, 0.9, 0.9)
            fill = null
        }

        curve.endXProperty().bind(layoutXProperty().add(inLinkPoint.translateXProperty()).add(10))
        curve.endYProperty().bind(layoutYProperty().add(inLinkPoint.translateYProperty()).add(10))

        return curve
    }
}