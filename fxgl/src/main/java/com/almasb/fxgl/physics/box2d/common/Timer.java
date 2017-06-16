/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package com.almasb.fxgl.physics.box2d.common;

/**
 * Timer for profiling
 *
 * @author Daniel
 */
public class Timer {

    private long resetNanos;

    public Timer() {
        reset();
    }

    public void reset() {
        resetNanos = System.nanoTime();
    }

    public float getMilliseconds() {
        return (System.nanoTime() - resetNanos) / 1000 * 1f / 1000;
    }
}
