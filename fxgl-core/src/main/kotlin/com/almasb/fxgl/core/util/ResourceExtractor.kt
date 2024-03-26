/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core.util

import com.almasb.fxgl.logging.Logger
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

/**
 * Extracts resources from the deployed jar to the local file system.
 *
 * @author Almas Baim (https://github.com/AlmasB)
 */
class ResourceExtractor {

    companion object {

        private val log = Logger.get(ResourceExtractor::class.java)

        /**
         * Extracts the file at jar [url] as a [relativeFilePath].
         * Note: the destination file will be overwritten.
         *
         * @return the url on the local file system of the extracted file
         */
        @JvmStatic fun extract(url: URL, relativeFilePath: String): URL {
            log.debug("Extracting $url as $relativeFilePath")

            val file = Paths.get(System.getProperty("user.home"))
                    .resolve(".openjfx")
                    .resolve("cache")
                    .resolve("fxgl-21")
                    .resolve(relativeFilePath)

            val fileParentDir = file.parent

            if (Files.notExists(fileParentDir)) {
                log.debug("Creating directories: $fileParentDir")

                Files.createDirectories(fileParentDir)
            }

            url.openStream().use {
                Files.copy(it, file, StandardCopyOption.REPLACE_EXISTING)
            }

            return file.toUri().toURL()
        }
    }
}