/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.io

import com.almasb.fxgl.core.concurrent.IOTask
import com.almasb.sslogger.Logger
import java.io.File
import java.io.Serializable

/**
 * A wrapper abstraction around the file system access.
 * Enables access of IO via IO tasks.
 * All file names used here are full paths relative to root.
 * Example: ./profiles/ProfileName/save1.dat
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class FS(isDesktop: Boolean) {

    private val log = Logger.get<FS>()

    private val fs: FSService = FSServiceImpl(isDesktop)

    init {
        log.debug("Loaded ${fs.javaClass.simpleName}")
    }

    /**
     * @return true if file or dir with given name exists
     */
    fun exists(pathName: String): Boolean {
        return fs.exists(pathName)
    }

    /**
     * Creates [dirName] directory, creating required parent directories if necessary.
     */
    fun createDirectoryTask(dirName: String): IOTask<Void> = IOTask.ofVoid("createDirectoryTask($dirName)", {
        fs.createDirectory(dirName)
    })

    /**
     * Writes binary data to file, creating required directories.
     *
     * @param data data object to save
     * @param fileName to save as
     * @return IO task
     */
    fun writeDataTask(data: Serializable, fileName: String) = IOTask.ofVoid("writeDataTask($fileName)", {
        fs.writeData(data, fileName)
    })

    /**
     * Writes text data to file, creating required directories.
     *
     * @param text text data to save
     * @param fileName to save as
     * @return IO task
     */
    fun writeDataTask(text: List<String>, fileName: String) = IOTask.ofVoid("writeDataTask($fileName)", {
        fs.writeData(text, fileName)
    })

    /**
     * Loads data from file into an object.
     *
     * @param fileName file to load from
     * @return IO task
     */
    fun <T> readDataTask(fileName: String): IOTask<T> = IOTask.of("readDataTask($fileName)") {
        fs.readData<T>(fileName)
    }

    /**
     * Loads file names from given directory.
     * Searches subdirectories if recursive flag is on.
     *
     * @param dirName directory name
     * @param recursive recursive flag
     * @return IO task
     */
    fun loadFileNamesTask(dirName: String, recursive: Boolean): IOTask<List<String>>
            = IOTask.of("loadFileNamesTask($dirName, $recursive)") {
        fs.loadFileNames(dirName, recursive)
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
    fun loadFileNamesTask(dirName: String, recursive: Boolean, extensions: List<FileExtension>): IOTask<List<String>>
            = IOTask.of("loadFileNamesTask($dirName, $recursive, $extensions)") {
        fs.loadFileNames(dirName, recursive, extensions)
    }

    /**
     * Loads directory names from [dirName].
     * Searches subdirectories if [recursive].
     *
     * @param dirName directory name
     * @param recursive recursive flag
     * @return IO task
     */
    fun loadDirectoryNamesTask(dirName: String, recursive: Boolean): IOTask<List<String>>
            = IOTask.of("loadDirectoryNamesTask($dirName, $recursive)", {
        fs.loadDirectoryNames(dirName, recursive)
    })

    /**
     * Loads (deserializes) last modified file from given [dirName] directory.
     * Searches subdirectories if [recursive].
     *
     * @param dirName directory name
     * @param recursive recursive flag
     * @return IO task
     */
    fun <T> loadLastModifiedFileTask(dirName: String, recursive: Boolean): IOTask<T>
            = IOTask.of("loadLastModifiedFileTask($dirName, $recursive)") {
        fs.loadLastModifiedFileName(dirName, recursive)
    }.then { fileName -> readDataTask<T>(normalize(dirName) + fileName) }

    /**
     * Delete file [fileName].
     *
     * @param fileName name of file to delete
     * @return IO task
     */
    fun deleteFileTask(fileName: String): IOTask<Void> = IOTask.ofVoid("deleteFileTask($fileName)") {
        fs.deleteFile(fileName)
    }

    /**
     * Delete directory [dirName] and its contents.
     *
     * @param dirName directory name to delete
     * @return IO task
     */
    fun deleteDirectoryTask(dirName: String): IOTask<Void> = IOTask.ofVoid("deleteDirectoryTask($dirName)") {
        fs.deleteDirectory(dirName)
    }

    private fun normalize(dirName: String): String
            = if (dirName.endsWith(File.separatorChar)) dirName else dirName + File.separatorChar
}