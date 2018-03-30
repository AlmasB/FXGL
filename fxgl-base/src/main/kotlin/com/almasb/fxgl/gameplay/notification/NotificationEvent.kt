/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.gameplay.notification

import javafx.event.Event
import javafx.event.EventType

/**
 * This event occurs when a notification has been shown to user.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
class NotificationEvent(val notification: Notification) : Event(ANY) {

    companion object {
        @JvmField val ANY = EventType<NotificationEvent>(Event.ANY, "NOTIFICATION_EVENT")
    }

    override fun toString() = "NotificationEvent[message=${notification.message}]"
}