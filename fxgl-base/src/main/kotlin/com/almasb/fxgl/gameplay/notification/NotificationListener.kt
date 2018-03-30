/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.gameplay.notification

/**
 * Listener for notification events.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
interface NotificationListener {

    /**
     * Fired on notification event.
     *
     * @param event the notification event
     */
    fun onNotificationEvent(event: NotificationEvent)
}