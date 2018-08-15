/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.animation

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.app.State
import com.almasb.fxgl.app.listener.StateListener
import com.almasb.fxgl.entity.animation.EntityAnimationBuilder
import com.almasb.fxgl.util.Consumer
import com.almasb.fxgl.util.EmptyRunnable
import javafx.animation.Interpolator
import javafx.util.Duration

open class AnimationBuilder
@JvmOverloads constructor(
        var duration: Duration = Duration.seconds(1.0),
        var delay: Duration = Duration.ZERO,
        var interpolator: Interpolator = Interpolator.LINEAR,
        var times: Int = 1,
        var onFinished: Runnable = EmptyRunnable,
        var isAutoReverse: Boolean = false) {

    fun duration(duration: Duration): AnimationBuilder {
        this.duration = duration
        return this
    }

    fun delay(delay: Duration): AnimationBuilder {
        this.delay = delay
        return this
    }

    fun interpolator(interpolator: Interpolator): AnimationBuilder {
        this.interpolator = interpolator
        return this
    }

    fun repeat(times: Int): AnimationBuilder {
        this.times = times
        return this
    }

    fun repeatInfinitely(): AnimationBuilder {
        return repeat(Integer.MAX_VALUE)
    }

    fun onFinished(onFinished: Runnable): AnimationBuilder {
        this.onFinished = onFinished
        return this
    }

    fun autoReverse(autoReverse: Boolean): AnimationBuilder {
        this.isAutoReverse = autoReverse
        return this
    }

    open fun <T> build(animatedValue: AnimatedValue<T>, onProgress: Consumer<T>): Animation<T> {
        animatedValue.interpolator = this.interpolator

        return object : Animation<T>(this, animatedValue) {
            override fun onProgress(value: T) {
                onProgress.accept(value)
            }
        }
    }
}

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
abstract class Animation<T>(
        val builder: AnimationBuilder,
        val animatedValue: AnimatedValue<T>): StateListener {

    var isAutoReverse: Boolean = builder.isAutoReverse
    var onFinished: Runnable = builder.onFinished

    private var time = 0.0

    // for single cycle
    private var endTime = builder.duration.toSeconds()

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

    private var checkDelay = true

    private val delay = builder.delay

    var cycleCount = builder.times

    /**
     * State in which we are animating.
     */
    private lateinit var state: State

    fun startInPlayState() {
        start(FXGL.getApp().stateMachine.playState)
    }

    fun startReverse(state: State) {
        if (!isAnimating) {
            isReverse = true
            start(state)
        }
    }

    fun start(state: State) {
        if (!isAnimating) {
            this.state = state
            isAnimating = true
            state.addStateListener(this)
        }
    }

    fun stop() {
        if (isAnimating) {
            isAnimating = false
            state.removeStateListener(this)
            time = 0.0
            count = 0
            isReverse = false
            checkDelay = true
        }
    }

    fun pause() {
        isPaused = true
    }

    fun resume() {
        isPaused = false
    }

    override fun onUpdate(tpf: Double) {
        if (isPaused)
            return

        if (checkDelay) {
            time += tpf

            if (time >= delay.toSeconds()) {
                checkDelay = false
                resetTime()
            } else {
                return
            }
        }

        if ((isReverse && time == endTime) || (!isReverse && time == 0.0)) {
            onProgress(animatedValue.getValue(if (isReverse) 1.0 else 0.0))

            updateTime(tpf)
            return
        }

        updateTime(tpf)

        if ((!isReverse && time >= endTime) || (isReverse && time <= 0.0)) {
            onProgress(animatedValue.getValue(if (isReverse) 0.0 else 1.0))

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

        onProgress(animatedValue.getValue(time / endTime))
    }

    private fun updateTime(tpf: Double) {
        time += if (isReverse) -tpf else tpf
    }

    private fun resetTime() {
        time = if (isReverse) endTime else 0.0
    }

    abstract fun onProgress(value: T)
}