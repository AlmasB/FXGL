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

package com.almasb.fxgl.event

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.time.UpdateEvent
import com.almasb.fxgl.time.UpdateEventListener
import javafx.event.Event
import javafx.util.Duration

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class EventTrigger<out T : Event>
@JvmOverloads constructor(

        /**
         * When to fire the event.
         */
        val eventCondition: EventCondition,

        /**
         * Producer to be used when creating events.
         */
        val eventProducer: EventProducer<T>,

        /**
         * Number of times this event can be triggered.
         * Must be > 0.
         * Default is 1.
         */
        val limit: Int = 1,

        /**
         * Delay between triggering events.
         * Default is zero.
         */
        val interval: Duration = Duration.ZERO) {

    private var timesFired = 0
    private val timer = FXGL.newLocalTimer()

    init {
        if (limit <= 0)
            throw IllegalArgumentException("Trigger limit must be non-negative and non-zero")
    }

    fun reachedLimit() = timesFired == limit

    /**
     * Triggers (fires) the event created by trigger's event producer.
     */
    fun fire() {
        if (!reachedLimit()) {
            FXGL.getEventBus().fireEvent(eventProducer.produce())
            timesFired++
        }
    }

    fun onUpdate(tpf: Double) {
        if (eventCondition.isTrue() && (timer.elapsed(interval) || timesFired == 0)) {
            fire()
            timer.capture()
        }
    }
}