package com.almasb.fxgl;

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

public final class FXGLLogger {

    private static Handler consoleHandler;

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

    public static void close() {
        if (consoleHandler != null)
            consoleHandler.close();
    }

    public static String errorTraceAsString(Throwable e) {
        StringBuilder sb = new StringBuilder();
        sb.append("Exception occurred: " + e.getClass().getCanonicalName() + " : " + e.getMessage());

        StackTraceElement[] elements = e.getStackTrace();
        for (StackTraceElement el : elements) {
            sb.append("E: " + el.toString());
        }

        return sb.toString();
    }

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

