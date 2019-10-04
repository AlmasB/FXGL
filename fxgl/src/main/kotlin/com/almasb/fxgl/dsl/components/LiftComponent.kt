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

    private class LiftData() {
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

        internal lateinit var timerX: LocalTimer
        internal lateinit var timerY: LocalTimer
    }

    private val liftData = LiftData()

    override fun onAdded() {
        liftData.timerX = FXGL.newLocalTimer()
        liftData.timerX.capture()

        liftData.timerY = FXGL.newLocalTimer()
        liftData.timerY.capture()
    }

    override fun onUpdate(tpf: Double) {
        if (liftData.isHorizontal) {
            if (liftData.timerX.elapsed(liftData.durationX)) {
                liftData.isGoingRight = !liftData.isGoingRight
                liftData.timerX.capture()
            }

            entity.translateX(if (liftData.isGoingRight) liftData.speedX * tpf else -liftData.speedX * tpf)
        }

        if (liftData.isVertical) {
            if (liftData.timerY.elapsed(liftData.durationY)) {
                liftData.isGoingUp = !liftData.isGoingUp
                liftData.timerY.capture()
            }

            entity.translateY(if (liftData.isGoingUp) -liftData.speedY * tpf else liftData.speedY * tpf)
        }
    }

    fun xAxisDistanceDuration(distance: Double, duration: Duration) = this.apply {
        liftData.distanceX = distance
        liftData.durationX = duration
        liftData.speedX = distance / duration.toSeconds()

        liftData.isHorizontal = true
    }

    fun xAxisSpeedDuration(speed: Double, duration: Duration) = this.apply {
        liftData.speedX = speed
        liftData.durationX = duration
        liftData.distanceX = speed * duration.toSeconds()

        liftData.isHorizontal = true
    }

    fun xAxisSpeedDistance(speed: Double, distance: Double) = this.apply {
        liftData.speedX = speed
        liftData.distanceX = distance
        liftData.durationX = Duration.seconds(distance / speed)

        liftData.isHorizontal = true
    }

    fun yAxisDistanceDuration(distance: Double, duration: Duration) = this.apply {
        liftData.distanceY = distance
        liftData.durationY = duration
        liftData.speedY = distance / duration.toSeconds()

        liftData.isVertical = true
    }

    fun yAxisSpeedDuration(speed: Double, duration: Duration) = this.apply {
        liftData.speedY = speed
        liftData.durationY = duration
        liftData.distanceY = speed * duration.toSeconds()

        liftData.isVertical = true
    }

    fun yAxisSpeedDistance(speed: Double, distance: Double) = this.apply {
        liftData.speedY = speed
        liftData.distanceY = distance
        liftData.durationY = Duration.seconds(distance / speed)

        liftData.isVertical = true
    }
}