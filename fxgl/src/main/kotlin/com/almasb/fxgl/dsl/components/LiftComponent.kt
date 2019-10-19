/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.dsl.components

import com.almasb.fxgl.core.math.Vec2
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

    private class LiftData {

        var isOn = false

        // moving in the direction of the axis
        var isGoingPositive = true

        var distance = 0.0
        var duration: Duration = Duration.ZERO
        var speed = 0.0

        internal lateinit var timer: LocalTimer

        fun initTimer() {
            timer = FXGL.newLocalTimer()
            timer.capture()
        }

        fun enable(distance: Double, duration: Duration, speed: Double) {
            this.distance = distance
            this.duration = duration
            this.speed = speed

            isOn = true
        }

        fun update(tpf: Double): Double {
            if (timer.elapsed(duration)) {
                isGoingPositive = !isGoingPositive
                timer.capture()
            }
            return if (isGoingPositive) speed * tpf else -speed * tpf
        }
    }

    private val liftDataX = LiftData()
    private val liftDataY = LiftData()

    var isGoingRight: Boolean
        get() = liftDataX.isGoingPositive
        set(value) { liftDataX.isGoingPositive = value }

    var isGoingUp: Boolean
        get() = !liftDataY.isGoingPositive
        set(value) { liftDataY.isGoingPositive = !value }

    override fun onAdded() {
        liftDataX.initTimer()
        liftDataY.initTimer()
    }

    override fun onUpdate(tpf: Double) {
        val vector = Vec2()
        if (liftDataX.isOn) {
            vector.x = liftDataX.update(tpf).toFloat()
        }
        if (liftDataY.isOn) {
            vector.y = liftDataY.update(tpf).toFloat()
        }
        entity.translate(vector)
    }

    fun xAxisDistanceDuration(distance: Double, duration: Duration) = this.apply {
        liftDataX.enable(distance, duration, distance / duration.toSeconds())
    }

    fun xAxisSpeedDuration(speed: Double, duration: Duration) = this.apply {
        liftDataX.enable(speed * duration.toSeconds(), duration, speed)
    }

    fun xAxisSpeedDistance(speed: Double, distance: Double) = this.apply {
        liftDataX.enable(distance, Duration.seconds(distance / speed), speed)
    }

    fun yAxisDistanceDuration(distance: Double, duration: Duration) = this.apply {
        liftDataY.enable(distance, duration, distance / duration.toSeconds())
    }

    fun yAxisSpeedDuration(speed: Double, duration: Duration) = this.apply {
        liftDataY.enable(speed * duration.toSeconds(), duration, speed)
    }

    fun yAxisSpeedDistance(speed: Double, distance: Double) = this.apply {
        liftDataY.enable(distance, Duration.seconds(distance / speed), speed)
    }
}