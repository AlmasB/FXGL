/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
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
