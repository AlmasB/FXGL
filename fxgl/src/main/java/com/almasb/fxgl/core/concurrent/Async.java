/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core.concurrent;

import java.util.concurrent.Callable;

/**
 * Easy way of invoking async tasks.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public abstract class Async<T> {

    /**
     * Starts an async task in a background thread.
     *
     * @param func function to run for result
     * @param <T> result type
     * @return an async object
     */
    public static <T> Async<T> start(Callable<T> func) {
        return new Coroutine<T>(func);
    }

    /**
     * Starts an async task in a background thread.
     *
     * @param func function to run
     * @return an async object
     */
    public static Async<Void> start(Runnable func) {
        return start(() -> {
            func.run();
            return null;
        });
    }

    /**
     * Runs the task on the JavaFX UI Thread.
     *
     * @param func function to run for result
     * @param <T> result type
     * @return async object
     */
    public static <T> Async<T> startFX(Callable<T> func) {
        return new FXCoroutine<T>(func);
    }

    /**
     * Runs the task on the JavaFX UI Thread.
     *
     * @param func function to run
     * @return async object
     */
    public static Async<Void> startFX(Runnable func) {
        return startFX(() -> {
            func.run();
            return null;
        });
    }

    /**
     * Blocks current thread to wait for the result of
     * this async task.
     *
     * @return task result
     */
    public abstract T await();
}
