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

    var autoReverse = false
    var onFinished: Runnable = EmptyRunnable

    private var time = 0.0

    // for single cycle
    private var endTime = duration.toSeconds()

    private var count = 0

    var reverse = false
        private set

    var paused = false
        private set

    var animating = false
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
        if (!animating) {
            reverse = true
            start(state)
        }
    }

    fun start(state: State) {
        if (!animating) {
            this.state = state
            animating = true
            state.addStateListener(this)
        }
    }

    fun stop() {
        if (animating) {
            animating = false
            state.removeStateListener(this)
            time = 0.0
            count = 0
            reverse = false
            checkDelay = true
        }
    }

    fun pause() {
        paused = true
    }

    fun resume() {
        paused = false
    }

    override fun onUpdate(tpf: Double) {
        updateManual(tpf)
    }

    // TODO: use states?
    fun updateManual(tpf: Double) {
        if (paused)
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

        if ((reverse && time == endTime) || (!reverse && time == 0.0)) {
            //onCycleStarted()

            onProgress(animatedValue.getValue(if (reverse) 1.0 else 0.0))

            updateTime(tpf)
            return
        }

        updateTime(tpf)

        if ((!reverse && time >= endTime) || (reverse && time <= 0.0)) {
            //onCycleFinished()

            onProgress(animatedValue.getValue(if (reverse) 0.0 else 1.0))

            count++

            if (count >= cycleCount) {
                onFinished.run()
                stop()
            } else {
                if (autoReverse) {
                    reverse = !reverse
                }

                resetTime()
            }

            return
        }

        onProgress(animatedValue.getValue(time / endTime))
    }

    private fun updateTime(tpf: Double) {
        time += if (reverse) -tpf else tpf
    }

    private fun resetTime() {
        time = if (reverse) endTime else 0.0
    }

    abstract fun onProgress(value: T)
}