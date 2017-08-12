package com.almasb.fxgl.core.logging

import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class FileOutput(private val baseFileName: String) : LoggerOutput {

    private val data = arrayListOf<String>()

    override fun append(message: String) {
        data.add(message)
    }

    override fun close() {
        val stamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy-HH.mm.ss"))
        val file = Paths.get("logs/$baseFileName-$stamp")

        Files.write(file, data)
    }
}