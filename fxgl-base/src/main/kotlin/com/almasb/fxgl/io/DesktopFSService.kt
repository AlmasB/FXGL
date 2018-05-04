/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.io

import com.almasb.fxgl.core.logging.Logger
import java.io.*
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import java.util.stream.Collectors

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
internal class DesktopFSService : FSService {

    private val log = Logger.get(javaClass)

    override fun exists(pathName: String): Boolean {
        return Files.exists(Paths.get(pathName))
    }

    override fun createDirectory(dirName: String) {
        val dir = Paths.get(dirName)

        Files.createDirectories(dir)
    }

    override fun writeData(data: Serializable, fileName: String) {
        val file = Paths.get(fileName)

        // if file.parent is null we will use current dir, which exists
        if (file.parent != null && !Files.exists(file.parent)) {
            log.debug("Creating directories to: ${file.parent}")
            Files.createDirectories(file.parent)
        }

        ObjectOutputStream(Files.newOutputStream(file)).use {
            log.debug("Writing to: $file")
            it.writeObject(data)
        }
    }

    override fun writeData(text: List<String>, fileName: String) {
        val file = Paths.get(fileName)

        Files.write(file, text)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> readData(fileName: String): T {
        val file = Paths.get(fileName)

        checkExists(file)

        ObjectInputStream(Files.newInputStream(file)).use {
            log.debug("Reading from: $file")
            return it.readObject() as T
        }
    }

    override fun loadFileNames(dirName: String, recursive: Boolean): List<String> {
        val dir = Paths.get(dirName)

        checkExists(dir)

        return Files.walk(dir, if (recursive) Int.MAX_VALUE else 1)
                .filter { Files.isRegularFile(it) }
                .map { dir.relativize(it).toString().replace("\\", "/") }
                .collect(Collectors.toList<String>())
    }

    override fun loadFileNames(dirName: String, recursive: Boolean, extensions: List<FileExtension>): List<String> {
        val dir = Paths.get(dirName)

        checkExists(dir)

        return Files.walk(dir, if (recursive) Int.MAX_VALUE else 1)
                .filter { file -> Files.isRegularFile(file) && extensions.filter { "$file".endsWith(it.extension) }.isNotEmpty() }
                .map { dir.relativize(it).toString().replace("\\", "/") }
                .collect(Collectors.toList<String>())
    }

    override fun loadDirectoryNames(dirName: String, recursive: Boolean): List<String> {
        val dir = Paths.get(dirName)

        checkExists(dir)

        return Files.walk(dir, if (recursive) Int.MAX_VALUE else 1)
                .filter { Files.isDirectory(it) }
                .map { dir.relativize(it).toString().replace("\\", "/") }
                .filter { it.isNotEmpty() }
                .collect(Collectors.toList<String>())
    }

    override fun loadLastModifiedFileName(dirName: String, recursive: Boolean): String {
        val dir = Paths.get(dirName)

        checkExists(dir)

        return Files.walk(dir, if (recursive) Int.MAX_VALUE else 1)
                .filter { Files.isRegularFile(it) }
                .sorted { file1, file2 ->
                    Files.getLastModifiedTime(file2).compareTo(Files.getLastModifiedTime(file1))
                }
                .findFirst()
                .map { dir.relativize(it).toString().replace("\\", "/") }
                .orElseThrow { FileNotFoundException("No files found in $dir") }
    }

    override fun deleteFile(fileName: String) {
        val file = Paths.get(fileName)

        checkExists(file)

        log.debug("Deleting file: $file")

        Files.delete(file)
    }

    override fun deleteDirectory(dirName: String) {
        val dir = Paths.get(dirName)

        checkExists(dir)

        Files.walkFileTree(dir, object : SimpleFileVisitor<Path>() {
            override fun visitFile(file: Path, p1: BasicFileAttributes): FileVisitResult {
                log.debug("Deleting file: $file")

                Files.delete(file)
                return FileVisitResult.CONTINUE
            }

            override fun postVisitDirectory(directory: Path, e: IOException?): FileVisitResult {
                if (e == null) {
                    log.debug("Deleting directory: $directory")

                    Files.delete(directory)
                    return FileVisitResult.CONTINUE
                } else {
                    throw e
                }
            }
        })
    }

    private fun checkExists(path: Path) {
        if (!Files.exists(path)) {
            throw FileNotFoundException("Path $path does not exist")
        }
    }
}