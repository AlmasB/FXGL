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

import com.almasb.fxgl.logging.FXGLLogger
import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.Image
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.LocalDateTime
import java.util.*
import java.util.function.Predicate
import java.util.stream.Collectors
import javax.imageio.ImageIO

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class FS {

    companion object {

        private val log = FXGLLogger.getLogger("FXGL.FS")

        /**
         * Writes data to file, creating required directories.
         *
         * @param data data object to save
         * @param fileName to save as
         * @return io result
         */
        @JvmStatic fun writeData(data: Serializable, fileName: String): IOResult<*> {
            try {
                val file = Paths.get(fileName)

                if (!Files.exists(file.parent)) {
                    Files.createDirectories(file.parent)
                }

                ObjectOutputStream(Files.newOutputStream(file)).use { it.writeObject(data) }

                return IOResult.success<Any>()
            } catch (e: Exception) {
                log.warning { "Write Failed: ${e.message}" }
                return IOResult.failure<Any>(e.message ?: "No error message")
            }
        }

        /**
         * Loads data from file into an object.
         *
         * @param fileName file to load from
         *
         * @return IO result with the data object
         */
        @JvmStatic fun <T> readData(fileName: String): IOResult<T> {
            try {
                ObjectInputStream(Files.newInputStream(Paths.get(fileName)))
                        .use { return IOResult.success(it.readObject() as T) }
            } catch (e: Exception) {
                log.warning { "Read Failed: ${e.message}" }
                return IOResult.failure<T>(e.message ?: "No error message")
            }

        }

        @JvmStatic fun loadFileNames(dirName: String, recursive: Boolean): IOResult<List<String> > {
            try {
                val dir = Paths.get(dirName)

                if (!Files.exists(dir)) {
                    return IOResult.failure("Directory does not exist")
                }

                val fileNames = Files.walk(dir, if (recursive) Int.MAX_VALUE else 1)
                        .filter { Files.isRegularFile(it) }
                        .map { dir.relativize(it).toString().replace("\\", "/") }
                        .collect(Collectors.toList<String>())

                return IOResult.success<List<String>>(fileNames)
            } catch (e: Exception) {
                log.warning { "Error: ${e.message}" }
                return IOResult.failure(e.message ?: "No error message")
            }
        }

        @JvmStatic fun loadDirectoryNames(dirName: String, recursive: Boolean): IOResult<List<String> > {
            try {
                val dir = Paths.get(dirName)

                if (!Files.exists(dir)) {
                    return IOResult.failure("Directory does not exist")
                }

                val dirNames = Files.walk(dir, if (recursive) Int.MAX_VALUE else 1)
                        .filter { Files.isDirectory(it) }
                        .map { dir.relativize(it).toString().replace("\\", "/") }
                        .filter { it.isNotEmpty() }
                        .collect(Collectors.toList<String>())

                return IOResult.success<List<String>>(dirNames)
            } catch (e: Exception) {
                log.warning { "Error: ${e.message}" }
                return IOResult.failure(e.message ?: "No error message")
            }
        }

        @JvmStatic fun <T> loadLastModifiedFile(dirName: String, recursive: Boolean): IOResult<T> {
            try {
                val dir = Paths.get(dirName)

                if (!Files.exists(dir)) {
                    return IOResult.failure<T>("Directory does not exist")
                }

                val fileName = Files.walk(dir, if (recursive) Int.MAX_VALUE else 1)
                        .filter { Files.isRegularFile(it) }
                        .sorted { file1, file2 ->
                            Files.getLastModifiedTime(file2).compareTo(Files.getLastModifiedTime(file1))
                        }
                        .findFirst()
                        .map { dir.relativize(it).toString().replace("\\", "/") }
                        .orElseThrow { Exception() }

                return readData(dirName + fileName)
            } catch (e: Exception) {
                log.warning { "Load failed: ${e.message}" }
                return IOResult.failure<T>(e.message ?: "No error message")
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
                    return if (ok) IOResult.success<Any>() else IOResult.failure<Any>("Failed to write image")
                }
            } catch (e: Exception) {
                log.warning { "Write Failed: ${e.message}" }
                return IOResult.failure<Any>(e.message ?: "No error message")
            }
        }
    }
}