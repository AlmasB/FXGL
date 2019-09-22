/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.saving

import com.almasb.fxgl.app.ProgressDialog
import com.almasb.fxgl.core.concurrent.Async
import com.almasb.fxgl.core.concurrent.IOTask
import com.almasb.fxgl.dsl.FXGL
import com.almasb.fxgl.io.FS
import com.almasb.fxgl.io.FileExtension
import com.almasb.sslogger.Logger
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import java.io.FileNotFoundException
import java.util.*

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class ProfileManager(val fs: FS,
                     val profileName: String,
                     val profilesDirName: String,
                     val profileFileName: String,
                     val savesDirName: String,
                     val saveFileExt: String,
                     val dataFileExt: String
                     ) {

    private val log = Logger.get<ProfileManager>()

    /**
     * Relative path as string to profile dir.
     */
    private val profileDir = "./$profilesDirName$profileName/"

    /**
     * Relative path as string to save dir.
     */
    private val saveDir = "$profileDir$savesDirName"

    private val saveFiles = FXCollections.observableArrayList<SaveFile>()

    /**
     * @return read only view of observable save files
     */
    fun saveFilesProperty(): ObservableList<SaveFile> = FXCollections.unmodifiableObservableList(saveFiles)

    /**
     * Saves user profile to "profiles/".
     * Creates "saves/" in that directory.
     *
     * @param profile the profile to save
     * @return saving task
     */
    fun saveProfileTask(profile: UserProfile): IOTask<Void> {
        log.debug("Saving profile: $profileName")
        
        return fs.writeDataTask(profile, profileDir + profileFileName)
                .then {
                    IOTask.ofVoid("checkSavesDir($saveDir)") {
                        if (!fs.exists(saveDir)) {
                            createSavesDir()
                        }
                    }
                }
    }
    
    private fun createSavesDir() {
        log.debug("Creating saves dir")

        fs.createDirectoryTask(saveDir)
                .then { fs.writeDataTask(listOf("This directory contains save files."), saveDir + "Readme.txt") }
                .onFailure { e ->
                    log.warning("Failed to create saves dir: $e")
                    Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e)
                }
                .run()
    }

    /**
     * Loads user profile from "profiles/".
     *
     * @return saving task
     */
    fun loadProfileTask(): IOTask<UserProfile> {
        log.debug("Loading profile: $profileName")
        return fs.readDataTask(profileDir + profileFileName)
    }

    /**
     * @param saveFileName save file name
     * @return true iff file exists
     */
    fun saveFileExists(saveFileName: String): Boolean {
        log.debug("Checking if save file exists: $saveFileName")

        return fs.exists(saveDir + saveFileName + saveFileExt)
    }

    /**
     * @param saveFile save file to delete
     * @return saving task
     */
    fun deleteSaveFileTask(saveFile: SaveFile): IOTask<Void> {
        log.debug("Deleting save file: " + saveFile.name)

        return fs.deleteFileTask(saveDir + saveFile.name + saveFileExt)
                .then { fs.deleteFileTask(saveDir + saveFile.name + dataFileExt) }
                .then { IOTask.ofVoid("updateSaves") { Async.startAsyncFX<Boolean> { saveFiles.remove(saveFile) } } }
    }

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
    fun saveTask(dataFile: DataFile, saveFile: SaveFile): IOTask<Void> {
        log.debug("Saving data: " + saveFile.name)

        return fs.writeDataTask(saveFile, saveDir + saveFile.name + saveFileExt)
                .then { fs.writeDataTask(dataFile, saveDir + saveFile.name + dataFileExt) }
                .then {
                    IOTask.ofVoid("updateSaves") {
                        Async.startAsyncFX {
                            saveFiles.add(saveFile)
                            Collections.sort(saveFiles, SaveFile)
                        }
                    }
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
    fun loadTask(saveFile: SaveFile): IOTask<DataFile> {
        log.debug("Loading data: " + saveFile.name)
        return fs.readDataTask(saveDir + saveFile.name + dataFileExt)
    }

    /**
     * Loads save files with save file extension from SAVE_DIR.
     *
     * @return saving task
     */
    fun loadSaveFilesTask(): IOTask<List<SaveFile>> {
        log.debug("Loading save files")

        return fs.loadFileNamesTask(saveDir, true, listOf<FileExtension>(FileExtension(saveFileExt)))
                .then { fileNames ->
                    IOTask.of<List<SaveFile>>("readSaveFiles") {

                        val list = ArrayList<SaveFile>()
                        for (name in fileNames) {
                            val file = fs.readDataTask<SaveFile>(saveDir + name).run()
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
     *
     * @return saving task
     */
    fun loadLastModifiedSaveFileTask(): IOTask<SaveFile> {
        log.debug("Loading last modified save file")

        return loadSaveFilesTask().then { files ->
            IOTask.of("findLastSave") {
                if (files.isEmpty()) {
                    throw FileNotFoundException("No save files found")
                }

                Collections.sort(files, SaveFile)
                files[0]
            }
        }
    }

    /**
     * TODO: extract FXGL ref, should probably return Task<> rather than void, so runAsyncFX can be
     * called from outside
     * Asynchronously (with a progress dialog) loads save files into observable list [saveFiles].
     */
    fun querySaveFiles() {
        log.debug("Querying save files")

        loadSaveFilesTask()
                .onSuccess { files ->
                    saveFiles.setAll(files)
                    Collections.sort(saveFiles, SaveFile)
                }
                .runAsyncFXWithDialog(ProgressDialog(FXGL.localize("menu.loadingSaveFiles")))
    }
}