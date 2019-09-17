/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.dsl.components

import com.almasb.fxgl.dsl.FXGL
import com.almasb.fxgl.entity.component.Component
import com.almasb.fxgl.time.LocalTimer
import javafx.util.Duration

/**
 * Moves the entity up/down and/or left/right.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
class LiftComponent : Component() {

    /**
     * Move in X axis.
     */
    var isHorizontal = false

    /**
     * Move in Y axis.
     */
    var isVertical = false

    var isGoingUp = false
    var isGoingRight = true

    var speedX = 0.0
    var speedY = 0.0

    var distanceX = 0.0
    var distanceY = 0.0

    var durationX: Duration = Duration.ZERO
    var durationY: Duration = Duration.ZERO

    private lateinit var timerX: LocalTimer
    private lateinit var timerY: LocalTimer

    override fun onAdded() {
        timerX = FXGL.newLocalTimer()
        timerX.capture()

        timerY = FXGL.newLocalTimer()
        timerY.capture()
    }

    override fun onUpdate(tpf: Double) {
        if (isHorizontal) {
            if (timerX.elapsed(durationX)) {
                isGoingRight = !isGoingRight
                timerX.capture()
            }

            entity.translateX(if (isGoingRight) speedX * tpf else -speedX * tpf)
        }

        if (isVertical) {
            if (timerY.elapsed(durationY)) {
                isGoingUp = !isGoingUp
                timerY.capture()
            }

            entity.translateY(if (isGoingUp) -speedY * tpf else speedY * tpf)
        }
    }

    fun xAxisDistanceDuration(distance: Double, duration: Duration) = this.apply {
        distanceX = distance
        durationX = duration
        speedX = distance / duration.toSeconds()

        isHorizontal = true
    }

    fun xAxisSpeedDuration(speed: Double, duration: Duration) = this.apply {
        speedX = speed
        durationX = duration
        distanceX = speed * duration.toSeconds()

        isHorizontal = true
    }

    fun xAxisSpeedDistance(speed: Double, distance: Double) = this.apply {
        speedX = speed
        distanceX = distance
        durationX = Duration.seconds(distance / speed)

        isHorizontal = true
    }

    fun yAxisDistanceDuration(distance: Double, duration: Duration) = this.apply {
        distanceY = distance
        durationY = duration
        speedY = distance / duration.toSeconds()

        isVertical = true
    }

    fun yAxisSpeedDuration(speed: Double, duration: Duration) = this.apply {
        speedY = speed
        durationY = duration
        distanceY = speed * duration.toSeconds()

        isVertical = true
    }

    fun yAxisSpeedDistance(speed: Double, distance: Double) = this.apply {
        speedY = speed
        distanceY = distance
        durationY = Duration.seconds(distance / speed)

        isVertical = true
    }
}