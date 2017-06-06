/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.event;

import javafx.beans.NamedArg;
import javafx.event.Event;
import javafx.event.EventType;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class TestEvent extends Event {

    public static final EventType<TestEvent> ANY = new EventType<>(Event.ANY, "TEST_EVENT");

    private static final EventType<TestEvent> HIDDEN = new EventType<>(ANY, "HIDDEN_EVENT");

    public static final int FAIL0 = 0;

    public final int FAIL1 = 1;

    private Object data;

    public TestEvent(@NamedArg("eventType") EventType<? extends Event> eventType, Object data) {
        super(eventType);
        this.data = data;
    }

    public Object getData() {
        return data;
    }
}
