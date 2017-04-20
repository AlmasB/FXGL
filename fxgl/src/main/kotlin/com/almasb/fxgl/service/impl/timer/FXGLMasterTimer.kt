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

package com.almasb.fxgl.service.impl.timer

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.io.serialization.Bundle
import com.almasb.fxgl.service.MasterTimer
import com.almasb.fxgl.settings.UserProfile
import com.google.inject.Inject
import javafx.beans.property.ReadOnlyLongProperty
import javafx.beans.property.ReadOnlyLongWrapper
import javafx.beans.property.SimpleIntegerProperty
import javafx.util.Duration
import java.util.*

/**
 * Contains convenience methods and manages timer based actions.
 * Computes time taken by each frame.
 * Keeps track of tick and global time.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
class FXGLMasterTimer
@Inject
private constructor() : MasterTimer {

    override fun pause() {
        log.debug { "Stopping master timer" }
        paused = true
    }

    override fun resume() {
        log.debug { "Resuming master timer" }
        paused = false
    }

    private val log = FXGL.getLogger(javaClass)

    companion object {
        /**
         * Time per frame in seconds.
         *
         * @return 0.166(6)
         */
        fun tpfSeconds() = 1.0 / 60

        private val TPF_NANOS = 1000000000L / 60

        /**
         * Timer per frame in nanoseconds.
         *
         * @return 16666666
         */
        fun tpfNanos() = TPF_NANOS

        /**
         * Converts seconds to nanoseconds.
         *
         * @param seconds value in seconds
         * @return value in nanoseconds
         */
        fun secondsToNanos(seconds: Double) = (seconds * 1000000000L).toLong()

        /**
         * Converts type Duration to nanoseconds.
         *
         * @param duration value as Duration
         * @return value in nanoseconds
         */
        fun toNanos(duration: Duration) = secondsToNanos(duration.toSeconds())
    }

    init {
        log.debug("Service [MasterTimer] initialized")
    }


    /**
     * Holds current tick (frame)
     */
    private val tick = ReadOnlyLongWrapper(0)

    /**
     * @return tick
     */
    override fun tickProperty(): ReadOnlyLongProperty = tick.readOnlyProperty

    private var playtime = ReadOnlyLongWrapper(0)

    override fun playtimeProperty(): ReadOnlyLongProperty = playtime.readOnlyProperty

    override fun startMainLoop() {
        //log.debug("Starting main loop")
    }

    private var paused = false

    /**
     * Resets current tick to 0 and clears scheduled actions.
     */
    override fun reset() {
        log.debug("Resetting ticks and clearing all actions")

        tick.set(0)
        now = 0

    }

    /**
     * Time for this tick in nanoseconds.
     */
    private var now: Long = 0

    /**
     * Current time for this tick in nanoseconds.
     * Also time elapsed
     * from the start of game.
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
     * Counter to compute FPS and keep fluctuations
     * to minimum.
     */
    private val fpsCounter = FPSCounter()

    /**
     * Average render FPS.
     */
    private val fps = SimpleIntegerProperty(60)

    /**
     * @return average render FPS property
     */
    override fun fpsProperty() = fps

    /**
     * Average performance FPS.
     */
    private val performanceFPS = SimpleIntegerProperty()

    /**
     * @return Average performance FPS property
     */
    override fun performanceFPSProperty() = performanceFPS





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
