/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.io

import com.almasb.fxgl.logging.Logger
import java.io.*

/**
 * All file names used here are paths relative to root.
 * Example: ./profiles/ProfileName/save1.dat
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
internal class FileSystemAccess(

        /**
         * On desktop it's the running dir, on mobile it's the private storage root.
         */
        private val rootStorage: File) {

    private val log = Logger.get(javaClass)

    fun exists(pathName: String): Boolean {
        return toFile(pathName).exists()
    }

    fun createDirectory(dirName: String) {
        val dir = toFile(dirName)
        dir.mkdirs()
    }

    fun writeData(data: Serializable, fileName: String) {
        val file = toFile(fileName)

        // due to how toFile() constructs file, parentFile is always non-null
        file.parentFile.mkdirs()

        ObjectOutputStream(FileOutputStream(file)).use {
            log.debug("Writing to: $file")
            it.writeObject(data)
        }
    }

    fun writeData(text: List<String>, fileName: String) {
        val file = toFile(fileName)

        file.writeText(text.joinToString("\n"))
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> readData(fileName: String): T {
        val file = toFile(fileName)

        ObjectInputStream(FileInputStream(file)).use {
            log.debug("Reading from: $file")
            return it.readObject() as T
        }
    }

    fun loadFileNames(dirName: String, recursive: Boolean): List<String> {
        val dir = toFile(dirName)

        checkExists(dir)

        return dir.walkTopDown()
                .maxDepth(if (recursive) Int.MAX_VALUE else 1)
                .filter { it.isFile }
                .map { it.relativeTo(dir).toString().replace("\\", "/") }
                .toList()
    }

    fun loadFileNames(dirName: String, recursive: Boolean, extensions: List<FileExtension>): List<String> {
        val dir = toFile(dirName)

        checkExists(dir)

        return dir.walkTopDown()
                .maxDepth(if (recursive) Int.MAX_VALUE else 1)
                .filter { file -> file.isFile && extensions.filter { "$file".endsWith(it.extension) }.isNotEmpty() }
                .map { it.relativeTo(dir).toString().replace("\\", "/") }
                .toList()
    }

    fun loadDirectoryNames(dirName: String, recursive: Boolean): List<String> {
        val dir = toFile(dirName)

        checkExists(dir)

        return dir.walkTopDown()
                .maxDepth(if (recursive) Int.MAX_VALUE else 1)
                .filter { it.isDirectory }
                .map { it.relativeTo(dir).toString().replace("\\", "/") }
                .filter { it.isNotEmpty() }
                .toList()
    }

    fun loadLastModifiedFileName(dirName: String, recursive: Boolean): String {
        val dir = toFile(dirName)

        checkExists(dir)

        return dir.walkTopDown()
                .maxDepth(if (recursive) Int.MAX_VALUE else 1)
                .filter { it.isFile }
                .sortedByDescending { it.lastModified() }
                .map { it.relativeTo(dir).toString().replace("\\", "/") }
                .firstOrNull() ?: throw FileNotFoundException("No files found in $dir")
    }

    fun deleteFile(fileName: String) {
        val file = toFile(fileName)

        checkExists(file)

        file.delete()
    }

    fun deleteDirectory(dirName: String) {
        val file = toFile(dirName)

        checkExists(file)

        file.deleteRecursively()
    }

    private fun toFile(fileName: String): File {
        return File(rootStorage.absolutePath + File.separatorChar + fileName)
    }

    private fun checkExists(file: File) {
        if (!file.exists()) {
            throw FileNotFoundException("Path $file does not exist")
        }
    }
}