/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.gameplay.notification

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
interface NotificationViewFactory {

    // TODO: separate new view and repeated notification
    // e.g. first time play animation but if the 2nd notification is pushed before view is removed
    // we might use a different call back / animation for that

    fun newView(notification: Notification): NotificationView
}