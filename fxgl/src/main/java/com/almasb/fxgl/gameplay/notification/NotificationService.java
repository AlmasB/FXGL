/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.gameplay.notification;

import com.almasb.fxgl.gameplay.AchievementEvent;
import com.almasb.fxgl.gameplay.AchievementListener;
import com.almasb.fxgl.gameplay.AchievementProgressEvent;
import com.almasb.fxgl.ui.Position;
import javafx.scene.paint.Color;

/**
 * Notification service allows to push notifications.
 * This is a globally available service with globally visible notifications.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public interface NotificationService extends AchievementListener {

    /**
     * Push a notification with given message.
     *
     * @param message the message
     */
    void pushNotification(String message);

    /**
     *
     * @return notification position
     */
    Position getPosition();

    /**
     * Set position of future notifications.
     *
     * @param position where to show notification
     */
    void setPosition(Position position);

    /**
     *
     * @return current background color for notifications
     */
    Color getBackgroundColor();

    /**
     * Set background color of notifications.
     *
     * @param backgroundColor the color
     */
    void setBackgroundColor(Color backgroundColor);

    /**
     * @return current text color for notifications
     */
    Color getTextColor();

    /**
     * Set text color of notifications.
     *
     * @param textColor the color
     */
    void setTextColor(Color textColor);

    void onAchievementEvent(AchievementEvent event);
}
