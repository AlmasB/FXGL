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

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.logging.*;

/**
 * Provides logging configuration of java.util.logging.Logger for the FXGL library.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public final class FXGLLogger {

    private static Handler consoleHandler, fileHandler;

    private static Logger log;

    /**
     * Initialize the logger with given level. This must be called
     * prior to any logging calls
     *
     * @param logLevel logging level
     */
    public static void init(Level logLevel) {
        if (consoleHandler != null) {
            consoleHandler.setLevel(logLevel);
            log.info("Logger level set to: " + logLevel);
            return;
        }

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

        try {
            Path logDir = Paths.get("logs/");
            if (!Files.exists(logDir)) {
                Files.createDirectory(logDir);
            }

            fileHandler = new FileHandler("logs/FXGL-" + LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("dd MMM yyyy HH-mm-ss-SSS")) + ".log",
                    1024 * 1024, 1);
            fileHandler.setLevel(Level.ALL);
            fileHandler.setFormatter(formatter);
        } catch (Exception ignored) {}

        consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(logLevel);
        consoleHandler.setFormatter(formatter);

        log = getLogger("FXGL.Logger");
        log.info("Logger initialized with level: " + logLevel);
        logSystemInfo();
    }

    /**
     * Shuts down the logging tools
     */
    public static void close() {
        if (log != null)
            log.finer("Logger is closing");

        if (fileHandler != null)
            fileHandler.close();
        if (consoleHandler != null)
            consoleHandler.close();
    }

    /**
     * Wraps the error stack trace into a single String
     *
     * @param e error
     * @return stack trace as String
     */
    public static String errorTraceAsString(Throwable e) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n\nException occurred: ")
                .append(e.getClass().getCanonicalName())
                .append(" : ").append(e.getMessage());

        StackTraceElement[] elements = e.getStackTrace();
        for (StackTraceElement el : elements) {
            sb.append("E: ").append(el.toString()).append('\n');
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

        if (fileHandler != null)
            logger.addHandler(fileHandler);
        else
            System.out.println("No file logger. Was FXGLLogger.init() called?");

        return logger;
    }

    /**
     * Logs various details about runtime environment into a file.
     */
    private static void logSystemInfo() {
        Runtime rt = Runtime.getRuntime();

        double MB = 1024 * 1024;

        log.finer("CPU cores: " + rt.availableProcessors());
        log.finer(String.format("Free Memory: %.0fMB", rt.freeMemory() / MB));
        log.finer(String.format("Max Memory: %.0fMB", rt.maxMemory() / MB));
        log.finer(String.format("Total Memory: %.0fMB", rt.totalMemory() / MB));

        log.finer("System Properties:");
        System.getProperties().forEach((k, v) -> log.finer(k + "=" + v));
    }
}

