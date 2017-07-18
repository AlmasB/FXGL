/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxglgames.spaceinvaders.event;

import javafx.event.Event;
import javafx.event.EventType;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class GameEvent extends Event {

    public static final EventType<GameEvent> ANY =
            new EventType<>(Event.ANY, "GAME_EVENT");

    public static final EventType<GameEvent> PLAYER_GOT_HIT =
            new EventType<>(ANY, "PLAYER_GOT_HIT");

    public static final EventType<GameEvent> ENEMY_KILLED =
            new EventType<>(ANY, "ENEMY_KILLED");

    public static final EventType<GameEvent> ENEMY_REACHED_END =
            new EventType<>(ANY, "ENEMY_REACHED_END");

    public GameEvent(EventType<? extends Event> eventType) {
        super(eventType);
    }
}
