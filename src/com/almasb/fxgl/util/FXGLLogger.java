/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.almasb.fxgl.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Provides logging configuration of java.util.logging.Logger for the FXGL library
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 * @version 1.0
 *
 */
public final class FXGLLogger {

    private static Handler consoleHandler;

    /**
     * Initialize the logger with given level. This must be called
     * prior to any logging calls
     *
     * @param logLevel
     */
    public static void init(Level logLevel) {
        Formatter formatter = new Formatter() {
            @Override
            public String format(LogRecord record) {
                StringBuilder sb = new StringBuilder();

                Date date = new Date(record.getMillis());
                LocalDateTime dateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());

                sb.append(dateTime.format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm:ss.SSS")));
                sb.append(" ");
                sb.append(String.format("[%7s]", record.getLevel().toString()));
                sb.append(" ");

                sb.append(String.format("[%s]", record.getLoggerName()));
                sb.append(" ");

                sb.append(String.format("[%13s]", "Thread id: " + record.getThreadID()));
                sb.append(" ");

                sb.append(record.getMessage());
                sb.append("\n");

                return sb.toString();
            }
        };

        consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(logLevel);
        consoleHandler.setFormatter(formatter);
    }

    /**
     * Shuts down the logging tools
     */
    public static void close() {
        if (consoleHandler != null)
            consoleHandler.close();
    }

    /**
     * Wraps the error stack trace into a single String
     *
     * @param e
     * @return
     */
    public static String errorTraceAsString(Throwable e) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n\nException occurred: " + e.getClass().getCanonicalName() + " : " + e.getMessage());

        StackTraceElement[] elements = e.getStackTrace();
        for (StackTraceElement el : elements) {
            sb.append("E: " + el.toString() + "\n");
        }

        return sb.toString();
    }

    /**
     * <pre>
     * Example:
     *
     * private static final Logger log = FXGLLogger.getLogger("YOUR_CLASS_NAME");
     * </pre>
     *
     * @param name class name
     * @return logger object
     */
    public static Logger getLogger(String name) {
        Logger logger = Logger.getLogger(name);
        logger.setLevel(Level.ALL);
        logger.setUseParentHandlers(false);

        if (consoleHandler != null)
            logger.addHandler(consoleHandler);
        else
            System.out.println("No console logger. Was FXGLLogger.init() called?");

        return logger;
    }
}

