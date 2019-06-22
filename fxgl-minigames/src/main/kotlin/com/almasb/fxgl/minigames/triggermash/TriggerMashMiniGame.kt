/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.minigames.triggermash

import com.almasb.fxgl.animation.Animation
import com.almasb.fxgl.animation.AnimationDSL
import com.almasb.fxgl.minigames.MiniGame
import com.almasb.fxgl.minigames.MiniGameResult
import com.almasb.fxgl.minigames.MiniGameView
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.geometry.Point2D
import javafx.scene.input.KeyCode
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.shape.Arc
import javafx.scene.shape.ArcType
import javafx.scene.shape.Circle
import javafx.scene.text.Font
import javafx.scene.text.Text
import javafx.util.Duration
import kotlin.math.max
import kotlin.math.min

class TriggerMashView(miniGame: TriggerMashMiniGame = TriggerMashMiniGame()) : MiniGameView<TriggerMashMiniGame>(miniGame) {

    private val animation: Animation<*>

    init {
        val circle = Circle(50.0, null)
        circle.stroke = Color.GREEN
        circle.strokeWidth = 2.5

        val innerCircle = Circle(1.0, Color.color(0.0, 0.67, 0.2, 0.65))

        innerCircle.radiusProperty().bind(miniGame.fillValue.divide(100.0).multiply(circle.radiusProperty()))

        val letter = Text("F")
        letter.font = Font.font(36.0)
        letter.stroke = Color.BLACK
        letter.fill = Color.YELLOW
        letter.strokeWidth = 1.5

        val letterCircle = Circle(35.0, null)
        letterCircle.stroke = Color.color(0.76, 0.9, 0.0, 0.76)

        animation = AnimationDSL().duration(Duration.seconds(0.09))
                .autoReverse(true)
                .repeat(2)
                .scale(letter, letterCircle)
                .from(Point2D(1.0, 1.0))
                .to(Point2D(1.4, 1.4))
                .build()

//        val arc = Arc()
//        arc.setCenterX(50.0)
//        arc.setCenterY(50.0)
//        arc.setRadiusX(25.0)
//        arc.setRadiusY(25.0)
//        arc.setStartAngle(45.0)
//        arc.setLength(270.0)
//        arc.setType(ArcType.ROUND)
//
//        arc.fill = null
//        arc.stroke = Color.GREEN



        children.addAll(StackPane(innerCircle, circle, letter, letterCircle))
    }

    override fun onUpdate(tpf: Double) {
        animation.onUpdate(tpf)
    }

    override fun onKeyPress(key: KeyCode) {
        animation.start()
        miniGame.boost()
    }
}

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class TriggerMashMiniGame : MiniGame<TriggerMashResult>() {

    var decayRate = 0.1
    var boostRate = 1.7

    val fillValue = SimpleDoubleProperty(0.0)

    override fun onUpdate(tpf: Double) {
        fillValue.value = max(0.0, fillValue.value - decayRate)
    }

    fun boost() {
        fillValue.value = min(100.0, fillValue.value + boostRate)

        if (fillValue.value == 100.0) {
            isDone = true
            result = TriggerMashResult(true)
        }
    }
}

class TriggerMashResult(override val isSuccess: Boolean) : MiniGameResult
