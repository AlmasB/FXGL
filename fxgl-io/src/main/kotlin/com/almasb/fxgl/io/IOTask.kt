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

package com.almasb.fxgl.io

import javafx.concurrent.Task
import org.apache.logging.log4j.LogManager
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
abstract class IOTask<T>(val name: String) {

    companion object {
        private val log = LogManager.getLogger(IOTask::class.java)

        /* Convenient way of creating small tasks */

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

    constructor() : this("NoName")

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
        log.warn("Task failed: $name Error: $error")
        if (onFailure == null) {
            FXGLIO.defaultExceptionHandler.accept(error)
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
    @JvmOverloads fun executeAsync(executor: Executor = FXGLIO.defaultExecutor) {
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
    @JvmOverloads fun executeAsyncWithDialog(executor: Executor = FXGLIO.defaultExecutor,
                                             dialog: UIDialogHandler = FXGLIO.defaultUIDialogSupplier.get()) {
        log.debug("Showing dialog")
        dialog.show()

        val task = object : IOTask<T>(name) {
            override fun onExecute(): T {
                log.debug("Executing task: $name")
                return this@IOTask.onExecute()
            }

            override fun succeed(value: T) {
                log.debug("succeed(): Dismissing dialog")
                dialog.dismiss()
                this@IOTask.succeed(value)
            }

            override fun fail(error: Throwable) {
                log.debug("fail(): Dismissing dialog")
                dialog.dismiss()
                this@IOTask.fail(error)
            }
        }

        task.executeAsync(executor)
    }

    /**
     * Executes this task asynchronously with default executor and shows dialog.
     * The dialog will be dismissed after task is completed, whether succeeded or failed.
     * Note: it is up to the caller to ensure that executor is actually async.
     * Unlike [executeAsyncWithDialog], this function hooks into the JavaFX concurrent model
     * with its Task as the primitive execution unit.
     * Hence, onSuccess and onFailure are executed from the JavaFX App thread.
     *
     * @param dialog dialog to use while task is being executed
     */
    fun executeAsyncWithDialogFX(dialog: UIDialogHandler) {
        executeAsyncWithDialogFX(FXGLIO.defaultExecutor, dialog)
    }

    /**
     * Executes this task asynchronously with given executor and shows dialog.
     * The dialog will be dismissed after task is completed, whether succeeded or failed.
     * Note: it is up to the caller to ensure that executor is actually async.
     * Unlike [executeAsyncWithDialog], this function hooks into the JavaFX concurrent model
     * with its Task as the primitive execution unit.
     * Hence, onSuccess and onFailure are executed from the JavaFX App thread.
     *
     * @param executor executor to use for async
     * @param dialog dialog to use while task is being executed
     */
    @JvmOverloads fun executeAsyncWithDialogFX(executor: Executor = FXGLIO.defaultExecutor,
                                               dialog: UIDialogHandler = FXGLIO.defaultUIDialogSupplier.get()) {
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