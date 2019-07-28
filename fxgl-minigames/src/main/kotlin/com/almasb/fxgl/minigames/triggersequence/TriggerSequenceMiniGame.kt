/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.minigames.triggersequence

import com.almasb.fxgl.animation.Animation
import com.almasb.fxgl.animation.AnimationDSL
import com.almasb.fxgl.animation.Interpolators
import com.almasb.fxgl.input.KeyTrigger
import com.almasb.fxgl.input.view.KeyView
import com.almasb.fxgl.input.view.TriggerView
import com.almasb.fxgl.minigames.MiniGame
import com.almasb.fxgl.minigames.MiniGameResult
import com.almasb.fxgl.minigames.MiniGameView
import javafx.geometry.Point2D
import javafx.scene.Group
import javafx.scene.input.KeyCode
import javafx.scene.input.MouseButton
import javafx.scene.paint.Color
import javafx.scene.shape.*
import javafx.util.Duration

class TriggerSequenceView(miniGame: TriggerSequenceMiniGame = TriggerSequenceMiniGame()) : MiniGameView<TriggerSequenceMiniGame>(miniGame) {

    private val animationGood: Animation<*>
    private val animationBad: Animation<*>

    private val circle = Circle(40.0, 40.0, 40.0, Color.GREEN)

    //private val circle = KeyView(KeyCode.A, Color.BLACK, 74.0)

    private val triggerViews = Group()

    init {
        val line1 = Line(0.0, 0.0, 0.0, 300.0)
        val line2 = Line(80.0, 0.0, 80.0, 300.0)
        line1.strokeWidth = 2.0
        line2.strokeWidth = 2.0

        val bg = Rectangle(80.0, 300.0, Color.color(0.2, 0.2, 0.8, 0.7))


        circle.opacity = 0.0


        animationGood = AnimationDSL().duration(Duration.seconds(0.49))
                .interpolator(Interpolators.ELASTIC.EASE_OUT())
                .onFinished(Runnable { circle.opacity = 0.0 })
                .translate(circle)
                .from(Point2D(0.0, 40.0))
                .to(Point2D(0.0, -90.0))
                .build()

        animationBad = AnimationDSL().duration(Duration.seconds(0.49))
                .onFinished(Runnable { circle.opacity = 0.0 })
                .interpolator(Interpolators.ELASTIC.EASE_OUT())
                .translate(circle)
                .from(Point2D(0.0, 40.0))
                .to(Point2D(0.0, 170.0))
                .build()

        children.addAll(line1, line2, circle, triggerViews)
    }

    private var firstTime = true

    override fun onUpdate(tpf: Double) {
        if (firstTime) {
            firstTime = false
            triggerViews.children.addAll(miniGame.views)
        }

        animationGood.onUpdate(tpf)
        animationBad.onUpdate(tpf)
    }

    override fun onKeyPress(key: KeyCode) {
        circle.opacity = 1.0

        if (triggerViews.children.isNotEmpty())
            triggerViews.children.removeAt(0)

        if (miniGame.press(key)) {
            animationGood.start()
            circle.fill = Color.GREEN
        } else {
            animationBad.start()
            circle.fill = Color.RED
        }
    }
}

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class TriggerSequenceMiniGame : MiniGame<TriggerSequenceResult>() {

    var numTriggers = 4
    var moveSpeed = 200

    private var currentIndex = 0

    private val triggers = arrayListOf(
            KeyTrigger(KeyCode.F),
            KeyTrigger(KeyCode.F),
            KeyTrigger(KeyCode.G),
            KeyTrigger(KeyCode.F)
    )

    val views = arrayListOf<TriggerView>()

    init {
        views += triggers.map { TriggerView(it, Color.GRAY, 74.0) }
        views.forEachIndexed { i, item ->
            item.translateX = 400.0 + 300*i
            item.translateY = 100.0

            item.opacityProperty().bind(item.translateXProperty().divide(numTriggers * 100.0).negate().add(1))
        }
    }

    override fun onUpdate(tpf: Double) {
        views.forEach { it.translateX -= moveSpeed * tpf }
    }

    fun press(key: KeyCode): Boolean {
        if (currentIndex == triggers.size) {
            isDone = true
            result = TriggerSequenceResult(true)
            return false
        }

        return triggers[currentIndex++].key == key
    }

    fun press(btn: MouseButton) {

    }
}

class TriggerSequenceResult(override val isSuccess: Boolean) : MiniGameResult
