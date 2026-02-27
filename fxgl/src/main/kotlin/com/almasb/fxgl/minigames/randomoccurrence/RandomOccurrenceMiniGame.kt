/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.minigames.randomoccurrence

import com.almasb.fxgl.core.math.FXGLMath
import com.almasb.fxgl.minigames.MiniGame
import com.almasb.fxgl.minigames.MiniGameResult
import com.almasb.fxgl.minigames.MiniGameView
import javafx.beans.property.SimpleDoubleProperty
import javafx.scene.effect.BoxBlur
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.text.Font
import javafx.scene.text.Text

class RandomOccurrenceView(miniGame: RandomOccurrenceMiniGame = RandomOccurrenceMiniGame()) : MiniGameView<RandomOccurrenceMiniGame>(miniGame) {

    init {
        val bg = Rectangle(400.0, 120.0)
        bg.arcWidth = 10.0
        bg.arcHeight = 10.0
        bg.stroke = Color.LIGHTGOLDENRODYELLOW
        bg.fill = Color.color(0.0, 0.0, 0.0, 0.75)

        val textSuccess = Text("Success")
        textSuccess.font = Font.font(26.0)
        textSuccess.fill = Color.LIGHTGOLDENRODYELLOW
        textSuccess.translateX = 25.0
        textSuccess.translateY = 50.0

        val textFailure = Text("Failure")
        textFailure.font = Font.font(26.0)
        textFailure.fill = Color.LIGHTGOLDENRODYELLOW
        textFailure.translateX = 25.0
        textFailure.translateY = 90.0

        val successOuterBar = Rectangle(220.0, 15.0)
        val failureOuterBar = Rectangle(220.0, 15.0)

        successOuterBar.translateX = textSuccess.translateX + textSuccess.layoutBounds.width + 15.0
        successOuterBar.translateY = textSuccess.translateY - 15.0

        failureOuterBar.translateX = successOuterBar.translateX
        failureOuterBar.translateY = textFailure.translateY - 15.0

        val successInnerBar = Rectangle(0.0, 15.0, Color.BLUE)
        val failureInnerBar = Rectangle(0.0, 15.0, Color.RED)

        successInnerBar.effect = BoxBlur(5.0, 5.0, 1)
        failureInnerBar.effect = BoxBlur(5.0, 5.0, 1)

        successInnerBar.translateX = successOuterBar.translateX
        successInnerBar.translateY = successOuterBar.translateY

        failureInnerBar.translateX = failureOuterBar.translateX
        failureInnerBar.translateY = failureOuterBar.translateY

        successInnerBar.widthProperty().bind(miniGame.successFill.multiply(220.0))
        failureInnerBar.widthProperty().bind(miniGame.failureFill.multiply(220.0))

        children.addAll(bg, textSuccess, textFailure, successOuterBar, failureOuterBar, successInnerBar, failureInnerBar)
    }
}

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class RandomOccurrenceMiniGame() : MiniGame<RandomOccurrenceResult>() {

    var successChance = 0.5

    var maxSuccessFill = 0.01
    var maxFailureFill = 0.01

    val successFill = SimpleDoubleProperty(0.0)
    val failureFill = SimpleDoubleProperty(0.0)

    override fun onUpdate(tpf: Double) {
        if (FXGLMath.randomBoolean(successChance)) {
            successFill.value += FXGLMath.random(0.001, maxSuccessFill)
        } else {
            failureFill.value += FXGLMath.random(0.001, maxFailureFill)
        }

        if (successFill.value >= 1) {
            isDone = true
            result = RandomOccurrenceResult(true)
        }

        if (failureFill.value >= 1) {
            isDone = true
            result = RandomOccurrenceResult(false)
        }
    }
}

class RandomOccurrenceResult(override val isSuccess: Boolean) : MiniGameResult
