/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.animation

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.app.State
import com.almasb.fxgl.app.listener.StateListener
import com.almasb.fxgl.entity.animation.AnimationBuilder
import com.almasb.fxgl.util.EmptyRunnable
import javafx.util.Duration

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
abstract class Animation<T>
@JvmOverloads constructor(val delay: Duration = Duration.ZERO,
                          val duration: Duration,
                          var cycleCount: Int = 1,
                          val animatedValue: AnimatedValue<T>): StateListener {

    constructor(animationBuilder: AnimationBuilder, animatedValue: AnimatedValue<T>) : this(animationBuilder.delay,
            animationBuilder.duration,
            animationBuilder.times,
            animatedValue) {
        onFinished = animationBuilder.onFinished
        isAutoReverse = animationBuilder.isAutoReverse
    }

    var isAutoReverse = false
    var onFinished: Runnable = EmptyRunnable

    private var time = 0.0

    // for single cycle
    private var endTime = duration.toSeconds()

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