/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.dsl

import com.almasb.fxgl.event.EventBus
import com.almasb.fxgl.time.Timer
import com.almasb.fxgl.time.TimerAction
import javafx.event.Event
import javafx.event.EventType
import javafx.util.Duration
import java.util.function.Supplier

private val FUNCTION_CALL = EventType<FunctionCallEvent>(EventType.ROOT, "FUNCTION_CALL")

private object FunctionCallEvent : Event(FUNCTION_CALL)

class EventBuilder {

    private var condition: Supplier<Boolean> = Supplier { false }
    private var eventSupplier: Supplier<Event>? = null
    private var limit = 1
    private var interval = Duration.ZERO

    private var timerAction: TimerAction? = null

    /**
     * Delay between triggering events.
     * Default is zero.
     */
    fun interval(interval: Duration) = this.also {
        this.interval = interval
    }

    /**
     * When to fire the event.
     */
    fun `when`(condition: Supplier<Boolean>) = this.also {
        this.condition = condition
    }

    /**
     * Maximum number of events to emit.
     */
    fun limit(times: Int) = this.also {
        require(times > 0) { "Trigger limit must be non-negative and non-zero" }

        this.limit = times
    }

    fun thenRun(action: Runnable) = thenFire {
        action.run()
        FunctionCallEvent
    }

    fun thenFire(event: Event) = thenFire(Supplier { event })

    fun thenFire(eventSupplier: Supplier<Event>) = this.also {
        this.eventSupplier = eventSupplier
    }

    fun buildAndStart(): TimerAction = buildAndStart(FXGL.getEventBus(), FXGL.getGameTimer())

    fun buildAndStart(eventBus: EventBus, timer: Timer): TimerAction {
        timerAction = timer.runAtInterval(object : Runnable {
            private var count = 0

            override fun run() {
                if (condition.get()) {
                    eventSupplier?.let {
                        eventBus.fireEvent(it.get())

                        count++

                        if (count == limit) {
                            timerAction!!.expire()
                        }
                    }
                }
            }
        }, interval)

        return timerAction!!
    }
}

