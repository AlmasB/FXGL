/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.time

import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.util.Duration
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Timer that supports running actions at an interval and with a delay.
 * Runs on the same thread that created the timer.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class Timer {

    /**
     * List for all timer based actions.
     */
    private val timerActions = CopyOnWriteArrayList<TimerAction>()

    /**
     * @return time in seconds accumulated in this state
     */
    var now = 0.0
        private set

    fun update(tpf: Double) {
        now += tpf

        val iter = timerActions.iterator()
        while (iter.hasNext()) {
            val action = iter.next()

            action.update(tpf)

            if (action.isExpired) {
                timerActions.remove(action)
            }
        }
    }

    /**
     * The Runnable action will be scheduled to start at given interval.
     * The action will start for the first time after given interval.
     *
     * Note: the scheduled action will not start while the game is paused.
     *
     * @param action   the action
     * @param interval time
     */
    fun runAtInterval(action: Runnable, interval: Duration): TimerAction {
        return runAtInterval(action, interval, Int.MAX_VALUE)
    }

    /**
     * The Runnable action will be scheduled to start at given interval.
     * The action will start for the first time after given interval.
     *
     * Note: the scheduled action will not start while the game is paused.
     *
     * @param action   the action
     * @param interval time
     * @param limit number of times to run
     */
    fun runAtInterval(action: Runnable, interval: Duration, limit: Int): TimerAction {
        val act = TimerAction(interval, action, limit)
        timerActions.add(act)
        return act
    }

    /**
     * The Runnable action will be scheduled for execution iff
     * whileCondition is initially true. If that's the case
     * then the Runnable action will be scheduled to start at given interval.
     * The action will start for the first time after given interval
     *
     * The action will be removed from schedule when whileCondition becomes `false`.
     *
     * Note: the scheduled action will not start while the game is paused
     *
     * @param action         action to execute
     * @param interval       interval between executions
     * @param whileCondition condition
     */
    fun runAtIntervalWhile(action: Runnable, interval: Duration, whileCondition: ReadOnlyBooleanProperty): TimerAction {
        if (!whileCondition.get()) {
            return TimerAction(interval, action, 0)
        }

        val act = TimerAction(interval, action)
        timerActions.add(act)

        whileCondition.addListener { _, _, isTrue ->
            if (!isTrue)
                act.expire()
        }

        return act
    }

    /**
     * The Runnable action will be executed once after given delay
     *
     *
     * Note: the scheduled action will not start while the game is paused
     *
     * @param action action to execute
     * @param delay  delay after which to execute
     */
    fun runOnceAfter(action: Runnable, delay: Duration): TimerAction {
        return runAtInterval(action, delay, 1)
    }

    fun runOnceAfter(action: () -> Unit, delay: Duration): TimerAction {
        return runOnceAfter(Runnable(action), delay)
    }

    /**
     * Remove all actions.
     */
    fun clear() {
        timerActions.clear()
    }

    /**
     * Simple timer to capture current time and check if certain time has passed.
     */
    fun newLocalTimer(): LocalTimer = object : LocalTimer {
        private var time = 0.0

        /**
         * Captures current time.
         */
        override fun capture() {
            time = now
        }

        /**
         * Returns true if difference between captured time
         * and now is greater or equal to given duration.
         *
         * @param duration time duration to check
         * @return true if elapsed, false otherwise
         */
        override fun elapsed(duration: Duration) =
                now - time >= duration.toSeconds()
    }
}