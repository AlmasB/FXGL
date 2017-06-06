/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.gameplay;

import javafx.event.Event;
import javafx.event.EventType;

/**
 * Occurs on achievement unlocked.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class AchievementEvent extends Event {

    public static final EventType<AchievementEvent> ANY =
            new EventType<>(Event.ANY, "ACHIEVEMENT_EVENT");

    public static final EventType<AchievementEvent> ACHIEVED =
            new EventType<>(ANY, "ACHIEVED");

    private Achievement achievement;

    public AchievementEvent(Achievement achievement) {
        this(ANY, achievement);
    }

    public AchievementEvent(EventType<? extends AchievementEvent> eventType, Achievement achievement) {
        super(eventType);
        this.achievement = achievement;
    }

    /**
     * @return achievement associated with the event
     */
    public Achievement getAchievement() {
        return achievement;
    }

    @Override
    public String toString() {
        return "AchievementEvent[name=" + achievement.getName()
                + ",description= " + achievement.getDescription() + "]";
    }
}
