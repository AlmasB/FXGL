/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.io

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.core.logging.Logger
import javafx.concurrent.Task
import java.util.concurrent.Callable
import java.util.concurrent.Executor
import java.util.function.Consumer

/**
 * IO Task that wraps some IO or any other operation
 * that may potentially fail.
 *
 * @param T type of the result of this task
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
abstract class IOTask<T>
@JvmOverloads constructor(val name: String = "NoName") {

    companion object {
        private val log = Logger.get(IOTask::class.java)

        /* Convenient way of creating small tasks in Java */

        @JvmStatic fun <R> of(func: Callable<R>) = of("NoName", func)

        @JvmStatic fun <R> of(taskName: String, func: Callable<R>) = object : IOTask<R>(taskName) {
            override fun onExecute(): R {
                return func.call()
            }
        }

        @JvmStatic fun ofVoid(func: Runnable) = ofVoid("NoName", func)

        @JvmStatic fun ofVoid(taskName: String, func: Runnable) = object : IOTask<Void?>(taskName) {
            override fun onExecute(): Void? {
                func.run()
                return null
            }
        }
    }

    private var onSuccess: ((T) -> Unit)? = null
    private var onFailure: ((Throwable) -> Unit)? = null

    @Throws(Exception::class)
    protected abstract fun onExecute(): T

    /**
     * Set consumer action for success scenario.
     * Note: the consumer will be invoked on the same thread
     * that invoked execute(), so be careful if your code
     * needs to be run on a particular thread.
     * See [executeAsyncWithDialogFX] for JavaFX.
     *
     * @param action action to call if the task succeeds
     */
    fun onSuccess(action: Consumer<T>): IOTask<T> {
        onSuccess = { action.accept(it) }
        return this
    }

    fun onSuccessKt(action: (T) -> Unit): IOTask<T> {
        onSuccess = action
        return this
    }

    /**
     * Set error consumer for fail scenario.
     * Note: the consumer will be invoked on the same thread
     * that invoked execute(), so be careful if your code
     * needs to be run on a particular thread.
     * See [executeAsyncWithDialogFX] for JavaFX.
     *
     * @param handler exception handler to call if the task fails
     */
    fun onFailure(handler: Consumer<Throwable>): IOTask<T> {
        onFailure = { handler.accept(it) }
        return this
    }

    fun onFailureKt(handler: (Throwable) -> Unit): IOTask<T> {
        onFailure = handler
        return this
    }

    open protected fun succeed(value: T) {
        log.debug("Task succeeded: $name")
        onSuccess?.invoke(value)
    }

    open protected fun fail(error: Throwable) {
        log.warning("Task failed: $name Error: $error")
        if (onFailure == null) {
            // https://github.com/AlmasB/FXGL/issues/480
            FXGL.getExceptionHandler().handle(error)
        } else {
            onFailure!!.invoke(error)
        }
    }

    /**
     * Allows chaining IO tasks to be executed sequentially.
     *
     * @param mapper function that takes result of previous task and returns new task
     * @return IO task
     */
    fun <R> then(mapper: (T) -> IOTask<R>) = taskOf(name, {

        log.debug("Executing task: $name")

        val result = this@IOTask.onExecute()

        val otherTask = mapper.invoke(result)

        log.debug("Executing task: ${otherTask.name}")

        return@taskOf otherTask.onExecute()
    })

    /**
     * Executes this task synchronously.
     * Note: "Void" tasks will also return null.
     *
     * @return value IO task resulted in, or null if failed
     */
    fun execute(): T? {
        log.debug("Executing task: $name")

        try {
            val value = onExecute()
            succeed(value)
            return value
        } catch (e: Exception) {
            fail(e)
            return null
        }
    }

    /**
     * Executes this task asynchronously with given executor.
     * Note: it is up to the caller to ensure that executor is actually async.
     *
     * @param executor executor to use for async
     */
    @JvmOverloads fun executeAsync(executor: Executor = FXGL.getExecutor()) {
        executor.execute({ execute() })
    }

    /**
     * Executes this task asynchronously with given executor and shows dialog.
     * The dialog will be dismissed after task is completed, whether succeeded or failed.
     * Note: it is up to the caller to ensure that executor is actually async.
     *
     * @param executor executor to use for async
     * @param dialog dialog to use while task is being executed
     */
    @JvmOverloads fun executeAsyncWithDialogFX(dialog: UIDialogHandler = NONE, executor: Executor = FXGL.getExecutor()) {
        log.debug("Showing dialog")
        dialog.show()

        val task = object : Task<T>() {
            override fun call(): T {
                log.debug("Executing task: $name")
                return onExecute()
            }

            override fun succeeded() {
                log.debug("succeed(): Dismissing dialog")
                dialog.dismiss()
                succeed(value)
            }

            override fun failed() {
                log.debug("fail(): Dismissing dialog")
                dialog.dismiss()
                fail(exception)
            }
        }

        executor.execute(task)
    }
}

fun <R> taskOf(func: () -> R) = taskOf("NoName", func)

fun <R> taskOf(taskName: String, func: () -> R): IOTask<R> = object : IOTask<R>(taskName) {
    override fun onExecute(): R {
        return func()
    }
}

fun <R> voidTaskOf(func: () -> R) = voidTaskOf("NoName", func)

fun <R> voidTaskOf(taskName: String, func: () -> R) = object : IOTask<Void?>(taskName) {
    override fun onExecute(): Void? {
        func()
        return null
    }
}