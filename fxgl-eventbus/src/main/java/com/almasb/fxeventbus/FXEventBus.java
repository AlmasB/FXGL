package com.almasb.fxeventbus;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Group;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public final class FXEventBus implements EventBus {

    private Group eventHandlers = new Group();

    @Override
    public <T extends Event> Subscriber addEventHandler(EventType<T> eventType, EventHandler<? super T> eventHandler) {
        eventHandlers.addEventHandler(eventType, eventHandler);
        return new Subscriber(this, eventType, (EventHandler<? super Event>) eventHandler);
    }

    @Override
    public <T extends Event> void removeEventHandler(EventType<T> eventType, EventHandler<? super T> eventHandler) {
        eventHandlers.removeEventHandler(eventType, eventHandler);
    }

    @Override
    public void fireEvent(Event event) {
        eventHandlers.fireEvent(event);
    }
}
