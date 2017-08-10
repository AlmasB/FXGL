/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.event

import com.almasb.fxgl.app.FXGL
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

    internal fun onUpdate(tpf: Double) {
        if (eventCondition.isTrue() && (timer.elapsed(interval) || timesFired == 0)) {
            fire()
            timer.capture()
        }
    }
}