/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.minigames.sweetspot

import com.almasb.fxgl.minigames.MiniGame
import javafx.beans.property.SimpleIntegerProperty

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class SweetSpotMiniGame : MiniGame<SweetSpotResult> {

    private var isIncreasing = true

    val cursorValue = SimpleIntegerProperty(0)
    private var cursorAccumulator = 0.0

    val minSuccessValue = SimpleIntegerProperty(0)
    val maxSuccessValue = SimpleIntegerProperty(0)

    override val result: SweetSpotResult
        get() = TODO("not implemented")

    override val isDone: Boolean
        get() = false

    fun click() {
        if (cursorValue.value in minSuccessValue.value..maxSuccessValue.value) {
            println("success")
        } else {
            println("fail")
        }
    }

    override fun onUpdate(tpf: Double) {
        val ratePerSecond = 60
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