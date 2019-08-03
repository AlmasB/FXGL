/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.io

import com.almasb.sslogger.Logger
import com.gluonhq.attach.storage.StorageService
import java.io.*

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
internal class FSServiceImpl(isDesktop: Boolean) : FSService {

    private val log = Logger.get(javaClass)

    private val rootStorage = if (isDesktop)
        File(System.getProperty("user.dir") + "/")
    else
        StorageService.create()
                .flatMap { it.privateStorage }
                .orElseThrow { RuntimeException("No private storage present") }

    override fun exists(pathName: String): Boolean {
        return toFile(pathName).exists()
    }

    override fun createDirectory(dirName: String) {
        val dir = toFile(dirName)
        dir.mkdirs()
    }

    override fun writeData(data: Serializable, fileName: String) {
        val file = toFile(fileName)

        if (file.parentFile != null && !file.parentFile.exists()) {
            log.debug("Creating directories to: ${file.parentFile}")
            file.parentFile.mkdirs()
        }

        ObjectOutputStream(FileOutputStream(file)).use {
            log.debug("Writing to: $file")
            it.writeObject(data)
        }
    }

    override fun writeData(text: List<String>, fileName: String) {
        val file = toFile(fileName)

        file.writeText(text.joinToString("\n"))
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> readData(fileName: String): T {
        val file = toFile(fileName)

        ObjectInputStream(FileInputStream(file)).use {
            log.debug("Reading from: $file")
            return it.readObject() as T
        }
    }

    override fun loadFileNames(dirName: String, recursive: Boolean): List<String> {
        val dir = toFile(dirName)

        checkExists(dir)

        return dir.walkTopDown()
                .maxDepth(if (recursive) Int.MAX_VALUE else 1)
                .filter { it.isFile }
                .map { it.relativeTo(dir).toString().replace("\\", "/") }
                .toList()
    }

    override fun loadFileNames(dirName: String, recursive: Boolean, extensions: List<FileExtension>): List<String> {
        val dir = toFile(dirName)

        checkExists(dir)

        return dir.walkTopDown()
                .maxDepth(if (recursive) Int.MAX_VALUE else 1)
                .filter { file -> file.isFile && extensions.filter { "$file".endsWith(it.extension) }.isNotEmpty() }
                .map { it.relativeTo(dir).toString().replace("\\", "/") }
                .toList()
    }

    override fun loadDirectoryNames(dirName: String, recursive: Boolean): List<String> {
        val dir = toFile(dirName)

        checkExists(dir)

        return dir.walkTopDown()
                .maxDepth(if (recursive) Int.MAX_VALUE else 1)
                .filter { it.isDirectory }
                .map { it.relativeTo(dir).toString().replace("\\", "/") }
                .filter { it.isNotEmpty() }
                .toList()
    }

    override fun loadLastModifiedFileName(dirName: String, recursive: Boolean): String {
        val dir = toFile(dirName)

        checkExists(dir)

        return dir.walkTopDown()
                .maxDepth(if (recursive) Int.MAX_VALUE else 1)
                .filter { it.isFile }
                .sortedByDescending { it.lastModified() }
                .map { it.relativeTo(dir).toString().replace("\\", "/") }
                .firstOrNull() ?: throw FileNotFoundException("No files found in $dir")
    }

    override fun deleteFile(fileName: String) {
        val file = toFile(fileName)

        checkExists(file)

        file.delete()
    }

    override fun deleteDirectory(dirName: String) {
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