package com.almasb.fxeventbus;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public final class Subscriber {

    private EventBus bus;

    private EventType<? extends Event> eventType;
    private EventHandler<? super Event> eventHandler;

    Subscriber(EventBus bus, EventType<? extends Event> eventType, EventHandler<? super Event> eventHandler) {
        this.bus = bus;
        this.eventType = eventType;
        this.eventHandler = eventHandler;
    }

    /**
     * Stop listening for events.
     */
    public void unsubscribe() {
        bus.removeEventHandler(eventType, eventHandler);
    }
}
