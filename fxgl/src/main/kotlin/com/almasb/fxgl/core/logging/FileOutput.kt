package com.almasb.fxgl.core.logging

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.stream.Collectors

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class FileOutput
@JvmOverloads constructor(private val baseFileName: String,
                          private val logDirectory: String = "logs/",
                          private val maxLogFiles: Int = 10) : LoggerOutput {

    private val data = arrayListOf<String>()

    init {
        cleanOldLogs()
    }

    override fun append(message: String) {
        data.add(message)
    }

    override fun close() {
        val stamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy-HH.mm.ss"))
        val file = Paths.get("${logDirectory}$baseFileName-$stamp.log")

        Files.write(file, data)
    }

    private fun cleanOldLogs() {
        val logDir = Paths.get(logDirectory)
        if (!Files.exists(logDir)) {
            Files.createDirectory(logDir)

            val readmeFile = Paths.get("${logDirectory}Readme.txt")

            Files.write(readmeFile, "This directory contains $baseFileName log files.".lines())
        }

        val logs = Files.walk(logDir, 1)
                .filter { Files.isRegularFile(it) }
                .sorted { file1, file2 -> Files.getLastModifiedTime(file1).compareTo(Files.getLastModifiedTime(file2)) }
                .collect(Collectors.toList<Path>())

        val logSize = logs.size
        if (logSize >= maxLogFiles) {
            for (i in 0..logSize + 1 - maxLogFiles - 1) {
                Files.delete(logs[i])
            }
        }
    }
}