/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core.concurrent;

import com.almasb.sslogger.Logger;
import javafx.concurrent.Task;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * IO Task that wraps some IO or any other operation
 * that may potentially fail.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public abstract class IOTask<T> {

    private static final Logger log = Logger.get(IOTask.class);

    private static final String DEFAULT_NAME = "NoName";

    private final String name;

    private Consumer<T> successAction = (result) -> {};
    private Consumer<Throwable> failAction = (e) -> { log.warning(getName() + " failed", e); };

    private boolean hasFailAction = false;

    public IOTask() {
        this(DEFAULT_NAME);
    }

    public IOTask(String name) {
        this.name = name;
    }

    public final String getName() {
        return name;
    }

    public final boolean isHasFailAction() {
        return hasFailAction;
    }

    /**
     * Set consumer action for success scenario.
     *
     * @param successAction action to call if the task succeeds
     */
    public final IOTask<T> onSuccess(Consumer<T> successAction) {
        this.successAction = successAction;
        return this;
    }

    /**
     * Set error consumer for fail scenario.
     *
     * @param failAction exception handler to call if the task fails
     */
    public final IOTask<T> onFailure(Consumer<Throwable> failAction) {
        hasFailAction = true;
        this.failAction = failAction;
        return this;
    }

    protected abstract T onExecute() throws Exception;

    /**
     * Executes this task synchronously on this thread.
     * All callbacks will be called on this thread.
     */
    public final T run() {
        try {
            T value = onExecute();
            succeed(value);
            return value;
        } catch (Exception e) {
            fail(e);
            return null;
        }
    }

    /**
     * Allows chaining IO tasks to be executed sequentially.
     *
     * @param mapper function that takes result of previous task and returns new task
     * @return IO task
     */
    public final <R> IOTask<R> then(Function<T, IOTask<R>> mapper) {
        return of(name, () -> mapper.apply(onExecute()).onExecute());
    }

    public final <R> IOTask<R> thenWrap(Function<T, R> mapper) {
        return then(t -> of(() -> mapper.apply(t)));
    }

    public final Task<T> toJavaFXTask() {
        return new Task<T>() {
            @Override
            protected T call() throws Exception {
                return onExecute();
            }

            @Override
            protected void succeeded() {
                succeed(getValue());
            }

            @Override
            protected void failed() {
                fail(getException());
            }
        };
    }

    private void succeed(T result) {
        successAction.accept(result);
    }

    private void fail(Throwable error) {
        failAction.accept(error);
    }

    /* Static convenience methods */

    public static IOTask<Void> ofVoid(Runnable action) {
        return ofVoid(DEFAULT_NAME, action);
    }

    public static IOTask<Void> ofVoid(String name, Runnable action) {
        return of(name, () -> {
            action.run();
            return null;
        });
    }

    public static <R> IOTask<R> of(Callable<R> action) {
        return of(DEFAULT_NAME, action);
    }

    public static <R> IOTask<R> of(String name, Callable<R> action) {
        return new IOTask<R>(name) {
            @Override
            protected R onExecute() throws Exception {
                return action.call();
            }
        };
    }
}