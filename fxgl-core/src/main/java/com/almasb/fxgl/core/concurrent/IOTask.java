/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core.concurrent;

import com.almasb.fxgl.core.util.Consumer;
import com.almasb.fxgl.core.util.Function;
import javafx.concurrent.Task;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

/**
 * IO Task that wraps some IO or any other operation
 * that may potentially fail.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public abstract class IOTask<T> {

    private static final String DEFAULT_NAME = "NoName";

    private static Executor defaultExecutor = Runnable::run;
    private static Consumer<Throwable> defaultFailAction = System.out::println;

    public static void setDefaultExecutor(Executor defaultExecutor) {
        IOTask.defaultExecutor = defaultExecutor;
    }

    public static void setDefaultFailAction(Consumer<Throwable> defaultFailAction) {
        IOTask.defaultFailAction = defaultFailAction;
    }

    private static final UIDialogHandler DUMMY_DIALOG = new UIDialogHandler() {
        @Override
        public void show() { }

        @Override
        public void dismiss() { }
    };

    private Consumer<T> successAction = (result) -> {};
    private Consumer<Throwable> failAction = defaultFailAction;

    private String name;

    public IOTask() {
        this(DEFAULT_NAME);
    }

    public IOTask(String name) {
        this.name = name;
    }

    public final String getName() {
        return name;
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
     * Executes this task asynchronously with default executor.
     * Note: it is up to the caller to ensure that executor is actually async.
     * All callbacks will be called on the executor thread.
     */
    public final void runAsync() {
        runAsync(defaultExecutor);
    }

    /**
     * Executes this task asynchronously with given executor.
     * Note: it is up to the caller to ensure that executor is actually async.
     * All callbacks will be called on the executor thread.
     *
     * @param executor executor to use for async
     */
    public final void runAsync(Executor executor) {
        executor.execute(this::run);
    }

    /**
     * Executes this task asynchronously with default executor.
     * Note: it is up to the caller to ensure that executor is actually async.
     * All callbacks will be called on the JavaFX thread.
     */
    public final void runAsyncFX() {
        runAsyncFX(defaultExecutor);
    }

    /**
     * Executes this task asynchronously with default executor.
     * Note: it is up to the caller to ensure that executor is actually async.
     * All callbacks will be called on the JavaFX thread.
     *
     * @param executor executor to use for async
     */
    public final void runAsyncFX(Executor executor) {
        runAsyncFXWithDialog(executor, DUMMY_DIALOG);
    }

    /**
     * Executes this task asynchronously with default executor and shows dialog.
     * The dialog will be dismissed after task is completed, whether succeeded or failed.
     * Note: it is up to the caller to ensure that executor is actually async.
     * All callbacks will be called on the JavaFX thread.
     *
     * @param dialog dialog to use while task is being executed
     */
    public final void runAsyncFXWithDialog(UIDialogHandler dialog) {
        runAsyncFXWithDialog(defaultExecutor, dialog);
    }

    /**
     * Executes this task asynchronously with given executor and shows dialog.
     * The dialog will be dismissed after task is completed, whether succeeded or failed.
     * Note: it is up to the caller to ensure that executor is actually async.
     * All callbacks will be called on the JavaFX thread.
     *
     * @param executor executor to use for async
     * @param dialog dialog to use while task is being executed
     */
    public final void runAsyncFXWithDialog(Executor executor, UIDialogHandler dialog) {
        dialog.show();

        Task<T> task = new Task<T>() {
            @Override
            protected T call() throws Exception {
                return onExecute();
            }

            @Override
            protected void succeeded() {
                dialog.dismiss();
                succeed(getValue());
            }

            @Override
            protected void failed() {
                dialog.dismiss();
                fail(getException());
            }
        };

        executor.execute(task);
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
        this.failAction = failAction;
        return this;
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

    public interface UIDialogHandler {

        void show();

        void dismiss();
    }
}
