/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
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
class TimerAction
internal constructor(

        /**
         * @param interval interval duration
         */
        interval: Duration,

        /**
         * @param action the action
         */
        private val action: Runnable,

        /**
         * @param limit number of times to run before self expiring
         * @defaltValue indefinite
         */
        private val limit: Int = Int.MAX_VALUE) {

    private val interval = interval.toSeconds()

    /**
     * @return true if the timer has expired, false if active
     */
    var isExpired = limit == 0
        private set

    var isPaused = false
        private set

    private var lastFired = 0.0
    private var currentTime = 0.0

    private var timesFired = 0

    /**
     * Updates the state of this timer action.
     * If the difference between current time
     * and recorded time is greater than the interval
     * then the action is executed and current time is recorded.
     *
     * Note: the action will not be executed if the timer
     * has expired.
     */
    fun update(tpf: Double) {
        if (isExpired || isPaused)
            return

        currentTime += tpf

        if (currentTime - lastFired >= interval) {
            action.run()
            timesFired++
            lastFired = currentTime

            if (timesFired == limit) {
                expire()
            }
        }
    }

    fun pause() {
        isPaused = true
    }

    fun resume() {
        isPaused = false
    }

    /**
     * Set the timer as expired. The action will no longer
     * be executed.
     */
    fun expire() {
        isExpired = true
    }
}
