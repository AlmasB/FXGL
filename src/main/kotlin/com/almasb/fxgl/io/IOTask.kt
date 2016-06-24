/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2016 AlmasB (almaslvl@gmail.com)
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

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.util.ExceptionHandler
import javafx.concurrent.Task
import java.util.function.Consumer

/**
 * IO Task that wraps some IO or any other operation
 * that may potentially fail.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
abstract class IOTask<T> {

    companion object {
        private val log = FXGL.getLogger(IOTask::class.java)
    }

    private var onSuccess: Consumer<T>? = null
    private var onFailure: ExceptionHandler? = null

    @Throws(Exception::class)
    protected abstract fun onExecute(): T

    open fun onSuccess(action: Consumer<T>): IOTask<T> {
        onSuccess = action
        return this
    }

    open fun onFailure(handler: ExceptionHandler): IOTask<T> {
        onFailure = handler
        return this
    }

    protected fun succeed(value: T) {
        onSuccess?.accept(value)
    }

    protected fun fail(error: Throwable) {
        log.warning("IOTask failed: $error")
        onFailure?.handle(error)
    }

    fun execute(): T? {
        try {
            val value = onExecute()
            onSuccess?.accept(value)
            return value
        } catch (e: Exception) {
            onFailure?.handle(e)
            return null
        }
    }

    fun executeAsync() {
        val task = object : Task<T>() {
            override fun call(): T {
                return onExecute()
            }

            override fun succeeded() {
                succeed(value)
            }

            override fun failed() {
                fail(exception)
            }
        }

        FXGL.getExecutor().submit(task)
    }

    fun executeAsyncWithProgressDialog(message: String) {
        val handler = FXGL.getDisplay().showProgressBox(message)

        val task = object : Task<T>() {
            override fun call(): T {
                return onExecute()
            }

            override fun succeeded() {
                handler.dismiss()
                succeed(value)
            }

            override fun failed() {
                handler.dismiss()
                fail(exception)
            }
        }

        FXGL.getExecutor().submit(task)
    }
}