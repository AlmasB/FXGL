/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.logging

import java.io.PrintWriter
import java.io.StringWriter
import java.time.LocalTime

/**
 * A flexible logger that can be obtained by calling [Logger.get].
 * The above call is safe to be made even before calling [Logger.configure].
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class Logger
private constructor(private val name: String) {

    companion object {

        private val outputs = ArrayList<LoggerOutput>()

        private val debug = ArrayList<LoggerOutput>()
        private val info = ArrayList<LoggerOutput>()
        private val warning = ArrayList<LoggerOutput>()
        private val fatal = ArrayList<LoggerOutput>()

        private var config = LoggerConfig()
        private var isConfigured = false
        private var isClosed = false

        @JvmStatic fun isConfigured(): Boolean = isConfigured

        /**
         * Configures all loggers obtained by [get] using the given [config].
         * Can only be configured once.
         * Subsequent calls will generate a warning log and are no-op.
         */
        @JvmStatic fun configure(config: LoggerConfig) {
            if (isConfigured) {
                doLog("Logger", "Logger already configured", LoggerLevel.WARN)
                return
            }

            this.config = config.copy()
            isConfigured = true

            doLog("Logger", "Configured Logger", LoggerLevel.DEBUG)
        }

        /**
         * Add [loggerOutput] for a given [level].
         * Inclusivity level order is: DEBUG, INFO, WARN, FATAL.
         * For example, an output added for WARN will also log FATAL messages, whereas
         * an output added for DEBUG will include all other levels.
         */
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

                LoggerLevel.WARN -> {
                    warning.add(loggerOutput)
                    fatal.add(loggerOutput)
                }

                LoggerLevel.FATAL -> {
                    fatal.add(loggerOutput)
                }
            }
        }

        @JvmStatic fun removeOutput(loggerOutput: LoggerOutput, level: LoggerLevel) {
            outputs.remove(loggerOutput)

            when(level) {
                LoggerLevel.DEBUG -> {
                    debug.remove(loggerOutput)
                    info.remove(loggerOutput)
                    warning.remove(loggerOutput)
                    fatal.remove(loggerOutput)
                }

                LoggerLevel.INFO -> {
                    info.remove(loggerOutput)
                    warning.remove(loggerOutput)
                    fatal.remove(loggerOutput)
                }

                LoggerLevel.WARN -> {
                    warning.remove(loggerOutput)
                    fatal.remove(loggerOutput)
                }

                LoggerLevel.FATAL -> {
                    fatal.remove(loggerOutput)
                }
            }
        }

        @JvmStatic fun removeAllOutputs() {
            outputs.clear()

            debug.clear()
            info.clear()
            warning.clear()
            fatal.clear()
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

                LoggerLevel.WARN -> {
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

        inline fun <reified T> get() = get(T::class.java)
        @JvmStatic fun get(caller: Class<*>): Logger {
            return get(caller.simpleName)
        }

        @JvmStatic fun close() {
            if (isClosed) {
                doLog("Logger", "Logger already closed", LoggerLevel.WARN)
                return
            }

            doLog("Logger", "Closing Logger", LoggerLevel.DEBUG)

            outputs.forEach(LoggerOutput::close)
            isClosed = true
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
        doLog(name, message, LoggerLevel.WARN)
    }

    /**
     * Log a warning level message.
     */
    fun warning(message: String, error: Throwable) {
        val trace = error.stackTraceToString()

        warning("$message\n$trace")
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
     * Log a fatal level message.
     */
    fun fatal(message: String, error: Throwable) {
        val trace = error.stackTraceToString()

        fatal("$message\n$trace")
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

/**
 * Collates the stack trace of this error to String.
 */
fun Throwable.stackTraceToString(): String {
    val sw = StringWriter()
    val pw = PrintWriter(sw)
    this.printStackTrace(pw)
    pw.close()

    return sw.toString()
}