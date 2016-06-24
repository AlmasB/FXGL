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
import java.util.*
import java.util.function.Consumer
import java.util.function.Function

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
abstract class SequentialIOTask<T> : IOTask<T>() {

    override fun onSuccess(action: Consumer<T>): SequentialIOTask<T> {
        super.onSuccess(action)
        return this
    }

    override fun onFailure(handler: ExceptionHandler): SequentialIOTask<T> {
        super.onFailure(handler)
        return this
    }

    fun <R> then(mapper: (T) -> IOTask<R>): SequentialIOTask<R> {

        return object : SequentialIOTask<R>() {
            override fun onExecute(): R {
                val result = this@SequentialIOTask.onExecute()

                return mapper.invoke(result).onExecute()
            }
        }
    }
}