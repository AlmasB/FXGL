/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.event

import javafx.event.Event
import javafx.event.EventHandler
import javafx.event.EventType

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
class Subscriber
internal constructor(private val bus: EventBus,
                     private val eventType: EventType<out Event>,
                     private val eventHandler: EventHandler<in Event>) {

    /**
     * Stop listening for events.
     */
    fun unsubscribe() {
        bus.removeEventHandler(eventType, eventHandler)
    }
}