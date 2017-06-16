/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.service.impl.display

import javafx.beans.NamedArg
import javafx.event.Event
import javafx.event.EventType

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class DisplayEvent
internal constructor(@NamedArg("eventType") eventType: EventType<out Event>) : Event(eventType) {

    companion object {

        /**
         * Common super-type for all display event types.
         */
        @JvmField val ANY = EventType<DisplayEvent>(Event.ANY, "DISPLAY_EVENT")

        /**
         * Fired when user requests application close.
         */
        @JvmField val CLOSE_REQUEST = EventType(ANY, "CLOSE_REQUEST")
    }

    override fun toString(): String {
        return "DisplayEvent[type=" + getEventType() + "]"
    }
}