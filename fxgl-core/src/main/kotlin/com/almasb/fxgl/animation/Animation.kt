/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.animation

import com.almasb.fxgl.core.Updatable
import javafx.animation.Interpolator
import javafx.util.Duration

/**
 * An animation needs to be updated by calling onUpdate().
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
abstract class Animation<T>(
        config: AnimationBuilder,
        val animatedValue: AnimatedValue<T>): Updatable {

    var isAutoReverse: Boolean = config.isAutoReverse
    var onFinished: Runnable = config.onFinished
    var onCycleFinished: Runnable = config.onCycleFinished

    var interpolator: Interpolator = config.interpolator

    private var time = 0.0

    // for single cycle
    var endTime = config.duration.toSeconds()
        private set

    private var count = 0

    var isReverse = false

    var isPaused = false
        private set

    /**
     * True between start and stop.
     * Pauses have no effect on this flag.
     */
    var isAnimating = false
        private set

    private val delay = config.delay

    private var checkDelay = delay.greaterThan(Duration.ZERO)

    var cycleCount = config.times

    fun startReverse() {
        if (!isAnimating) {
            isReverse = true
            start()
        }
    }

    fun start() {
        if (!isAnimating) {
            isAnimating = true
            resetTime()
            onProgress(animatedValue.getValue(if (isReverse) 1.0 else 0.0))
        }
    }

    fun stop() {
        if (isAnimating) {
            isAnimating = false
            time = 0.0
            count = 0
            isReverse = false
            checkDelay = delay.greaterThan(Duration.ZERO)
        }
    }

    fun pause() {
        isPaused = true
    }

    fun resume() {
        isPaused = false
    }

    override fun onUpdate(tpf: Double) {
        if (isPaused || !isAnimating)
            return

        if (checkDelay) {
            time += tpf

            if (time >= delay.toSeconds()) {
                checkDelay = false
                resetTime()
                return
            } else {
                return
            }
        }

        updateTime(tpf)

        if ((!isReverse && time >= endTime) || (isReverse && time <= 0.0)) {
            onProgress(animatedValue.getValue(if (isReverse) 0.0 else 1.0))

            onCycleFinished.run()
            count++

            if (count >= cycleCount) {
                onFinished.run()
                stop()
            } else {
                if (isAutoReverse) {
                    isReverse = !isReverse
                }

                resetTime()
            }

            return
        }

        onProgress(animatedValue.getValue(time / endTime, interpolator))
    }

    private fun updateTime(tpf: Double) {
        time += if (isReverse) -tpf else tpf
    }

    private fun resetTime() {
        time = if (isReverse) endTime else 0.0
    }

    fun setTimeTo(newTime: Double) {
        time = newTime

        onProgress(animatedValue.getValue(time / endTime, interpolator))
    }

    abstract fun onProgress(value: T)
}