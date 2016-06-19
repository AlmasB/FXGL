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

import com.almasb.fxgl.util.ExceptionHandler
import java.util.function.Consumer

/**
 * Represents result of an IO based operation.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
class IOResult<T> {

    /**
     * @return true iff the IO operation succeeded
     */
    val isOK: Boolean

    /**
     * @return error that happened, NONE if OK
     */
    val error: Throwable

    /**
     * @return IO result data
     */
    val data: T?

    private constructor(ok: Boolean, error: Throwable) {
        this.isOK = ok
        this.error = error
        data = null
    }

    private constructor(data: T) {
        this.isOK = true
        this.error = NONE
        this.data = data
    }

    fun getErrorMessage() = error.message

    /**
     * @return true iff result has data associated with it
     */
    fun hasData() = data != null

    /**
     * The callback is executed immediately.
     */
    fun ifOK(consumer: Consumer<T>): IOResult<T> {
        if (isOK)
            consumer.accept(data)

        return this
    }

    /**
     * The callback is executed immediately.
     */
    fun ifError(handler: ExceptionHandler): IOResult<T> {
        if (!isOK)
            handler.handle(error)

        return this
    }

    companion object {

        private val NONE = Exception("NONE")

        /**
         * @return successful IO result
         */
        @JvmStatic fun <T> success(): IOResult<T> = IOResult(true, NONE)

        /**
         * @param data IO data
         *
         * @return successful IO result
         */
        @JvmStatic fun <T> success(data: T) = IOResult(data)

        /**
         * @param message error message
         *
         * @return failed IO result
         */
        @JvmStatic fun <T> failure(error: Throwable): IOResult<T> = IOResult(false, error)
    }
}