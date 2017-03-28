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

package com.almasb.fxgl.core.logging;

import org.apache.logging.log4j.LogManager;

import java.util.function.Supplier;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class Log4j2 implements Logger {

    private org.apache.logging.log4j.Logger log;

    Log4j2(String name) {
        this.log = LogManager.getLogger(name);
    }

    @Override
    public void info(String message) {
        log.info(message);
    }

    @Override
    public void info(Supplier<String> messageSupplier) {
        log.info(messageSupplier.get());
    }

    @Override
    public void debug(String message) {
        log.debug(message);
    }

    @Override
    public void debug(Supplier<String> messageSupplier) {
        log.debug(messageSupplier.get());
    }

    @Override
    public void warning(String message) {
        log.warn(message);
    }

    @Override
    public void warning(Supplier<String> messageSupplier) {
        log.warn(messageSupplier.get());
    }

    @Override
    public void fatal(String message) {
        log.fatal(message);
    }

    @Override
    public void fatal(Supplier<String> messageSupplier) {
        log.fatal(messageSupplier.get());
    }

    @Override
    public void close() {
        LogManager.shutdown();
    }
}
