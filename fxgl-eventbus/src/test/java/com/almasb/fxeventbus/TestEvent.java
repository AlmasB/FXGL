package com.almasb.fxeventbus;

import javafx.beans.NamedArg;
import javafx.event.Event;
import javafx.event.EventType;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class TestEvent extends Event {

    public static final EventType<TestEvent> ANY =
            new EventType<>(Event.ANY, "TEST_EVENT");

    private Object data;

    public TestEvent(@NamedArg("eventType") EventType<? extends Event> eventType, Object data) {
        super(eventType);
        this.data = data;
    }

    public Object getData() {
        return data;
    }
}
