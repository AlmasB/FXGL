/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.achievement

import javafx.event.EventType

/**
 * Fired when a numeric value based achievement has made some progress.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class AchievementProgressEvent(
        achievement: Achievement,
        val value: Double,
        val max: Double
) : AchievementEvent(PROGRESS, achievement) {

    companion object {
        val PROGRESS = EventType<AchievementProgressEvent>(AchievementEvent.ANY, "PROGRESS")
    }

    override fun toString() = "AchievementProgressEvent[value=$value,max=$max]"
}