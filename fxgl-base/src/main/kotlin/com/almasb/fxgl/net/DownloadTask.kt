/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.net

import com.almasb.fxgl.core.concurrent.IOTask
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class DownloadTask(private val url: String) : IOTask<Path>() {

    override fun onExecute(): Path {
        URL(url).openStream().use {
            val fileName = url.substringAfterLast("/")

            val file = Paths.get("./$fileName")
            Files.copy(it, file, StandardCopyOption.REPLACE_EXISTING)
            return file
        }
    }
}