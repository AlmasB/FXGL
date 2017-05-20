/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.almasb.fxgl.service;

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

    @Override
    default void onAchievementEvent(AchievementEvent event) {
        if (event.getEventType() == AchievementEvent.ACHIEVED) {
            pushNotification("You got an achievement! " + event.getAchievement().getName());
        } else if (event.getEventType() == AchievementProgressEvent.PROGRESS) {
            pushNotification("Achievement " + event.getAchievement().getName() + "\n"
                + "Progress: " + ((AchievementProgressEvent)event).getValue() + "/" + ((AchievementProgressEvent)event).getMax());
        }
    }
}
