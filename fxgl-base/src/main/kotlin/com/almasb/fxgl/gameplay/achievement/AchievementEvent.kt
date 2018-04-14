/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.gameplay.achievement

import javafx.event.Event
import javafx.event.EventType

/**
 * Occurs on achievement unlocked.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
open class AchievementEvent(
        eventType: EventType<out AchievementEvent>,
        val achievement: Achievement
) : Event(eventType) {

    companion object {
        @JvmField val ANY = EventType<AchievementEvent>(Event.ANY, "ACHIEVEMENT_EVENT")

        @JvmField val ACHIEVED = EventType(ANY, "ACHIEVED")
    }

    constructor(achievement: Achievement) : this(ANY, achievement) {}

    override fun toString() =
            "AchievementEvent[name=${achievement.name}, description=${achievement.description}]"
}