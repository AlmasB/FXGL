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

package com.almasb.fxgl.io

import com.almasb.fxgl.app.FXGL
import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.Image
import java.io.*
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import java.util.stream.Collectors
import javax.imageio.ImageIO

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class FS {

    companion object {

        private val log = FXGL.getLogger("FXGLFileSystem")

        /**
         * Writes data to file, creating required directories.
         *
         * @param data data object to save
         * @param fileName to save as
         * @return io task
         */
        @JvmStatic fun writeDataTask(data: Serializable, fileName: String): IOTask<Void?> {
            return object : IOTask<Void?>() {
                override fun onExecute(): Void? {

                    val file = Paths.get(fileName)

                    if (!Files.exists(file.parent)) {
                        Files.createDirectories(file.parent)
                    }

                    ObjectOutputStream(Files.newOutputStream(file)).use { it.writeObject(data) }

                    return null
                }
            }
        }

        /**
         * Loads data from file into an object.
         *
         * @param fileName file to loadTask from
         *
         * @return IO result with the data object
         */
        @JvmStatic fun <T> readDataTask(fileName: String): IOTask<T> {
            return object : IOTask<T>() {
                @Suppress("UNCHECKED_CAST")
                override fun onExecute(): T {

                    ObjectInputStream(Files.newInputStream(Paths.get(fileName)))
                            .use { return it.readObject() as T }
                }
            }
        }

        @JvmStatic fun loadFileNamesTask(dirName: String, recursive: Boolean): IOTask<List<String> > {
            return object : IOTask<List<String> >() {
                override fun onExecute(): List<String> {

                    val dir = Paths.get(dirName)

                    if (!Files.exists(dir)) {
                        throw FileNotFoundException("Directory $dir does not exist")
                    }

                    return Files.walk(dir, if (recursive) Int.MAX_VALUE else 1)
                            .filter { Files.isRegularFile(it) }
                            .map { dir.relativize(it).toString().replace("\\", "/") }
                            .collect(Collectors.toList<String>())
                }
            }
        }

        @JvmStatic fun loadFileNamesTask(dirName: String, recursive: Boolean, extensions: List<FileExtension>): IOTask<List<String> > {
            return object : IOTask<List<String> >() {
                override fun onExecute(): List<String> {

                    val dir = Paths.get(dirName)

                    if (!Files.exists(dir)) {
                        throw FileNotFoundException("Directory $dir does not exist")
                    }

                    return Files.walk(dir, if (recursive) Int.MAX_VALUE else 1)
                            .filter { file -> Files.isRegularFile(file) && extensions.filter { "$file".endsWith(it.extension) }.isNotEmpty() }
                            .map { dir.relativize(it).toString().replace("\\", "/") }
                            .collect(Collectors.toList<String>())
                }
            }
        }

        /**
         * Loads directory names from [dirName].
         * Searches subdirectories if [recursive].
         *
         * @return io task
         */
        @JvmStatic fun loadDirectoryNamesTask(dirName: String, recursive: Boolean): IOTask<List<String> > {
            return object : IOTask<List<String> >() {
                override fun onExecute(): List<String> {

                    val dir = Paths.get(dirName)

                    if (!Files.exists(dir)) {
                        throw FileNotFoundException("Directory does not exist")
                    }

                    val dirNames = Files.walk(dir, if (recursive) Int.MAX_VALUE else 1)
                            .filter { Files.isDirectory(it) }
                            .map { dir.relativize(it).toString().replace("\\", "/") }
                            .filter { it.isNotEmpty() }
                            .collect(Collectors.toList<String>())

                    return dirNames
                }
            }
        }

        /**
         * Loads (deserializes) last modified file from given [dirName] directory.
         * Searches subdirectories if [recursive].
         *
         * @return io task
         */
        @JvmStatic fun <T> loadLastModifiedFileTask(dirName: String, recursive: Boolean): IOTask<T> {
            return object : IOTask<String>() {
                override fun onExecute(): String {

                    val dir = Paths.get(dirName)

                    if (!Files.exists(dir)) {
                        throw FileNotFoundException("Directory $dirName does not exist")
                    }

                    val fileName = Files.walk(dir, if (recursive) Int.MAX_VALUE else 1)
                            .filter { Files.isRegularFile(it) }
                            .sorted { file1, file2 ->
                                Files.getLastModifiedTime(file2).compareTo(Files.getLastModifiedTime(file1))
                            }
                            .findFirst()
                            .map { dir.relativize(it).toString().replace("\\", "/") }
                            .orElseThrow { Exception("No files found") }

                    return fileName
                }
            }
            .then { fileName -> readDataTask<T>(dirName + fileName) }
        }

        /**
         * Delete file [fileName].
         *
         * @return io task
         */
        @JvmStatic fun deleteFileTask(fileName: String): IOTask<Void?> {
            log.debug { "Deleting file: $fileName" }

            return object : IOTask<Void?>() {
                override fun onExecute(): Void? {

                    val file = Paths.get(fileName)

                    if (!Files.exists(file)) {
                        throw FileNotFoundException("File $file does not exist")
                    }

                    Files.delete(file)

                    return null
                }
            }
        }

        /**
         * Delete directory [dirName] and its contents.
         *
         * @return io task
         */
        @JvmStatic fun deleteDirectoryTask(dirName: String): IOTask<Void?> {
            return object : IOTask<Void?>() {
                override fun onExecute(): Void? {
                    log.debug { "Deleting directory: $dirName" }

                    val dir = Paths.get(dirName)

                    if (!Files.exists(dir)) {
                        throw FileNotFoundException("Directory $dirName does not exist")
                    }

                    Files.walkFileTree(dir, object : SimpleFileVisitor<Path>() {
                        override fun visitFile(file: Path, p1: BasicFileAttributes): FileVisitResult {
                            Files.delete(file)
                            return FileVisitResult.CONTINUE
                        }

                        override fun postVisitDirectory(dir: Path, e: IOException?): FileVisitResult {
                            if (e == null) {
                                Files.delete(dir)
                                return FileVisitResult.CONTINUE
                            } else {
                                throw e
                            }
                        }
                    })

                    return null
                }
            }
        }

        /**
         * Writes [fxImage] to [fileName].
         * Extension ".png" will be appended to [fileName].
         *
         * @return io result of operation
         */
        @JvmStatic fun writeFxImagePNG(fxImage: Image, fileName: String): IOResult<*> {
            val img = SwingFXUtils.fromFXImage(fxImage, null)

            try {
                Files.newOutputStream(Paths.get(fileName + ".png")).use {
                    val ok = ImageIO.write(img, "png", it)
                    return if (ok) IOResult.success<Any>() else IOResult.failure<Any>(IOException("Failed to write image"))
                }
            } catch (e: Exception) {
                log.warning { "Write Failed: ${e.message}" }
                return IOResult.failure<Any>(e)
            }
        }
    }
}