/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core.logging;

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
     * Log an info level message with given format and arguments.
     *
     * @param format message format
     * @param args arguments
     */
    default void infof(String format, Object... args) {
        info(String.format(format, args));
    }

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
     * Log a debug level message with given format and arguments.
     *
     * @param format message format
     * @param args arguments
     */
    default void debugf(String format, Object... args) {
        debug(String.format(format, args));
    }

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
     * Log a warning level message with given format and arguments.
     *
     * @param format message format
     * @param args arguments
     */
    default void warningf(String format, Object... args) {
        warning(String.format(format, args));
    }

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
     * Log a fatal level message with given format and arguments.
     *
     * @param format message format
     * @param args arguments
     */
    default void fatalf(String format, Object... args) {
        fatal(String.format(format, args));
    }

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
