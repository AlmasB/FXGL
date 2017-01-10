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

package com.almasb.fxgl.eventbus;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;

/**
 * An event dispatcher that can be used for subscribing to events and posting the events.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public interface EventBus {
    /**
     * Register event handler for event type.
     *
     * @param eventType type
     * @param eventHandler handler
     * @param <T> event
     */
    <T extends Event> Subscriber addEventHandler(EventType<T> eventType,
                                                 EventHandler<? super T> eventHandler);

    /**
     * Remove event handler for event type.
     *
     * @param eventType type
     * @param eventHandler handler
     * @param <T> event
     */
    <T extends Event> void removeEventHandler(EventType<T> eventType,
                                              EventHandler<? super T> eventHandler);

    /**
     * Post (fire) given event. All listening parties will be notified.
     * Events will be handled on the same thread that fired the event,
     * i.e. synchronous.
     *
     * <p>
     *     Note: according to JavaFX doc this must be called on JavaFX Application Thread.
     *     In reality this doesn't seem to be true.
     * </p>
     *
     * @param event the event
     */
    void fireEvent(Event event);
}
