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

package com.almasb.fxgl.logging

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.function.Supplier
import java.util.logging.*
import java.util.logging.Formatter
import java.util.stream.Collectors

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
object SystemLogger : Logger {

    private val MAX_LOGS = 10

    private val threadNames = HashMap<Long, String>()

    private val consoleHandler = ConsoleHandler()
    private val fileHandler: FileHandler

    private val logger = java.util.logging.Logger.getLogger("FXGL.SystemLogger")

    init {
        cleanOldLogs()

        fileHandler = FileHandler("logs/FXGL-${LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy HH-mm-ss-SSS"))}.log",
                1024 * 1024, 1)

        initLogger()

        logSystemInfo()
    }

    private fun cleanOldLogs() {
        val logDir = Paths.get("logs/")
        if (!Files.exists(logDir)) {
            Files.createDirectory(logDir)
        }

        val logs = Files.walk(logDir, 1)
                .filter { Files.isRegularFile(it) }
                .sorted { file1, file2 -> Files.getLastModifiedTime(file1).compareTo(Files.getLastModifiedTime(file2)) }
                .collect(Collectors.toList<Path>())

        val logSize = logs.size
        if (logSize >= MAX_LOGS) {
            for (i in 0..logSize + 1 - MAX_LOGS - 1) {
                Files.delete(logs[i])
            }
        }
    }

    private fun initLogger() {
        val formatter = initFormatter()

        consoleHandler.level = Level.CONFIG
        consoleHandler.formatter = formatter

        fileHandler.level = Level.ALL
        fileHandler.formatter = formatter

        logger.level = Level.ALL
        logger.setUseParentHandlers(false)
        logger.addHandler(consoleHandler)
        logger.addHandler(fileHandler)

        logger.info("System Logger is initialized")
    }

    private fun initFormatter(): Formatter {
        return object : Formatter() {
            override fun format(record: LogRecord): String {
                val sb = StringBuilder()

                val date = Date(record.millis)
                val dateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault())

                sb.append(dateTime.format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm:ss.SSS")))
                sb.append(" ")
                sb.append(String.format("[%7s]", record.level.toString()))
                sb.append(" ")

                sb.append(String.format("[%20s]", record.loggerName))
                sb.append(" ")

                sb.append(String.format("[%15s]", getThreadName(record.threadID)))
                sb.append(" ")

                sb.append(record.message)
                sb.append("\n")

                return sb.toString()
            }
        }
    }

    override fun info(message: String?) = logger.info { message }

    override fun info(messageSupplier: Supplier<String>?) = logger.info(messageSupplier?.get())

    override fun debug(message: String?) = logger.finer { message }

    override fun debug(messageSupplier: Supplier<String>?) = logger.finer(messageSupplier?.get())

    override fun warning(message: String?) = logger.warning { message }

    override fun warning(messageSupplier: Supplier<String>?) = logger.warning(messageSupplier?.get())

    override fun fatal(message: String?) = logger.severe { message }

    override fun fatal(messageSupplier: Supplier<String>?) = logger.severe(messageSupplier?.get())

    /**
     * Shuts down the logging tools
     */
    override fun close() {
        logger.finer("Logger is closing")

        fileHandler.close()
        consoleHandler.close()
    }

    /**
     * Wraps the error stack trace into a single String

     * @param e error
     * *
     * @return stack trace as String
     */
    fun errorTraceAsString(e: Throwable): String {
        val sb = StringBuilder()
        sb.append("\n\nException occurred: ").append(e.javaClass.canonicalName).append(" : ").append(e.message)

        val elements = e.stackTrace
        for (el in elements) {
            sb.append("E: ").append(el.toString()).append('\n')
        }

        return sb.toString()
    }

    /**
     * Logs various details about runtime environment into a file.
     */
    private fun logSystemInfo() {
        logger.finer("Logging System Info")

        val rt = Runtime.getRuntime()

        val MB = (1024 * 1024).toDouble()

        logger.finer("CPU cores: " + rt.availableProcessors())
        logger.finer(String.format("Free Memory: %.0fMB", rt.freeMemory() / MB))
        logger.finer(String.format("Max Memory: %.0fMB", rt.maxMemory() / MB))
        logger.finer(String.format("Total Memory: %.0fMB", rt.totalMemory() / MB))

        logger.finer("System Properties:")
        System.getProperties().forEach { k, v -> logger.finer { "$k=$v" } }
    }

    private fun pollThreadNames() {
        val threads = arrayOfNulls<Thread>(Thread.activeCount())
        Thread.enumerate(threads)

        for (t in threads) {
            threadNames.put(t!!.id, t.name)
        }
    }

    private fun getThreadName(id: Int): String {
        val name = (threadNames).getOrDefault(id.toLong(), "")
        if (name.isEmpty())
            pollThreadNames()

        return threadNames.getOrDefault(id.toLong(), "Unknown")
    }
}