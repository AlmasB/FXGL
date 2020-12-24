/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.logging

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.stream.Collectors

/**
 * Stores all incoming messages in memory, then saves the messages to a file on [close].
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class FileOutput
@JvmOverloads constructor(

        /**
         * Base log file name without extension.
         */
        private val baseFileName: String,

        /**
         * The directory in which the log is to be saved.
         */
        private val logDirectory: String = "logs/",

        /**
         * Maximum number of log files to keep in the given directory.
         */
        private val maxLogFiles: Int = 10) : LoggerOutput {

    private val data = arrayListOf<String>()

    init {
        val logDir = getOrCreateLogDir()

        cleanOldLogs(logDir)
    }

    override fun append(message: String) {
        data.add(message)
    }

    override fun close() {
        val stamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy-HH.mm.ss"))
        val file = Paths.get("$logDirectory$baseFileName-$stamp.log")

        Files.write(file, data)
    }

    private fun getOrCreateLogDir(): Path {
        val logDir = Paths.get(logDirectory)
        if (!Files.exists(logDir)) {
            Files.createDirectory(logDir)
        }

        return logDir
    }

    private fun cleanOldLogs(logDir: Path) {
        val logs = Files.walk(logDir, 1)
                .filter { Files.isRegularFile(it) }
                .sorted { file1, file2 -> Files.getLastModifiedTime(file1).compareTo(Files.getLastModifiedTime(file2)) }
                .collect(Collectors.toList<Path>())

        val logSize = logs.size
        if (logSize >= maxLogFiles) {
            for (i in 0 until logSize + 1 - maxLogFiles) {
                Files.delete(logs[i])
            }
        }
    }
}