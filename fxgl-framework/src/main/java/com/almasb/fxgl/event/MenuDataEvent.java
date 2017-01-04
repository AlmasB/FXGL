/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
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
import javafx.event.EventType;

/**
 * MenuEvent that carries necessary data.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public final class MenuDataEvent extends MenuEvent {

    /**
     * Common super-type for all menu data event types.
     */
    public static final EventType<MenuDataEvent> ANY =
            new EventType<>(MenuEvent.ANY, "MENU_DATA_EVENT");

    /**
     * When user clicks load.
     */
    public static final EventType<MenuDataEvent> LOAD =
            new EventType<>(ANY, "LOAD");

    /**
     * When user clicks delete.
     */
    public static final EventType<MenuDataEvent> DELETE =
            new EventType<>(ANY, "DELETE");

    /**
     * When profile has been selected.
     */
    //public static final EventType<MenuDataEvent> PROFILE_SELECTED = new EventType<>(ANY, "PROFILE_SELECTED");

    private final String data;

    public MenuDataEvent(EventType<? extends Event> eventType, String data) {
        super(eventType);
        this.data = data;

        if (this.data == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
    }

    /**
     * @return data associated with this event
     */
    public String getData() {
        return data;
    }

    @Override
    public String toString() {
        return "MenuEvent[type=" + getEventType() + ",data=" + data + "]";
    }
}
