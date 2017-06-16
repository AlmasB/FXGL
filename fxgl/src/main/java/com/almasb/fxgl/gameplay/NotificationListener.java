/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.gameplay;

/**
 * Marks a service that wants to listen for notification events.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public interface NotificationListener {

    /**
     * Fired on notification event.
     *
     * @param event the notification event
     */
    void onNotificationEvent(NotificationEvent event);
}
