/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.gameplay.notification

import com.almasb.fxgl.gameplay.achievement.AchievementListener
import com.almasb.fxgl.ui.Position
import javafx.scene.paint.Color

/**
 * Notification service allows to push notifications.
 * This is a globally available service with globally visible notifications.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
interface NotificationService : AchievementListener {

    /**
     *
     * @return notification position
     */
    /**
     * Set position of future notifications.
     *
     * @param position where to show notification
     */
    var position: Position

    /**
     *
     * @return current background color for notifications
     */
    /**
     * Set background color of notifications.
     *
     * @param backgroundColor the color
     */
    var backgroundColor: Color

    /**
     * @return current text color for notifications
     */
    /**
     * Set text color of notifications.
     *
     * @param textColor the color
     */
    var textColor: Color

    /**
     * Push a notification with given message.
     *
     * @param message the message
     */
    fun pushNotification(message: String)
}