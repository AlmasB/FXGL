/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.minigames.triggermash

import com.almasb.fxgl.animation.Animation
import com.almasb.fxgl.animation.AnimationDSL
import com.almasb.fxgl.input.Input
import com.almasb.fxgl.input.KeyTrigger
import com.almasb.fxgl.input.UserAction
import com.almasb.fxgl.minigames.MiniGame
import com.almasb.fxgl.minigames.MiniGameResult
import com.almasb.fxgl.minigames.MiniGameView
import javafx.beans.property.SimpleDoubleProperty
import javafx.geometry.Point2D
import javafx.geometry.VPos
import javafx.scene.effect.DropShadow
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.paint.CycleMethod
import javafx.scene.paint.LinearGradient
import javafx.scene.paint.Stop
import javafx.scene.shape.Arc
import javafx.scene.shape.ArcType
import javafx.scene.shape.Circle
import javafx.scene.text.Font
import javafx.scene.text.Text
import javafx.scene.transform.Scale
import javafx.util.Duration
import kotlin.math.max
import kotlin.math.min

class TriggerMashView(trigger: KeyTrigger, miniGame: TriggerMashMiniGame = TriggerMashMiniGame(trigger)) : MiniGameView<TriggerMashMiniGame>(miniGame) {

    private val animation: Animation<*>

    init {
        val circle = Circle(50.0, null)
        circle.stroke = Color.GREEN
        circle.strokeWidth = 2.5

        val innerCircle = Circle(1.0, Color.color(0.0, 0.67, 0.2, 0.65))

        innerCircle.radiusProperty().bind(miniGame.fillValue.divide(100.0).multiply(circle.radiusProperty()))

        val letter = Text(miniGame.trigger.toString())
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

        children.addAll(StackPane(innerCircle, circle, letter, letterCircle))
    }

    override fun onUpdate(tpf: Double) {
        animation.onUpdate(tpf)
    }

    override fun onInitInput(input: Input) {
        input.addAction(object : UserAction("Button Mash") {
            override fun onActionBegin() {
                animation.start()
                miniGame.boost()
            }

        }, miniGame.trigger.key)
    }
}

class CircleTriggerMashView(trigger: KeyTrigger, miniGame: TriggerMashMiniGame = TriggerMashMiniGame(trigger)) : MiniGameView<TriggerMashMiniGame>(miniGame) {

    private val animation: Animation<*>

    init {
        val circle = Circle(100.0, 100.0, 40.0, null)
        circle.stroke = Color.DARKGREEN
        circle.strokeWidth = 5.0

        val arc = Arc()
        arc.centerX = 100.0
        arc.centerY = 100.0
        arc.radiusX = circle.radius
        arc.radiusY = circle.radius
        arc.startAngle = 90.0
        arc.type = ArcType.OPEN
        arc.fill = null
        arc.stroke = Color.YELLOW
        arc.strokeWidth = 3.0
        arc.transforms.add(Scale(-1.0, 1.0, arc.centerX, arc.centerY))

        arc.lengthProperty().bind(miniGame.fillValue.divide(100.0).multiply(360))

        val letter = Text(miniGame.trigger.toString())
        letter.font = Font.font(36.0)
        letter.stroke = Color.BLACK
        letter.fill = Color.YELLOW
        letter.strokeWidth = 1.5
        letter.textOrigin = VPos.TOP

        letter.translateX = 100.0 - letter.layoutBounds.width / 2
        letter.translateY = 100.0 - letter.layoutBounds.height / 2 - 1.5

        val letterCircle = Circle(100.0, 100.0, 20.0, null)
        letterCircle.stroke = Color.color(0.76, 0.9, 0.0, 0.76)

        animation = AnimationDSL().duration(Duration.seconds(0.09))
                .autoReverse(true)
                .repeat(2)
                .scale(letter, letterCircle)
                .from(Point2D(1.0, 1.0))
                .to(Point2D(1.4, 1.4))
                .build()

        val bg = Circle(100.0, 100.0, 100.0, LinearGradient(
                0.0, 0.0, 1.0, 1.0, true, CycleMethod.NO_CYCLE,
                Stop(0.1, Color.color(0.1, 0.4, 0.9, 0.8)),
                Stop(0.9, Color.color(0.1, 0.9, 0.1, 0.4))
        ))

        effect = DropShadow(20.0, Color.BLACK)

        children.addAll(bg, circle, arc, letter, letterCircle)
    }

    override fun onUpdate(tpf: Double) {
        animation.onUpdate(tpf)
    }

    override fun onInitInput(input: Input) {
        input.addAction(object : UserAction("Button Mash") {
            override fun onActionBegin() {
                animation.start()
                miniGame.boost()
            }

        }, miniGame.trigger.key)
    }
}

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class TriggerMashMiniGame(var trigger: KeyTrigger) : MiniGame<TriggerMashResult>() {

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
