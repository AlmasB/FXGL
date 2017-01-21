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

package com.almasb.fxgl.logging

import java.util.function.Supplier

/**
 * Used for junit tests.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
object MockLoggerFactory : com.almasb.fxgl.logging.LoggerFactory() {

    object MockLogger : com.almasb.fxgl.logging.Logger {
        override fun info(message: String?) {

        }

        override fun info(messageSupplier: Supplier<String>?) {

        }

        override fun debug(message: String?) {

        }

        override fun debug(messageSupplier: Supplier<String>?) {

        }

        override fun warning(message: String?) {

        }

        override fun warning(messageSupplier: Supplier<String>?) {

        }

        override fun fatal(message: String?) {

        }

        override fun fatal(messageSupplier: Supplier<String>?) {

        }

        override fun close() {

        }
    }

    override fun newLogger(caller: Class<*>?): com.almasb.fxgl.logging.Logger? {
        return MockLogger
    }

    override fun newLogger(name: String?): com.almasb.fxgl.logging.Logger? {
        return MockLogger
    }
}