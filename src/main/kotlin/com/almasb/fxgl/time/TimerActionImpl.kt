/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2016 AlmasB (almaslvl@gmail.com)
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

package com.almasb.fxgl.time

import javafx.util.Duration

/**
 * A wrapper for Runnable which is executed at given intervals.
 * The timer can be made to expire, in which case the action
 * will not execute.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
class TimerActionImpl
/**
 * @param now      current time in nanoseconds
 * @param interval interval duration
 * @param action   the action
 * @param type     ONCE or INDEFINITE
 */
(private var time: Long, interval: Duration, private val action: Runnable, private val type: TimerActionImpl.TimerType)
: TimerAction {

    enum class TimerType {
        ONCE, INDEFINITE
    }

    private val interval: Double

    /**
     * @return true if the timer has expired, false if active
     */
    private var isExpired = false
    override fun isExpired() = isExpired

    private var isPaused = false
    override fun isPaused() = isPaused

    init {
        this.interval = FXGLMasterTimer.secondsToNanos(interval.toSeconds()).toDouble()
    }

    /**
     * Updates the state of this timer action.
     * If the difference between current time
     * and recorded time is greater than the interval
     * then the action is executed and current time is recorded.
     *
     * Note: the action will not be executed if the timer
     * has expired.
     *
     * @param now current time in nanoseconds
     */
    fun update(now: Long) {
        if (isExpired || isPaused)
            return

        if (now - time >= interval) {
            action.run()
            time = now

            if (type == TimerType.ONCE) {
                expire()
            }
        }
    }

    override fun pause() {
        isPaused = true
    }

    override fun resume() {
        isPaused = false
    }

    /**
     * Set the timer as expired. The action will no longer
     * be executed.
     */
    override fun expire() {
        isExpired = true
    }
}
