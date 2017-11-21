/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.gameplay.notification;

import javafx.event.Event;
import javafx.event.EventType;

/**
 * This event occurs when a notification has been shown to user.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public final class NotificationEvent extends Event {

    public static final EventType<NotificationEvent> ANY =
            new EventType<>(Event.ANY, "NOTIFICATION_EVENT");

    private Notification notification;

    /**
     * @return notification associated with the event
     */
    public Notification getNotification() {
        return notification;
    }

    public NotificationEvent(Notification notification) {
        super(ANY);
        this.notification = notification;
    }

    @Override
    public String toString() {
        return "NotificationEvent[message=" + notification.getMessage() + "]";
    }
}
