/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.gameplay;

import javafx.event.EventType;

/**
 * Fired when a numeric value based achievement has made some progress.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public final class AchievementProgressEvent extends AchievementEvent {

    public static final EventType<AchievementProgressEvent> PROGRESS =
            new EventType<>(ANY, "PROGRESS");

    private double value, max;

    public double getValue() {
        return value;
    }

    public double getMax() {
        return max;
    }

    public AchievementProgressEvent(Achievement achievement, double value, double max) {
        super(PROGRESS, achievement);
        this.value = value;
        this.max = max;
    }

    @Override
    public String toString() {
        return "AchievementProgressEvent[value=" + value + ",max=" + max + "]";
    }
}
