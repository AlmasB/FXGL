/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.gameplay.achievement

/**
 * Listener for achievement events.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
interface AchievementListener {

    /**
     * Fired on achievement event.
     *
     * @param event the event
     */
    fun onAchievementEvent(event: AchievementEvent)
}