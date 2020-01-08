/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.profile

import com.almasb.fxgl.core.EngineService
import com.almasb.fxgl.core.Inject
import com.almasb.fxgl.core.collection.PropertyMap
import com.almasb.fxgl.core.concurrent.IOTask
import com.almasb.fxgl.core.serialization.Bundle
import com.almasb.fxgl.io.FS
import com.almasb.sslogger.Logger
import java.util.*

class SaveLoadService : EngineService {

    private val log = Logger.get(javaClass)

    private val PROFILES_DIR = "profiles/"

    @Inject("FS")
    private lateinit var fs: FS

    private val saveLoadHandlers = arrayListOf<SaveLoadHandler>()

    //    private val saveFiles = FXCollections.observableArrayList<SaveFile>()

    //
//    /**
//     * @return read only view of observable save files
//     */
//    fun saveFilesProperty(): ObservableList<SaveFile> = FXCollections.unmodifiableObservableList(saveFiles)

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

    /**
     * @param saveFileName save file name
     * @return true iff file exists
     */
    fun saveFileExists(saveFile: SaveFile): Boolean {
        log.debug("Checking if save file exists: $saveFile")

        return fs.exists(saveFile.relativePathName)
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
    fun writeSaveFileTask(saveFile: SaveFile): IOTask<Void> {
        if (!fs.exists(PROFILES_DIR)) {
            createProfilesDirTask().run()
        }

        log.debug("Saving data: ${saveFile.name}")

        return fs.writeDataTask(saveFile, PROFILES_DIR + saveFile.relativePathName)
                .then {
                    IOTask.ofVoid("updateSaves") {
//                        Async.startAsyncFX {
//                            saveFiles.add(saveFile)
//                            Collections.sort(saveFiles, SaveFile)
//                        }
                    }
                }
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
                .then {
                    IOTask.ofVoid("updateSaves") {
                        //Async.startAsyncFX<Boolean> { saveFiles.remove(saveFile) }
                    }
                }
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

    override fun onMainLoopStarting() {

    }

    override fun onGameReady(vars: PropertyMap) {
        // TODO: auto (de)serialize these
    }

    override fun onExit() {
    }

    override fun onUpdate(tpf: Double) {
    }

    override fun write(bundle: Bundle) {
    }

    override fun read(bundle: Bundle) {
    }
}











//
//    /**
//     * Saves user profile to "profiles/".
//     * Creates "saves/" in that directory.
//     *
//     * @param profile the profile to save
//     * @return saving task
//     */
//    fun saveProfileTask(profile: UserProfile): IOTask<Void> {
//        log.debug("Saving profile: $profileName")
//
//        return fs.writeDataTask(profile, profileDir + profileFileName)
//                .then {
//                    IOTask.ofVoid("checkSavesDir($saveDir)") {
//                        if (!fs.exists(saveDir)) {
//                            createSavesDir()
//                        }
//                    }
//                }
//    }
//
//    private fun createSavesDir() {
//        log.debug("Creating saves dir")
//
//        fs.createDirectoryTask(saveDir)
//                .then { fs.writeDataTask(listOf("This directory contains save files."), saveDir + "Readme.txt") }
//                .onFailure { e ->
//                    log.warning("Failed to create saves dir: $e")
//                    Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e)
//                }
//                .run()
//    }
//
//    /**
//     * Loads user profile from "profiles/".
//     *
//     * @return saving task
//     */
//    fun loadProfileTask(): IOTask<UserProfile> {
//        log.debug("Loading profile: $profileName")
//        return fs.readDataTask(profileDir + profileFileName)
//    }

//
//    /**
//     * Loads save files with save file extension from SAVE_DIR.
//     *
//     * @return saving task
//     */
//    fun loadSaveFilesTask(): IOTask<List<SaveFile>> {
//        log.debug("Loading save files")
//
//        return fs.loadFileNamesTask(saveDir, true, listOf<FileExtension>(FileExtension(saveFileExt)))
//                .then { fileNames ->
//                    IOTask.of<List<SaveFile>>("readSaveFiles") {
//
//                        val list = ArrayList<SaveFile>()
//                        for (name in fileNames) {
//                            val file = fs.readDataTask<SaveFile>(saveDir + name).run()
//                            if (file != null) {
//                                list.add(file)
//                            }
//                        }
//                        list
//                    }
//                }
//    }
//
//    /**
//     * Loads last modified save file from saves directory.
//     *
//     * @return saving task
//     */
//    fun loadLastModifiedSaveFileTask(): IOTask<SaveFile> {
//        log.debug("Loading last modified save file")
//
//        return loadSaveFilesTask().then { files ->
//            IOTask.of("findLastSave") {
//                if (files.isEmpty()) {
//                    throw FileNotFoundException("No save files found")
//                }
//
//                Collections.sort(files, SaveFile)
//                files[0]
//            }
//        }
//    }
//
//    /**
//     * TODO: extract FXGL ref, should probably return Task<> rather than void, so runAsyncFX can be
//     * called from outside
//     * Asynchronously (with a progress dialog) loads save files into observable list [saveFiles].
//     */
//    fun querySaveFiles() {
//        log.debug("Querying save files")
//
////        loadSaveFilesTask()
////                .onSuccess { files ->
////                    saveFiles.setAll(files)
////                    Collections.sort(saveFiles, SaveFile)
////                }
////                .runAsyncFXWithDialog(ProgressDialog(FXGL.localize("menu.loadingSaveFiles")))
//    }