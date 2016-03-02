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
 * This is an FXGL system event.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class FXGLEvent extends Event {

    public static final EventType<FXGLEvent> ANY =
            new EventType<>(Event.ANY, "FXGL_EVENT");

    public static final EventType<FXGLEvent> INIT_APP_COMPLETE =
            new EventType<>(ANY, "INIT_APP_COMPLETE");

    public static final EventType<FXGLEvent> PAUSE =
            new EventType<>(ANY, "PAUSE");

    public static final EventType<FXGLEvent> RESUME =
            new EventType<>(ANY, "RESUME");

    public static final EventType<FXGLEvent> RESET =
            new EventType<>(ANY, "RESET");

    public static final EventType<FXGLEvent> EXIT =
            new EventType<>(ANY, "EXIT");

    public FXGLEvent(@NamedArg("eventType") EventType<? extends Event> eventType) {
        super(eventType);
    }

    public static FXGLEvent initAppComplete() {
        return new FXGLEvent(INIT_APP_COMPLETE);
    }

    public static FXGLEvent pause() {
        return new FXGLEvent(PAUSE);
    }

    public static FXGLEvent resume() {
        return new FXGLEvent(RESUME);
    }

    public static FXGLEvent reset() {
        return new FXGLEvent(RESET);
    }

    public static FXGLEvent exit() {
        return new FXGLEvent(EXIT);
    }

    @Override
    public String toString() {
        return "FXGLEvent[type=" + getEventType().toString() + "]";
    }
}
