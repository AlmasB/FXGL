/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.minigames.triggersequence

import com.almasb.fxgl.animation.Animation
import com.almasb.fxgl.animation.AnimationBuilder
import com.almasb.fxgl.animation.Interpolators
import com.almasb.fxgl.input.Input
import com.almasb.fxgl.input.KeyTrigger
import com.almasb.fxgl.input.Trigger
import com.almasb.fxgl.input.TriggerListener
import com.almasb.fxgl.input.view.TriggerView
import com.almasb.fxgl.minigames.MiniGame
import com.almasb.fxgl.minigames.MiniGameResult
import com.almasb.fxgl.minigames.MiniGameView
import javafx.geometry.Point2D
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCode
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.shape.Line
import javafx.util.Duration

class TriggerSequenceView(miniGame: TriggerSequenceMiniGame = TriggerSequenceMiniGame(winRatio = 1.0)) : MiniGameView<TriggerSequenceMiniGame>(miniGame) {

    private val animationGood: Animation<*>
    private val animationBad: Animation<*>

    private val circle = StackPane()
    private val bg = Circle(40.0, 40.0, 40.0, Color.GREEN)

    private val line1 = Line(0.0, 0.0, 0.0, 300.0)
    private val line2 = Line(150.0, 0.0, 150.0, 300.0)

    private val triggerViews = Group()
    private var numCorrectTriggers = 0
    private var currentTriggerIndex = 0

    private val good = ImageView(Image(javaClass.getResourceAsStream("checkmark.png")))
    private val bad = ImageView(Image(javaClass.getResourceAsStream("cross.png")))

    init {
        line1.strokeWidth = 2.0
        line2.strokeWidth = 2.0

        circle.opacity = 0.0
        circle.children.addAll(bg, good)

        animationGood = AnimationBuilder().duration(Duration.seconds(0.49))
                .interpolator(Interpolators.ELASTIC.EASE_OUT())
                .onFinished(Runnable { circle.opacity = 0.0 })
                .translate(circle)
                .from(Point2D(25.0, 40.0))
                .to(Point2D(25.0, -40.0))
                .build()

        animationBad = AnimationBuilder().duration(Duration.seconds(0.49))
                .onFinished(Runnable { circle.opacity = 0.0 })
                .interpolator(Interpolators.ELASTIC.EASE_OUT())
                .translate(circle)
                .from(Point2D(25.0, 40.0))
                .to(Point2D(25.0, 190.0))
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

        if (triggerViews.children.isNotEmpty()) {
            checkTriggerPosition(triggerViews.children[0])
        } else {
            miniGame.endGame(numCorrectTriggers)
        }
    }

    private fun checkTriggerPosition(currentTriggerView: Node) {
        if (currentTriggerView.translateX < line1.startX) {
            triggerViews.children.remove(currentTriggerView)
            currentTriggerIndex++
            startAnimationBad()
        }
    }

    private fun startAnimationGood() {
        circle.opacity = 1.0
        animationGood.start()
        bg.fill = Color.GREEN

        circle.children[1] = good
    }

    private fun startAnimationBad() {
        circle.opacity = 1.0
        animationBad.start()
        bg.fill = Color.RED

        circle.children[1] = bad
    }

    override fun onInitInput(input: Input) {
        input.addTriggerListener(object : TriggerListener() {
            override fun onActionBegin(trigger: Trigger) {
                if (trigger is KeyTrigger) {
                    val key = trigger.key

                    if (triggerViews.children.isEmpty())
                        return

                    val currentTrigger = triggerViews.children.removeAt(0)

                    // Has the correct key been pressed, and is the current view between the two lines
                    if (miniGame.isCorrect(key, currentTriggerIndex)
                            && currentTrigger.translateX >= line1.startX
                            && currentTrigger.translateX <= line2.startX) {

                        startAnimationGood()
                        numCorrectTriggers++
                    } else {
                        startAnimationBad()
                    }
                    // After checking, increment trigger index
                    currentTriggerIndex++
                }
            }
        })
    }
}

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class TriggerSequenceMiniGame(val winRatio: Double) : MiniGame<TriggerSequenceResult>() {

    var numTriggersForSuccess = 0
    val numTriggers: Int
        get() = triggers.size

    var moveSpeed = 350

    val triggers = arrayListOf<KeyTrigger>()

    val views = arrayListOf<TriggerView>()

    private var firstTime = true

    override fun onUpdate(tpf: Double) {
        if (firstTime) {
            numTriggersForSuccess = triggers.size

            views += triggers.map { TriggerView(it, Color.GRAY, 74.0) }
            views.forEachIndexed { i, item ->
                item.translateX = 400.0 + 300*i
                item.translateY = 100.0

                item.opacityProperty().bind(item.translateXProperty().divide(numTriggers * 100.0).negate().add(1))
            }
            firstTime = false
        }

        views.forEach { it.translateX -= moveSpeed * tpf }
    }

    fun endGame(numCorrectTriggers: Int) {
        // Trigger the end of the MiniGame
        isDone = true
        val ratio = numCorrectTriggers / numTriggers.toDouble()
        result = TriggerSequenceResult(ratio >= winRatio, ratio)
    }

    fun isCorrect(key: KeyCode, currentIndex: Int): Boolean {
        return triggers[currentIndex].key == key
    }
}

class TriggerSequenceResult(override val isSuccess: Boolean, val ratio: Double ) : MiniGameResult
