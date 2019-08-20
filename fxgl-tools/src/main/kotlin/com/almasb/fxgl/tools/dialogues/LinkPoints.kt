/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.tools.dialogues

import com.almasb.fxgl.cutscene.dialogue.DialogueChoiceEdge
import com.almasb.fxgl.cutscene.dialogue.DialogueEdge
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
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

    override fun toString(): String {
        return "InLink(${owner.javaClass.simpleName}, links=$connectedPoints)"
    }
}

class OutLinkPoint(owner: NodeView) : LinkPoint(owner) {

    var other: InLinkPoint? = null

    var choiceOptionID: Int = -1
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

    fun connect(inPoint: InLinkPoint) {
        other = inPoint
        inPoint.connectedPoints += this
        isConnected = true
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

    override fun toString(): String {
        return "OutLink(${owner.javaClass.simpleName}, connected=${other?.owner?.javaClass?.simpleName}, optionID=$choiceOptionID)"
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

class EdgeView(val edge: DialogueEdge, val source: OutLinkPoint, val target: InLinkPoint) : CubicCurve() {
    var optionID = if (edge is DialogueChoiceEdge) edge.optionID else -1

    init {
        val outPoint = source
        val inPoint = target

        val fromView = source.owner
        val toView = target.owner

        startXProperty().bind(fromView.layoutXProperty().add(outPoint.translateXProperty().add(10)))
        startYProperty().bind(fromView.layoutYProperty().add(outPoint.translateYProperty().add(10)))

        endXProperty().bind(toView.layoutXProperty().add(inPoint.translateXProperty()).add(10))
        endYProperty().bind(toView.layoutYProperty().add(inPoint.translateYProperty()).add(10))

        val right = endXProperty().greaterThanOrEqualTo(startXProperty())

        val offset = Bindings.max(startXProperty().subtract(endXProperty()), 160.0)

        controlX1Property().bind(Bindings.`when`(right)
                .then(
                        startXProperty().add(endXProperty()).divide(2)
                ).otherwise(
                        startXProperty().add(offset)
                )
        )
        controlY1Property().bind(Bindings.`when`(right)
                .then(
                        startYProperty()
                ).otherwise(
                        startYProperty().add(endYProperty()).divide(2)
                )
        )

        controlX2Property().bind(Bindings.`when`(right)
                .then(
                        startXProperty().add(endXProperty()).divide(2)
                ).otherwise(
                        endXProperty().subtract(offset)
                )
        )
        controlY2Property().bind(Bindings.`when`(right)
                .then(
                        endYProperty()
                ).otherwise(
                        startYProperty().add(endYProperty()).divide(2)
                )
        )

        strokeWidth = 2.5
        stroke = NodeView.colors[inPoint.owner.node.type] ?: Color.color(0.9, 0.9, 0.9, 0.9)
        fill = null
        effect = Glow(0.7)
    }
}