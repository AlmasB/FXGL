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
class XboxNotificationViewFactory : NotificationViewFactory {

    override fun newView(notification: Notification): NotificationView {
        return XboxNotificationView(notification.message, notification.bgColor, notification.position)
    }
}