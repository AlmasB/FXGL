/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.io

import java.io.Serializable

/**
 * All file names used here are full paths relative to root.
 * Example: ./profiles/ProfileName/save1.dat
 * On desktop it's the running dir, on mobile it's the private storage root.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
interface FSService {

    fun exists(pathName: String): Boolean

    fun writeData(data: Serializable, fileName: String)

    fun writeData(text: List<String>, fileName: String)

    fun <T> readData(fileName: String): T

    fun loadFileNames(dirName: String, recursive: Boolean): List<String>

    fun loadFileNames(dirName: String, recursive: Boolean, extensions: List<FileExtension>): List<String>

    fun loadDirectoryNames(dirName: String, recursive: Boolean): List<String>

    fun loadLastModifiedFileName(dirName: String, recursive: Boolean): String

    fun deleteFile(fileName: String)

    fun createDirectory(dirName: String)

    fun deleteDirectory(dirName: String)
}