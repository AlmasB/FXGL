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
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.shape.CubicCurve

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
sealed class LinkPoint(val owner: NodeView) : Circle(8.0, 8.0, 8.0) {

    protected val connectedProperty = SimpleBooleanProperty(false)

    var isConnected: Boolean
        get() = connectedProperty.value
        protected set(value) { connectedProperty.value = value }

    init {
        fillProperty().bind(
                Bindings.`when`(connectedProperty).then(Color.YELLOWGREEN).otherwise(Color.TRANSPARENT)
        )
        strokeProperty().bind(
                Bindings.`when`(connectedProperty).then(Color.YELLOW.brighter()).otherwise(Color.YELLOW.darker())
        )
        strokeWidth = 2.0
    }
}

class InLinkPoint(owner: NodeView) : LinkPoint(owner) {

    val connectedPoints = FXCollections.observableArrayList<OutLinkPoint>()

    init {
        connectedProperty.bind(Bindings.isEmpty(connectedPoints).not())
    }
}

class OutLinkPoint(owner: NodeView) : LinkPoint(owner) {

    var other: InLinkPoint? = null

    var choiceLocalID: Int = -1
    var choiceLocalOptionProperty = SimpleStringProperty("")

    fun connect(inPoint: InLinkPoint): EdgeView? {
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

            strokeWidth = 2.0
            stroke = Color.color(0.9, 0.9, 0.9, 0.9)
            fill = null
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

class EdgeView(val source: OutLinkPoint, val target: InLinkPoint) : CubicCurve() {
    var localID = -1
}