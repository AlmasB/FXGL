/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.gameplay;

/**
 * Marks a service that wants to listen for achievement events.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public interface AchievementListener {

    /**
     * Fired on achievement event.
     *
     * @param event the event
     */
    void onAchievementEvent(AchievementEvent event);
}
