/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
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

package com.almasb.fxgl.io

import org.apache.logging.log4j.LogManager
import java.io.*
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import java.util.stream.Collectors

/**
 * A collection of static methods to access IO via IO tasks.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class FS {

    companion object {

        private val log = LogManager.getLogger(FS::class.java)

        private fun errorIfAbsent(path: Path) {
            if (!Files.exists(path)) {
                log.warn ( "Path $path does not exist" )
                throw FileNotFoundException("Path $path does not exist")
            }
        }

        /**
         * Writes data to file, creating required directories.
         *
         * @param data data object to save
         * @param fileName to save as
         * @return IO task
         */
        @JvmStatic fun writeDataTask(data: Serializable, fileName: String) = voidTaskOf("writeDataTask($fileName)", {

            val file = Paths.get(fileName)

            // if file.parent is null we will use current dir, which exists
            if (file.parent != null && !Files.exists(file.parent)) {
                log.debug ( "Creating directories to: ${file.parent}" )
                Files.createDirectories(file.parent)
            }

            ObjectOutputStream(Files.newOutputStream(file)).use {
                log.debug ( "Writing to: $file" )
                it.writeObject(data)
            }
        })

        /**
         * Loads data from file into an object.
         *
         * @param fileName file to load from
         *
         * @return IO task
         */
        @Suppress("UNCHECKED_CAST")
        @JvmStatic fun <T> readDataTask(fileName: String) = taskOf("readDataTask($fileName)") {

            val file = Paths.get(fileName)

            errorIfAbsent(file)

            ObjectInputStream(Files.newInputStream(file)).use {
                log.debug ( "Reading from: $file" )
                return@taskOf it.readObject() as T
            }
        }

        /**
         * Loads file names from given directory.
         * Searches subdirectories if recursive flag is on.
         *
         * @param dirName directory name
         * @param recursive recursive flag
         * @return IO task
         */
        @JvmStatic fun loadFileNamesTask(dirName: String, recursive: Boolean) = taskOf("loadFileNamesTask($dirName, $recursive)") {

            val dir = Paths.get(dirName)

            errorIfAbsent(dir)

            return@taskOf Files.walk(dir, if (recursive) Int.MAX_VALUE else 1)
                    .filter { Files.isRegularFile(it) }
                    .map { dir.relativize(it).toString().replace("\\", "/") }
                    .collect(Collectors.toList<String>())
        }

        /**
         * Loads file names from given directory.
         * Searches subdirectories if recursive flag is on.
         * Only names from extensions list will be reported.
         *
         * @param dirName directory name
         * @param recursive recursive flag
         * @param extensions file extensions to include
         * @return IO task
         */
        @JvmStatic fun loadFileNamesTask(dirName: String, recursive: Boolean, extensions: List<FileExtension>) = taskOf("loadFileNamesTask($dirName, $recursive, $extensions)") {

            val dir = Paths.get(dirName)

            errorIfAbsent(dir)

            return@taskOf Files.walk(dir, if (recursive) Int.MAX_VALUE else 1)
                    .filter { file -> Files.isRegularFile(file) && extensions.filter { "$file".endsWith(it.extension) }.isNotEmpty() }
                    .map { dir.relativize(it).toString().replace("\\", "/") }
                    .collect(Collectors.toList<String>())
        }

        /**
         * Loads directory names from [dirName].
         * Searches subdirectories if [recursive].
         *
         * @param dirName directory name
         * @param recursive recursive flag
         * @return IO task
         */
        @JvmStatic fun loadDirectoryNamesTask(dirName: String, recursive: Boolean) = taskOf("loadDirectoryNamesTask($dirName, $recursive)", {
            val dir = Paths.get(dirName)

            errorIfAbsent(dir)

            return@taskOf Files.walk(dir, if (recursive) Int.MAX_VALUE else 1)
                    .filter { Files.isDirectory(it) }
                    .map { dir.relativize(it).toString().replace("\\", "/") }
                    .filter { it.isNotEmpty() }
                    .collect(Collectors.toList<String>())
        })

        /**
         * Loads (deserializes) last modified file from given [dirName] directory.
         * Searches subdirectories if [recursive].
         *
         * @param dirName directory name
         * @param recursive recursive flag
         * @return IO task
         */
        @JvmStatic fun <T> loadLastModifiedFileTask(dirName: String, recursive: Boolean) = taskOf("loadLastModifiedFileTask($dirName, $recursive)") {

            val dir = Paths.get(dirName)

            errorIfAbsent(dir)

            return@taskOf Files.walk(dir, if (recursive) Int.MAX_VALUE else 1)
                    .filter { Files.isRegularFile(it) }
                    .sorted { file1, file2 ->
                        Files.getLastModifiedTime(file2).compareTo(Files.getLastModifiedTime(file1))
                    }
                    .findFirst()
                    .map { dir.relativize(it).toString().replace("\\", "/") }
                    .orElseThrow { FileNotFoundException("No files found in $dir") }

        }.then { fileName -> readDataTask<T>(dirName + fileName) }

        /**
         * Delete file [fileName].
         *
         * @param fileName name of file to delete
         * @return IO task
         */
        @JvmStatic fun deleteFileTask(fileName: String) = voidTaskOf("deleteFileTask($fileName)") {

            val file = Paths.get(fileName)

            errorIfAbsent(file)

            log.debug ( "Deleting file: $file" )

            Files.delete(file)
        }

        /**
         * Delete directory [dirName] and its contents.
         *
         * @param dirName directory name to delete
         * @return IO task
         */
        @JvmStatic fun deleteDirectoryTask(dirName: String) = voidTaskOf("deleteDirectoryTask($dirName)") {

            val dir = Paths.get(dirName)

            errorIfAbsent(dir)

            Files.walkFileTree(dir, object : SimpleFileVisitor<Path>() {
                override fun visitFile(file: Path, p1: BasicFileAttributes): FileVisitResult {
                    log.debug ( "Deleting file: $file" )

                    Files.delete(file)
                    return FileVisitResult.CONTINUE
                }

                override fun postVisitDirectory(dir: Path, e: IOException?): FileVisitResult {
                    if (e == null) {
                        log.debug ( "Deleting directory: $dir" )

                        Files.delete(dir)
                        return FileVisitResult.CONTINUE
                    } else {
                        throw e
                    }
                }
            })
        }
    }
}