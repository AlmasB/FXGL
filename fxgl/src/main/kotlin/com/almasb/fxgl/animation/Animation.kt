/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.almasb.fxgl.animation

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.app.State
import com.almasb.fxgl.app.listener.StateListener
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

    var isAutoReverse = false
    var onFinished: Runnable = EmptyRunnable

    private var time = 0.0

    // for single cycle
    private var endTime = duration.toSeconds()

    private var count = 0

    var isReverse = false

    var isPaused = false
        private set

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
        updateManual(tpf)
    }

    // TODO: use states?
    fun updateManual(tpf: Double) {
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
            //onCycleStarted()

            onProgress(animatedValue.getValue(if (isReverse) 1.0 else 0.0))

            updateTime(tpf)
            return
        }

        updateTime(tpf)

        if ((!isReverse && time >= endTime) || (isReverse && time <= 0.0)) {
            //onCycleFinished()

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