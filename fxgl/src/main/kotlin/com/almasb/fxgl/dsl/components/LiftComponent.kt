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
class LiftComponent
@JvmOverloads constructor(timer: LocalTimer = FXGL.newLocalTimer()) : Component() {

    private val liftDataX = LiftData(timer)
    private val liftDataY = LiftData(timer)

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
        var dx = 0.0
        var dy = 0.0

        if (liftDataX.isOn) {
            dx = liftDataX.update(tpf)
        }

        if (liftDataY.isOn) {
            dy = liftDataY.update(tpf)
        }

        entity.translate(dx, dy)
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

    override fun isComponentInjectionRequired(): Boolean = false
}

private class LiftData(val timer: LocalTimer) {

    var isOn = false

    // moving in the direction of the axis
    var isGoingPositive = true

    var distance = 0.0
    var duration: Duration = Duration.ZERO
    var speed = 0.0

    fun initTimer() {
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