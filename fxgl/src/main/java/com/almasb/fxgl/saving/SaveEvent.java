/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.saving;

import javafx.event.Event;
import javafx.event.EventType;

/**
 * Occurs during save.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class SaveEvent extends Event {

    public static final EventType<SaveEvent> ANY =
            new EventType<>(Event.ANY, "SAVE_EVENT");

    private UserProfile profile;

    public UserProfile getProfile() {
        return profile;
    }

    public SaveEvent(UserProfile profile) {
        super(ANY);
        this.profile = profile;
    }

    @Override
    public String toString() {
        return "SaveEvent";
    }
}
