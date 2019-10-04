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

        var isHorizontal = false
        var isGoingPositive = true
        var speed = 0.0
        var distance = 0.0
        var duration: Duration = Duration.ZERO

        internal lateinit var timer: LocalTimer
    }

    private val liftDataX = LiftData()
    private val liftDataY = LiftData()

    override fun onAdded() {
        initTimer(liftDataX)
        initTimer(liftDataY)
    }

    private fun initTimer(liftData: LiftData) {
        liftData.timer = FXGL.newLocalTimer()
        liftData.timer.capture()
    }

    override fun onUpdate(tpf: Double) {
        if (liftDataX.isHorizontal) {
            if (liftDataX.timer.elapsed(liftDataX.duration)) {
                liftDataX.isGoingPositive = !liftDataX.isGoingPositive
                liftDataX.timer.capture()
            }

            entity.translateX(if (liftDataX.isGoingPositive) liftDataX.speed * tpf else -liftDataX.speed * tpf)
        }

        if (!liftDataY.isHorizontal) {
            if (liftDataY.timer.elapsed(liftDataY.duration)) {
                liftDataY.isGoingPositive = !liftDataY.isGoingPositive
                liftDataY.timer.capture()
            }

            entity.translateY(if (liftDataY.isGoingPositive) -liftDataY.speed * tpf else liftDataY.speed * tpf)
        }
    }

    fun xAxisDistanceDuration(distance: Double, duration: Duration) = this.apply {
        liftDataX.distance = distance
        liftDataX.duration = duration
        liftDataX.speed = distance / duration.toSeconds()

        liftDataX.isHorizontal = true
    }

    fun xAxisSpeedDuration(speed: Double, duration: Duration) = this.apply {
        liftDataX.speed = speed
        liftDataX.duration = duration
        liftDataX.distance = speed * duration.toSeconds()

        liftDataX.isHorizontal = true
    }

    fun xAxisSpeedDistance(speed: Double, distance: Double) = this.apply {
        liftDataX.speed = speed
        liftDataX.distance = distance
        liftDataX.duration = Duration.seconds(distance / speed)

        liftDataX.isHorizontal = true
    }

    fun yAxisDistanceDuration(distance: Double, duration: Duration) = this.apply {
        liftDataY.distance = distance
        liftDataY.duration = duration
        liftDataY.speed = distance / duration.toSeconds()

        liftDataY.isHorizontal = false
    }

    fun yAxisSpeedDuration(speed: Double, duration: Duration) = this.apply {
        liftDataY.speed = speed
        liftDataY.duration = duration
        liftDataY.distance = speed * duration.toSeconds()

        liftDataY.isHorizontal = false
    }

    fun yAxisSpeedDistance(speed: Double, distance: Double) = this.apply {
        liftDataY.speed = speed
        liftDataY.distance = distance
        liftDataY.duration = Duration.seconds(distance / speed)

        liftDataY.isHorizontal = false
    }
}