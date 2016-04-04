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

package com.almasb.fxgl.logging;

import java.util.function.Supplier;

/**
 * Logger service.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public interface Logger {

    /**
     * Log an info level message.
     *
     * @param message the message
     */
    void info(String message);

    /**
     * Log an info level message supplied by object.
     *
     * @param messageSupplier the message supplier
     */
    void info(Supplier<String> messageSupplier);

    /**
     * Log a debug level message.
     *
     * @param message the message
     */
    void debug(String message);

    /**
     * Log a debug level message supplied by object.
     *
     * @param messageSupplier the message supplier
     */
    void debug(Supplier<String> messageSupplier);

    /**
     * Log a warning level message.
     *
     * @param message the message
     */
    void warning(String message);

    /**
     * Log a warning level message supplied by object.
     *
     * @param messageSupplier the message supplier
     */
    void warning(Supplier<String> messageSupplier);

    /**
     * Log a fatal level message.
     *
     * @param message the message
     */
    void fatal(String message);

    /**
     * Log a fatal level message supplied by object.
     *
     * @param messageSupplier the message supplier
     */
    void fatal(Supplier<String> messageSupplier);

    /**
     * Close the logger.
     */
    void close();
}
