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

package com.almasb.fxgl.time;

import javafx.event.Event;
import javafx.event.EventType;

/**
 * This is a game update tick event.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public final class UpdateEvent extends Event {
    public static final EventType<UpdateEvent> ANY =
            new EventType<>(Event.ANY, "UPDATE_EVENT");

    private long tick;

    public void setTick(long tick) {
        this.tick = tick;
    }

    /**
     * @return current tick
     */
    public long tick() {
        return tick;
    }

    private double tpf;

    public void setTPF(double tpf) {
        this.tpf = tpf;
    }

    /**
     * @return time per frame
     */
    public double tpf() {
        return tpf;
    }

    /**
     * Constructs update event.
     *
     * @param tpf time per last frame
     */
    public UpdateEvent(long tick, double tpf) {
        super(ANY);
        this.tick = tick;
        this.tpf = tpf;
    }

    @Override
    public String toString() {
        return "UpdateEvent{" +
                "tick=" + tick +
                ", tpf=" + tpf +
                '}';
    }
}
