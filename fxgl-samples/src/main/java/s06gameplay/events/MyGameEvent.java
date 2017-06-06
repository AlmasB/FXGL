/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s06gameplay.events;

import javafx.event.Event;
import javafx.event.EventType;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class MyGameEvent extends Event {

    public static final EventType<MyGameEvent> ANY = new EventType<>(Event.ANY, "MY_GAME_EVENT");

    public MyGameEvent(EventType<? extends Event> eventType) {
        super(eventType);
    }
}
