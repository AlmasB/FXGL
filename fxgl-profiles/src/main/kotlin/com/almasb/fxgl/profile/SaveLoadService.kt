/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.profile

import com.almasb.fxgl.core.concurrent.IOTask
import com.almasb.fxgl.io.FS
import com.almasb.fxgl.io.FileExtension
import com.almasb.sslogger.Logger
import java.io.FileNotFoundException
import java.util.*

class SaveLoadService(private val fs: FS) {

    private val log = Logger.get(javaClass)

    // TODO: this should be read from settings
    private val PROFILES_DIR = "profiles/"

    private val saveLoadHandlers = arrayListOf<SaveLoadHandler>()

    fun addHandler(saveLoadHandler: SaveLoadHandler) {
        saveLoadHandlers += saveLoadHandler
    }

    fun removeHandler(saveLoadHandler: SaveLoadHandler) {
        saveLoadHandlers -= saveLoadHandler
    }

    fun save(dataFile: DataFile) {
        saveLoadHandlers.forEach { it.onSave(dataFile) }
    }

    fun load(dataFile: DataFile) {
        saveLoadHandlers.forEach { it.onLoad(dataFile) }
    }

    fun saveFileExists(saveFile: SaveFile): Boolean {
        log.debug("Checking if save file exists: $saveFile")

        return fs.exists(PROFILES_DIR + saveFile.relativePathName)
    }

    // TODO: API rename for consistency

    /**
     * Save serializable data onto a disk file system under saves directory,
     * which is created if necessary in the directory where the game is start from.
     *
     *
     * All extra directories will also be created if necessary.
     *
     * @param dataFile data file
     * @param saveFile save file
     * @return saving task
     */
    fun writeSaveFileTask(saveFile: SaveFile): IOTask<Void> {
        if (!fs.exists(PROFILES_DIR)) {
            createProfilesDirTask().run()
        }

        log.debug("Saving data: ${saveFile.name}")

        return fs.writeDataTask(saveFile, PROFILES_DIR + saveFile.relativePathName)
    }

    private fun createProfilesDirTask(): IOTask<*> {
        log.debug("Creating profiles dir")

        return fs.createDirectoryTask(PROFILES_DIR)
                .then { fs.writeDataTask(Collections.singletonList("This directory contains user profiles."), PROFILES_DIR + "Readme.txt") }
                .onFailure {
                    log.warning("Failed to create profiles dir: $it")
                    Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), it)
                }
    }

    /**
     * Load serializable data from external
     * file on disk file system from saves directory which is
     * in the directory where the game is start from.
     *
     * @param saveFile save file to load
     * @return saving task
     */
    fun readSaveFileTask(saveFile: SaveFile): IOTask<SaveFile> {
        log.debug("Loading data: ${saveFile.name}")
        return fs.readDataTask(PROFILES_DIR + saveFile.relativePathName)
    }

    /**
     * @param saveFile save file to delete
     * @return saving task
     */
    fun deleteSaveFileTask(saveFile: SaveFile): IOTask<Void> {
        log.debug("Deleting save file: ${saveFile.name}")

        return fs.deleteFileTask(PROFILES_DIR + saveFile.relativePathName)
    }

    /**
     * Loads save files with save file extension from SAVE_DIR.
     *
     * @return saving task
     */
    fun readSaveFilesTask(profileName: String, saveFileExt: String): IOTask<List<SaveFile>> {
        log.debug("Loading save files")

        return fs.loadFileNamesTask(PROFILES_DIR + profileName, true, listOf(FileExtension(saveFileExt)))
                .then { fileNames ->
                    IOTask.of<List<SaveFile>>("readSaveFiles") {

                        val list = ArrayList<SaveFile>()
                        for (name in fileNames) {
                            val file = fs.readDataTask<SaveFile>("$PROFILES_DIR$profileName/$name").run()
                            if (file != null) {
                                list.add(file)
                            }
                        }
                        list
                    }
                }
    }

    /**
     * Loads last modified save file from saves directory.
     */
    fun loadLastModifiedSaveFileTask(profileName: String, saveFileExt: String): IOTask<SaveFile> {
        log.debug("Loading last modified save file")

        return readSaveFilesTask(profileName, saveFileExt).then { files ->
            IOTask.of("findLastSave") {
                if (files.isEmpty()) {
                    throw FileNotFoundException("No save files found")
                }

                Collections.sort(files, SaveFile.RECENT_FIRST)
                files[0]
            }
        }
    }

    fun createProfileTask(profileName: String): IOTask<Void> {
        log.debug("Creating profile: $profileName")
        return fs.createDirectoryTask("./$PROFILES_DIR$profileName")
    }

    /**
     * A task that reads all profile names.
     */
    fun readProfileNamesTask(): IOTask<List<String>> {
        log.debug("Reading profile names")
        return fs.loadDirectoryNamesTask("./$PROFILES_DIR", recursive = false)
    }

    /**
     * Delete profile.
     *
     * @param profileName name of profile to delete
     */
    fun deleteProfileTask(profileName: String): IOTask<Void> {
        log.debug("Deleting profile: $profileName")
        return fs.deleteDirectoryTask("./$PROFILES_DIR$profileName")
    }
}