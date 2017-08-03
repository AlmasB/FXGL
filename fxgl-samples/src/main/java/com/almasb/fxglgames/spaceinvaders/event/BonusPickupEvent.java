/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxglgames.spaceinvaders.event;

import com.almasb.fxglgames.spaceinvaders.BonusType;
import javafx.beans.NamedArg;
import javafx.event.Event;
import javafx.event.EventType;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class BonusPickupEvent extends GameEvent {

    public static final EventType<BonusPickupEvent> ANY =
            new EventType<>(GameEvent.ANY, "BONUS_EVENT");

    private BonusType type;

    public BonusPickupEvent(@NamedArg("eventType") EventType<? extends Event> eventType, BonusType type) {
        super(eventType);
        this.type = type;
    }

    public BonusType getType() {
        return type;
    }
}
