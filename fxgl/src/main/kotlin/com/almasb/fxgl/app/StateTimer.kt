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

package com.almasb.fxgl.app

import com.almasb.fxgl.time.FXGLLocalTimer
import com.almasb.fxgl.time.LocalTimer
import com.almasb.fxgl.time.TimerAction
import com.almasb.fxgl.time.TimerActionImpl
import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.util.Duration
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Timer that runs in and belongs to a single state.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class StateTimer {

    /**
     * List for all timer based actions.
     */
    private val timerActions = CopyOnWriteArrayList<TimerActionImpl>()

    /**
     * @return time in seconds accumulated in this state
     */
    var now = 0.0
        private set

    fun update(tpf: Double) {
        now += tpf

        timerActions.forEach { it.update(tpf) }
        timerActions.removeIf(TimerActionImpl::isExpired)
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
        val act = TimerActionImpl(interval, action, TimerActionImpl.TimerType.INDEFINITE)
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
            throw IllegalArgumentException("While condition is false")
        }
        val act = TimerActionImpl(interval, action, TimerActionImpl.TimerType.INDEFINITE)
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
        val act = TimerActionImpl(delay, action, TimerActionImpl.TimerType.ONCE)
        timerActions.add(act)
        return act
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

    fun newLocalTimer(): LocalTimer {
        return FXGLLocalTimer(this)
    }
}