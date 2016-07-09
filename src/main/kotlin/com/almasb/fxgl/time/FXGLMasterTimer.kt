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

import com.almasb.easyio.serialization.Bundle
import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.event.UpdateEvent
import com.almasb.fxgl.settings.UserProfile
import com.almasb.fxgl.time.TimerActionImpl.TimerType
import com.google.inject.Inject
import com.google.inject.Singleton
import javafx.animation.AnimationTimer
import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.beans.property.ReadOnlyLongWrapper
import javafx.beans.property.SimpleIntegerProperty
import javafx.util.Duration
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Contains convenience methods and manages timer based actions.
 * Computes time taken by each frame.
 * Keeps track of tick and global time.

 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
@Singleton
class FXGLMasterTimer
@Inject
private constructor() : AnimationTimer(), MasterTimer {

    override fun onInitComplete() {
    }

    override fun onPause() {
        stop()
    }

    override fun onResume() {
        start()
    }

    override fun onReset() {
        reset()
    }

    override fun onExit() {

    }

    private val log = FXGL.getLogger(javaClass)

    companion object {
        /**
         * Time per frame in seconds.

         * @return 0.166(6)
         */
        fun tpfSeconds() = 1.0 / 60

        /**
         * Timer per frame in nanoseconds.

         * @return 16666666
         */
        fun tpfNanos() = 1000000000L / 60

        /**
         * Converts seconds to nanoseconds.

         * @param seconds value in seconds
         * *
         * @return value in nanoseconds
         */
        fun secondsToNanos(seconds: Double) = (seconds * 1000000000L).toLong()

        /**
         * Converts type Duration to nanoseconds.

         * @param duration value as Duration
         * *
         * @return value in nanoseconds
         */
        fun toNanos(duration: Duration) = secondsToNanos(duration.toSeconds())
    }

    init {
        log.debug("Service [MasterTimer] initialized")
    }

    private var updateListener: UpdateEventListener? = null

    override fun setUpdateListener(listener: UpdateEventListener) {
        updateListener = listener
    }

    /**
     * This is the internal FXGL update tick,
     * executed 60 times a second ~ every 0.166 (6) seconds.
     *
     * @param internalTime - The timestamp of the current frame given in nanoseconds (from JavaFX)
     */
    override fun handle(internalTime: Long) {
        // this will set up current tick and current time
        // for the rest of the game modules to use
        tickStart(internalTime)

        timerActions.forEach { action -> action.update(now) }
        timerActions.removeIf { it.isExpired }

        // this is the master update event
        updateListener?.onUpdateEvent(UpdateEvent(getTick(), tpf))

        // this is only end for our processing tick for basic profiling
        // the actual JavaFX tick ends when our new tick begins. So
        // JavaFX event callbacks will properly fire within the same "our" tick.
        tickEnd()
    }

    /**
     * List for all timer based actions
     */
    private val timerActions = CopyOnWriteArrayList<TimerActionImpl>()

    /**
     * Holds current tick (frame)
     */
    private val tick = ReadOnlyLongWrapper(0)

    /**
     * @return tick
     */
    override fun tickProperty() = tick.readOnlyProperty

    private var playtime = ReadOnlyLongWrapper(0)

    override fun playtimeProperty() = playtime.readOnlyProperty

    override fun start() {
        log.debug { "Starting master timer" }
        super.start()
    }

    override fun stop() {
        log.debug { "Stopping master timer" }
        super.stop()
    }

    /**
     * Resets current tick to 0 and clears scheduled actions.
     */
    override fun reset() {
        log.debug("Resetting ticks and clearing all actions")

        tick.set(0)
        now = 0

        timerActions.clear()
    }

    /**
     * Time for this tick in nanoseconds.
     */
    private var now: Long = 0

    /**
     * Current time for this tick in nanoseconds. Also time elapsed
     * from the start of game. This time does not change while the game is paused.
     * This time does not change while within the same tick.
     *
     * @return current time in nanoseconds
     */
    override fun getNow(): Long {
        return now
    }

    private var tpf = 0.0

    override fun tpf() = tpf

    /**
     * These are used to approximate FPS value
     */
    private val fpsCounter = FPSCounter()
    private val fpsPerformanceCounter = FPSCounter()

    /**
     * Used as delta from internal JavaFX timestamp to calculate render FPS
     */
    private var previousInternalTime: Long = 0

    /**
     * Average render FPS
     */
    private val fps = SimpleIntegerProperty()

    /**
     * @return average render FPS property
     */
    override fun fpsProperty() = fps

    /**
     * Average performance FPS
     */
    private val performanceFPS = SimpleIntegerProperty()

    /**
     * @return Average performance FPS property
     */
    override fun performanceFPSProperty() = performanceFPS

    private var startNanos: Long = -1
    private var realTPF: Long = -1

    /**
     * Called at the start of a game update tick.
     * This is where tick becomes tick + 1.

     * @param internalTime internal JavaFX time
     */
    private fun tickStart(internalTime: Long) {
        tick.set(tick.get() + 1)

        startNanos = System.nanoTime()
        realTPF = internalTime - previousInternalTime

        if (realTPF > tpfNanos()) {
            realTPF = tpfNanos()
        }

        now += realTPF
        playtime.value += realTPF

        previousInternalTime = internalTime
        tpf = realTPF / 1000000000.0
    }

    /**
     * Called at the end of a game update tick.
     */
    private fun tickEnd() {
        performanceFPS.set(Math.round(fpsPerformanceCounter.count((secondsToNanos(1.0) / (System.nanoTime() - startNanos)).toFloat())))
        fps.set(Math.round(fpsCounter.count((secondsToNanos(1.0) / realTPF).toFloat())))
    }

    /**
     * The Runnable action will be scheduled to run at given interval.
     * The action will run for the first time after given interval.
     *
     *
     * Note: the scheduled action will not run while the game is paused.
     *
     * @param action   the action
     * *
     * @param interval time
     */
    override fun runAtInterval(action: Runnable, interval: Duration): TimerAction {
        val act = TimerActionImpl(getNow(), interval, action, TimerType.INDEFINITE)
        timerActions.add(act)
        return act
    }

    /**
     * The Runnable action will be scheduled for execution iff
     * whileCondition is initially true. If that's the case
     * then the Runnable action will be scheduled to run at given interval.
     * The action will run for the first time after given interval
     *
     * The action will be removed from schedule when whileCondition becomes `false`.
     *
     * Note: the scheduled action will not run while the game is paused
     *
     * @param action         action to execute
     * *
     * @param interval       interval between executions
     * *
     * @param whileCondition condition
     */
    override fun runAtIntervalWhile(action: Runnable, interval: Duration, whileCondition: ReadOnlyBooleanProperty): TimerAction {
        if (!whileCondition.get()) {
            throw IllegalArgumentException("While condition is false")
        }
        val act = TimerActionImpl(getNow(), interval, action, TimerType.INDEFINITE)
        timerActions.add(act)

        whileCondition.addListener { obs, old, isTrue ->
            if (!isTrue)
                act.expire()
        }

        return act
    }

    /**
     * The Runnable action will be executed once after given delay
     *
     *
     * Note: the scheduled action will not run while the game is paused

     * @param action action to execute
     * *
     * @param delay  delay after which to execute
     */
    override fun runOnceAfter(action: Runnable, delay: Duration): TimerAction {
        val act = TimerActionImpl(getNow(), delay, action, TimerType.ONCE)
        timerActions.add(act)
        return act
    }

    override fun save(profile: UserProfile) {
        log.debug("Saving data to profile")

        val bundle = Bundle("timer")
        bundle.put("playtime", playtime.value)

        bundle.log()
        profile.putBundle(bundle)
    }

    override fun load(profile: UserProfile) {
        log.debug("Loading data from profile")
        val bundle = profile.getBundle("timer")
        bundle.log()

        playtime.value = bundle.get<Long>("playtime")
    }
}
