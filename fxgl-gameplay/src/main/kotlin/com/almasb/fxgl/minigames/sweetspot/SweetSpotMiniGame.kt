/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.minigames.sweetspot

import com.almasb.fxgl.core.math.FXGLMath
import com.almasb.fxgl.minigames.MiniGame
import javafx.beans.property.SimpleIntegerProperty

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class SweetSpotMiniGame : MiniGame<SweetSpotResult>() {

    private var isIncreasing = true

    val cursorValue = SimpleIntegerProperty(0)
    private var cursorAccumulator = 0.0

    val minSuccessValue = SimpleIntegerProperty(0)
    val maxSuccessValue = SimpleIntegerProperty(0)

    fun click() {
        if (cursorValue.value in minSuccessValue.value..maxSuccessValue.value) {
            result = SweetSpotResult(true)
        } else {
            result = SweetSpotResult(false)
        }

        isDone = true
    }

    fun randomizeRange(successRange: Int) {
        require(successRange in 0..100) {
            "Success range must be in 0..100"
        }

        minSuccessValue.value = FXGLMath.random(0, 100 - successRange)
        maxSuccessValue.value = minSuccessValue.value + successRange
    }

    override fun onUpdate(tpf: Double) {
        val ratePerSecond = 140
        val speed = tpf * ratePerSecond

        if (isIncreasing) {
            cursorAccumulator += speed
        } else {
            cursorAccumulator -= speed
        }

        if (cursorAccumulator > 100) {
            cursorAccumulator = 100.0
            isIncreasing = false
        }

        if (cursorAccumulator < 0) {
            cursorAccumulator = 0.0
            isIncreasing = true
        }

        cursorValue.value = cursorAccumulator.toInt()
    }
}