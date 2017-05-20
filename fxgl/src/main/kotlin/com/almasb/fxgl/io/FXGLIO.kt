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

import org.apache.logging.log4j.LogManager
import java.util.concurrent.Executor
import java.util.function.Consumer
import java.util.function.Supplier

/**
 * Instance of FXGLIO configuration.
 * Allows to set defaults to simplify IOTask construction and usage.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
object FXGLIO {

    private val log = LogManager.getLogger(FXGLIO::class.java)

    /**
     * Default exception handler that simply logs the exception
     */
    var defaultExceptionHandler = Consumer<Throwable> { log.warn("Exception occurred: $it") }

    /**
     * Default executor that uses the same thread as the caller.
     */
    var defaultExecutor = Executor { it.run() }

    /**
     * Default UI dialog supplier that does nothing.
     */
    var defaultUIDialogSupplier = Supplier<UIDialogHandler> { object : UIDialogHandler {

            override fun dismiss() {
                // no-op
            }

            override fun show() {
                // no-op
            }
        }
    }
}