/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app;

import com.almasb.fxgl.core.concurrent.Async;
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
     * Instantly starts a non-blocking async task.
     *
     * @param func the code to run
     * @param <T> return type of the code block
     * @return async object
     */
    <T> Async<T> async(Callable<T> func);

    /**
     * Instantly starts a non-blocking async task.
     *
     * @param func the code to run
     * @return async object
     */
    Async<Void> async(Runnable func);

    /**
     * Shuts down all background threads used by this executor.
     */
    void shutdownNow();
}
