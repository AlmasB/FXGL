/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core.concurrent;

import javafx.util.Duration;

import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledFuture;

/**
 * Asynchronous executor service.
 * Allows submitting tasks to be run in the background, including after a certain delay.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public interface Executor extends java.util.concurrent.Executor {

    /**
     * Schedule a single action to run after delay.
     * Unlike MasterTimer service, this is not blocked by game execution
     * and runs even if the game is paused.
     *
     * @param action the action
     * @param delay delay
     * @return scheduled future which can be cancelled
     */
    ScheduledFuture<?> schedule(Runnable action, Duration delay);

    /**
     * Starts an async task on a background thread.
     *
     * @param func function to run for result
     * @param <T> result type
     * @return an async object
     */
    <T> AsyncTask<T> startAsync(Callable<T> func);

    /**
     * Starts an async task on a background thread.
     *
     * @param func function to run
     * @return an async object
     */
    AsyncTask<?> startAsync(Runnable func);

    /**
     * Starts an async task on a JavaFX thread.
     *
     * @param func function to run for result
     * @param <T> result type
     * @return an async object
     */
    <T> AsyncTask<T> startAsyncFX(Callable<T> func);

    /**
     * Starts an async task on a JavaFX thread.
     *
     * @param func function to run
     * @return an async object
     */
    AsyncTask<?> startAsyncFX(Runnable func);

    /**
     * Shuts down all background threads used by this executor.
     */
    void shutdownNow();
}
