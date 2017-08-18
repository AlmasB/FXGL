/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core.logging

import com.almasb.fxgl.core.collection.Array
import java.time.LocalTime

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class Logger
private constructor(private val name: String) {

    companion object {

        private val outputs = Array<LoggerOutput>()

        private val debug = Array<LoggerOutput>()
        private val info = Array<LoggerOutput>()
        private val warning = Array<LoggerOutput>()
        private val fatal = Array<LoggerOutput>()

        private var config = LoggerConfig()
        private var configured = false
        private var closed = false

        @JvmStatic fun isConfigured(): Boolean = configured

        @JvmStatic fun configure(config: LoggerConfig) {
            if (configured)
                throw IllegalStateException("Logger already configured")

            this.config = config.copy()
            configured = true
        }

        @JvmStatic fun addOutput(loggerOutput: LoggerOutput, level: LoggerLevel) {
            outputs.add(loggerOutput)

            when(level) {
                LoggerLevel.DEBUG -> {
                    debug.add(loggerOutput)
                    info.add(loggerOutput)
                    warning.add(loggerOutput)
                    fatal.add(loggerOutput)
                }

                LoggerLevel.INFO -> {
                    info.add(loggerOutput)
                    warning.add(loggerOutput)
                    fatal.add(loggerOutput)
                }

                LoggerLevel.WARNING -> {
                    warning.add(loggerOutput)
                    fatal.add(loggerOutput)
                }

                LoggerLevel.FATAL -> {
                    fatal.add(loggerOutput)
                }
            }
        }

        private fun doLog(loggerName: String, loggerMessage: String, level: LoggerLevel) {
            val message = lazy { makeMessage(loggerName, loggerMessage, level) }

            when(level) {
                LoggerLevel.DEBUG -> {
                    debug.forEach { it.append(message.value) }
                }

                LoggerLevel.INFO -> {
                    info.forEach { it.append(message.value) }
                }

                LoggerLevel.WARNING -> {
                    warning.forEach { it.append(message.value) }
                }

                LoggerLevel.FATAL -> {
                    fatal.forEach { it.append(message.value) }
                }
            }
        }

        private fun makeMessage(loggerName: String, loggerMessage: String, level: LoggerLevel): String {
            val time = LocalTime.now().format(config.dateTimeFormatter)
            val threadName = Thread.currentThread().name
            return config.messageFormatter.makeMessage(time, threadName, "$level", loggerName, loggerMessage)
        }

        @JvmStatic fun get(name: String): Logger {
            return Logger(name)
        }

        @JvmStatic fun get(caller: Class<*>): Logger {
            return get(caller.simpleName)
        }

        @JvmStatic fun close() {
            if (closed)
                throw IllegalStateException("Logger already closed")

            outputs.forEach(LoggerOutput::close)
            closed = true
        }

        @JvmStatic fun errorTraceAsString(e: Throwable): String {
            val sb = StringBuilder()
            sb.append("\n\nException occurred: ")
                    .append(e.javaClass.canonicalName)
                    .append(" : ")
                    .append("${e.message}\n")

            val elements = e.stackTrace
            for (el in elements) {
                sb.append("E: ").append(el.toString()).append('\n')
            }

            return sb.toString()
        }
    }

    /**
     * Log an info level message.
     *
     * @param message the message
     */
    fun info(message: String) {
        doLog(name, message, LoggerLevel.INFO)
    }

    /**
     * Log an info level message with given format and arguments.
     *
     * @param format message format
     * @param args arguments
     */
    fun infof(format: String, vararg args: Any) {
        info(String.format(format, *args))
    }

    /**
     * Log a debug level message.
     *
     * @param message the message
     */
    fun debug(message: String) {
        doLog(name, message, LoggerLevel.DEBUG)
    }

    /**
     * Log a debug level message with given format and arguments.
     *
     * @param format message format
     * @param args arguments
     */
    fun debugf(format: String, vararg args: Any) {
        debug(String.format(format, *args))
    }

    /**
     * Log a warning level message.
     *
     * @param message the message
     */
    fun warning(message: String) {
        doLog(name, message, LoggerLevel.WARNING)
    }

    /**
     * Log a warning level message with given format and arguments.
     *
     * @param format message format
     * @param args arguments
     */
    fun warningf(format: String, vararg args: Any) {
        warning(String.format(format, *args))
    }

    /**
     * Log a fatal level message.
     *
     * @param message the message
     */
    fun fatal(message: String) {
        doLog(name, message, LoggerLevel.FATAL)
    }

    /**
     * Log a fatal level message with given format and arguments.
     *
     * @param format message format
     * @param args arguments
     */
    fun fatalf(format: String, vararg args: Any) {
        fatal(String.format(format, *args))
    }
}