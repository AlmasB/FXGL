/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2016 AlmasB (almaslvl@gmail.com)
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

import javafx.beans.NamedArg;
import javafx.event.Event;
import javafx.event.EventType;

/**
 * Event related to display.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class DisplayEvent extends Event {

    /**
     * Common super-type for all display event types.
     */
    public static final EventType<DisplayEvent> ANY =
            new EventType<>(Event.ANY, "DISPLAY_EVENT");

    /**
     * Fired when user requests application close.
     */
    public static final EventType<DisplayEvent> CLOSE_REQUEST =
            new EventType<>(ANY, "CLOSE_REQUEST");

    /**
     * Fired when a dialog has opened.
     */
    public static final EventType<DisplayEvent> DIALOG_OPENED =
            new EventType<>(ANY, "DIALOG_OPENED");

    /**
     * Fired when a dialog has closed.
     */
    public static final EventType<DisplayEvent> DIALOG_CLOSED =
            new EventType<>(ANY, "DIALOG_CLOSED");

    public DisplayEvent(@NamedArg("eventType") EventType<? extends Event> eventType) {
        super(eventType);
    }

    @Override
    public String toString() {
        return "DisplayEvent[type=" + getEventType() + "]";
    }
}
