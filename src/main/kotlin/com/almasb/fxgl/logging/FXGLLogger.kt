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

package com.almasb.fxgl.logging

import org.apache.logging.log4j.LogManager
import java.util.function.Supplier

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class FXGLLogger(val caller: Class<*>) : Logger {

    private val log = LogManager.getLogger(caller)

    override fun info(message: String?) = log.info(message)

    override fun info(messageSupplier: Supplier<String>?) = log.info(messageSupplier?.get())

    override fun debug(message: String?) = log.debug(message)

    override fun debug(messageSupplier: Supplier<String>?) = log.debug(messageSupplier?.get())

    override fun warning(message: String?) = log.warn(message)

    override fun warning(messageSupplier: Supplier<String>?) = log.warn(messageSupplier?.get())

    override fun fatal(message: String?) = log.fatal(message)

    override fun fatal(messageSupplier: Supplier<String>?) = log.fatal(messageSupplier?.get())

    override fun close() {

    }
}