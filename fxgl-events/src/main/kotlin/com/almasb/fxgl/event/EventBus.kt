/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.event

import com.almasb.sslogger.Logger
import javafx.event.Event
import javafx.event.EventHandler
import javafx.event.EventType
import javafx.scene.Group

/**
 * FXGL event dispatcher that uses JavaFX event system.
 * Allows firing events and listening for events.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
class EventBus {

    private val log = Logger.get(javaClass)

    private val eventHandlers = Group()

    /**
     * Register event handler for event type.
     */
    fun <T : Event> addEventHandler(eventType: EventType<T>, eventHandler: EventHandler<in T>): Subscriber {
        eventHandlers.addEventHandler(eventType, eventHandler)

        @Suppress("UNCHECKED_CAST")
        return Subscriber(this, eventType, eventHandler as EventHandler<in Event>)
    }

    /**
     * Remove event handler for event type.
     */
    fun <T : Event> removeEventHandler(eventType: EventType<T>, eventHandler: EventHandler<in T>) {
        eventHandlers.removeEventHandler(eventType, eventHandler)
    }

    /**
     * Fire given event.
     * All listening parties will be notified.
     * Events will be handled on the same thread that fired the event,
     * i.e. synchronous.
     */
    fun fireEvent(event: Event) {
        log.debug("Firing event: $event")

        eventHandlers.fireEvent(event)
    }
}