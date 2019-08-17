/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package dev.dialogue

import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.scene.Node
import javafx.scene.effect.Glow
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.shape.CubicCurve
import javafx.scene.shape.Polygon

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
sealed class LinkPoint(val owner: NodeView) : Pane() {

    protected val connectedProperty = SimpleBooleanProperty(false)

    var isConnected: Boolean
        get() = connectedProperty.value
        protected set(value) { connectedProperty.value = value }

    protected val bg = Circle(8.0, 8.0, 8.0)

    init {
        bg.fillProperty().bind(
                Bindings.`when`(connectedProperty).then(Color.YELLOWGREEN).otherwise(Color.TRANSPARENT)
        )
        bg.strokeProperty().bind(
                Bindings.`when`(connectedProperty).then(Color.YELLOW.brighter()).otherwise(Color.YELLOW.darker())
        )
        bg.strokeWidth = 2.0

        children += bg
    }
}

class InLinkPoint(owner: NodeView) : LinkPoint(owner) {

    val connectedPoints = FXCollections.observableArrayList<OutLinkPoint>()

    init {
        connectedProperty.bind(Bindings.isEmpty(connectedPoints).not())

        val arrow = Arrow()
        arrow.translateX = -5.5
        arrow.translateY = 8.0 - 2.5

        arrow.strokeProperty().bind(
                Bindings.`when`(connectedProperty).then(Color.YELLOW.brighter()).otherwise(Color.color(0.9, 0.9, 0.9, 0.7))
        )

        children += arrow
    }
}

class OutLinkPoint(owner: NodeView) : LinkPoint(owner) {

    var other: InLinkPoint? = null

    var choiceLocalID: Int = -1
    var choiceLocalOptionProperty = SimpleStringProperty("")

    init {
        val arrow = Arrow()
        arrow.translateX = 16.0 + 2.5
        arrow.translateY = 8.0 - 2.5

        arrow.strokeProperty().bind(
                Bindings.`when`(connectedProperty).then(Color.YELLOW.brighter()).otherwise(Color.color(0.9, 0.9, 0.9, 0.7))
        )

        children += arrow
    }

    fun connect(inPoint: InLinkPoint): EdgeView {
        other = inPoint
        inPoint.connectedPoints += this
        isConnected = true

        val fromView = this.owner
        val toView = inPoint.owner
        val outPoint = this

        val curve = EdgeView(outPoint, inPoint)
        with(curve) {
            startXProperty().bind(fromView.layoutXProperty().add(outPoint.translateXProperty().add(10)))
            startYProperty().bind(fromView.layoutYProperty().add(outPoint.translateYProperty().add(10)))

            controlX1Property().bind(startXProperty().add(endXProperty()).divide(2))
            controlY1Property().bind(startYProperty())

            controlX2Property().bind(startXProperty().add(endXProperty()).divide(2))
            controlY2Property().bind(endYProperty())

            strokeWidth = 2.5
            stroke = NodeView.colors[inPoint.owner.node.type] ?: Color.color(0.9, 0.9, 0.9, 0.9)
            fill = null
            effect = Glow(0.7)
        }

        curve.endXProperty().bind(toView.layoutXProperty().add(inPoint.translateXProperty()).add(10))
        curve.endYProperty().bind(toView.layoutYProperty().add(inPoint.translateYProperty()).add(10))

        return curve
    }

    fun disconnect(): InLinkPoint? {
        other?.let {
            it.connectedPoints -= this
        }

        val result = other

        isConnected = false
        other = null

        return result
    }
}

private class Arrow : Polygon(
        0.0, 0.0,
        3.0, 2.5,
        0.0, 5.0) {

    init {
        fill = Color.color(0.9, 0.9, 0.9, 0.7)
    }
}

class EdgeView(val source: OutLinkPoint, val target: InLinkPoint) : CubicCurve() {
    var localID = -1
}