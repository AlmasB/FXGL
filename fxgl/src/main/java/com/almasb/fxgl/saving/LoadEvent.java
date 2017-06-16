/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.saving;

import javafx.event.Event;
import javafx.event.EventType;

/**
 * Event related to any type of loading.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public final class LoadEvent extends Event {

    public static final EventType<LoadEvent> ANY =
            new EventType<>(Event.ANY, "LOAD_EVENT");

    /**
     * Fired when profile is being loaded.
     */
    public static final EventType<LoadEvent> LOAD_PROFILE =
            new EventType<>(ANY, "LOAD_PROFILE");

    /**
     * Fired when settings are being restored.
     */
    public static final EventType<LoadEvent> RESTORE_SETTINGS =
            new EventType<>(ANY, "RESTORE_SETTINGS");

    private UserProfile profile;

    public UserProfile getProfile() {
        return profile;
    }

    public LoadEvent(EventType<LoadEvent> eventType, UserProfile profile) {
        super(eventType);
        this.profile = profile;
    }

    @Override
    public String toString() {
        return "LoadEvent[type=" + getEventType() + "]";
    }
}
