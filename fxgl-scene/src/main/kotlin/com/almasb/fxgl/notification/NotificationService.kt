/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.notification

import com.almasb.fxgl.core.EngineService
import javafx.scene.paint.Color

/**
 * Notification service allows to push notifications.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
abstract class NotificationService : EngineService() {

    /**
     * Current background color for notifications.
     */
    abstract var backgroundColor: Color

    /**
     * Current text color for notifications.
     */
    abstract var textColor: Color

    /**
     * Push a notification with given [message].
     */
    abstract fun pushNotification(message: String)
}