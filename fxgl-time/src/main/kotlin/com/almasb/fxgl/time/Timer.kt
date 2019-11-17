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
 * Runs on the same thread that updates the timer.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class Timer {

    private val timerActions = CopyOnWriteArrayList<TimerAction>()

    /**
     * @return time in seconds accumulated by this timer
     */
    var now = 0.0
        private set

    /**
     * Call this to drive (advance) the timer.
     *
     * @param tpf time per frame by which to advance this timer
     */
    fun update(tpf: Double) {
        now += tpf

        timerActions.forEach {
            it.update(tpf)

            if (it.isExpired)
                timerActions.remove(it)
        }
    }

    /**
     * The Runnable [action] will be scheduled to start at given [interval].
     * The action will start for the first time after given interval.
     * The action will be scheduled unlimited number of times unless user cancels it
     * via the returned action object.
     *
     * @return timer action
     */
    fun runAtInterval(action: Runnable, interval: Duration): TimerAction {
        return runAtInterval(action, interval, Int.MAX_VALUE)
    }

    /**
     * The Runnable [action] will be scheduled to start at given [interval].
     * The action will start for the first time after given interval.
     * The action will be scheduled [limit] number of times unless user cancels it
     * via the returned action object.
     *
     * @return timer action
     */
    fun runAtInterval(action: Runnable, interval: Duration, limit: Int): TimerAction {
        val act = TimerAction(interval, action, limit)
        timerActions.add(act)
        return act
    }

    /**
     * The Runnable [action] will be scheduled to start at given [interval].
     * The Runnable action will be scheduled IFF
     * [whileCondition] is initially true.
     * The action will start for the first time after given interval.
     * The action will be removed from schedule when [whileCondition] becomes "false".
     * Note: you must retain the reference to the [whileCondition] property to avoid it being
     * garbage collected, otherwise the [action] may never stop.
     *
     * @return timer action
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
     * The Runnable [action] will be scheduled to run once after given [delay].
     * The action can be cancelled before it starts via the returned action object.
     *
     * @return timer action
     */
    fun runOnceAfter(action: Runnable, delay: Duration): TimerAction {
        return runAtInterval(action, delay, 1)
    }

    /**
     * The Runnable [action] will be scheduled to run once after given [delay].
     * The action can be cancelled before it starts via the returned action object.
     *
     * @return timer action
     */
    fun runOnceAfter(action: () -> Unit, delay: Duration): TimerAction {
        return runOnceAfter(Runnable(action), delay)
    }

    /**
     * Remove all scheduled actions.
     */
    fun clear() {
        timerActions.clear()
    }

    /**
     * Constructs a local timer that is driven by this timer.
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