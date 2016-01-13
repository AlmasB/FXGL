/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.almasb.fxgl.event;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;

import java.util.Optional;

/**
 * An event related to menus. This event can only occur if menu is enabled.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public final class MenuEvent extends Event {

    /**
     * Common super-type for all menu event types.
     */
    public static final EventType<MenuEvent> ANY =
            new EventType<>(Event.ANY, "MENU_EVENT");

    /**
     * This event occurs when the user hit menu key in the game
     * to open game menu.
     */
    public static final EventType<MenuEvent> PAUSE =
            new EventType<>(MenuEvent.ANY, "PAUSE");

    public static final EventType<MenuEvent> RESUME =
            new EventType<>(MenuEvent.ANY, "RESUME");

    public static final EventType<MenuEvent> NEW_GAME =
            new EventType<>(MenuEvent.ANY, "NEW_GAME");

    public static final EventType<MenuEvent> SAVE =
            new EventType<>(MenuEvent.ANY, "SAVE");

    public static final EventType<MenuEvent> LOAD =
            new EventType<>(MenuEvent.ANY, "LOAD");

    public static final EventType<MenuEvent> DELETE =
            new EventType<>(MenuEvent.ANY, "DELETE");

    public static final EventType<MenuEvent> EXIT =
            new EventType<>(MenuEvent.ANY, "EXIT");

    public static final EventType<MenuEvent> EXIT_TO_MAIN_MENU =
            new EventType<>(MenuEvent.ANY, "EXIT_TO_MAIN_MENU");

    public static final EventType<MenuEvent> PROFILE_SELECTED =
            new EventType<>(MenuEvent.ANY, "PROFILE_SELECTED");

    public MenuEvent(EventType<? extends Event> eventType) {
        this(eventType, null);
    }

    public MenuEvent(EventType<? extends Event> eventType, Object data) {
        super(eventType);
        this.data = Optional.ofNullable(data);
    }

    private final Optional<Object> data;

    /**
     * @return data associated with the event or empty Optional if no data is present
     */
    public Optional<Object> getData() {
        return data;
    }

    @Override
    public String toString() {
        return "MenuEvent[type=" + getEventType() + "]";
    }
}
