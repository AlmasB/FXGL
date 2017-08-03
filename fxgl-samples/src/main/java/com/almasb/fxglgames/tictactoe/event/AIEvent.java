/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxglgames.tictactoe.event;

import javafx.event.Event;
import javafx.event.EventType;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class AIEvent extends Event {

    public static final EventType<AIEvent> ANY = new EventType<>(Event.ANY, "AI_EVENT");
    public static final EventType<AIEvent> WAITING = new EventType<>(ANY, "AI_EVENT_WAITING");
    public static final EventType<AIEvent> MOVED = new EventType<>(ANY, "AI_EVENT_MOVED");

    public AIEvent(EventType<? extends Event> eventType) {
        super(eventType);
    }
}
