/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.profile

import com.almasb.fxgl.core.EngineService
import com.almasb.fxgl.core.collection.PropertyMap
import com.almasb.fxgl.core.concurrent.IOTask
import com.almasb.fxgl.core.serialization.Bundle
import com.almasb.fxgl.io.FileExtension
import com.almasb.fxgl.io.FileSystemService
import com.almasb.fxgl.logging.Logger
import com.almasb.fxgl.scene.SceneService
import java.util.*

/**
 * Responsible for save/load operations to/from data files.
 * Also responsible for write/read IO operations of save files.
 */
class SaveLoadService : EngineService() {

    private val log = Logger.get(javaClass)

    private lateinit var sceneService: SceneService
    private lateinit var fs: FileSystemService

    private val saveLoadHandlers = arrayListOf<SaveLoadHandler>()

    fun addHandler(saveLoadHandler: SaveLoadHandler) {
        saveLoadHandlers += saveLoadHandler
    }

    fun removeHandler(saveLoadHandler: SaveLoadHandler) {
        saveLoadHandlers -= saveLoadHandler
    }

    fun addWorldPropertiesHandler() {
        addHandler(object : SaveLoadHandler {
            override fun onSave(data: DataFile) {
                data.putBundle(Bundle("fxglDataVars").also {
                    it.put("vars", sceneService.worldProperties.toStringMap() as java.io.Serializable)
                })
            }

            override fun onLoad(data: DataFile) {
                val bundle = data.getBundle("fxglDataVars")
                val map = bundle.get<Map<String, String>>("vars")

                val pMap = PropertyMap.fromStringMap(map)

                pMap.forEach { key, value ->
                    sceneService.worldProperties.setValue(key, pMap.getValue(key))
                }
            }
        })
    }

    /**
     * Ask all handlers to save their data to given [dataFile].
     * Note: this doesn't perform any IO operations.
     */
    fun save(dataFile: DataFile) {
        saveLoadHandlers.forEach { it.onSave(dataFile) }
    }

    /**
     * Ask all handlers to load their data from given [dataFile].
     * Note: this doesn't perform any IO operations.
     */
    fun load(dataFile: DataFile) {
        saveLoadHandlers.forEach { it.onLoad(dataFile) }
    }

    fun saveFileExists(saveFileName: String): Boolean {
        log.debug("Checking if save file exists: $saveFileName")

        return fs.exists(saveFileName)
    }

    /**
     * Calls [save] first, then writes the resulting data file to file system.
     * All extra directories will also be created if necessary.
     */
    fun saveAndWriteTask(saveFileName: String): IOTask<Void> {
        return IOTask.of {
            val dataFile = DataFile()

            save(dataFile)

            dataFile
        }.then { writeTask(saveFileName, it) }
    }

    /**
     * Writes [dataFile] to file system under name [saveFileName].
     */
    fun writeTask(saveFileName: String, dataFile: DataFile): IOTask<Void> {
        val saveFile = SaveFile(saveFileName, data = dataFile)

        log.debug("writeTask: ${saveFile.name}")

        return fs.writeDataTask(saveFile, saveFile.name)
    }

    /**
     * Reads serializable data from external file on disk file system.
     * Then calls [load].
     */
    fun readAndLoadTask(saveFileName: String): IOTask<Void> {
        log.debug("readAndLoadTask: $saveFileName")

        return readTask(saveFileName)
                .then { saveFile ->
                    IOTask.ofVoid {
                        load(saveFile.data)
                    }
                }
    }

    /**
     * Reads serializable data from external file on disk file system.
     */
    fun readTask(saveFileName: String): IOTask<SaveFile> {
        return fs.readDataTask<SaveFile>(saveFileName)
    }

    /**
     * Deletes a save file with a given [saveFileName].
     */
    fun deleteSaveFileTask(saveFileName: String): IOTask<Void> {
        log.debug("Deleting save file: $saveFileName")

        return fs.deleteFileTask(saveFileName)
    }

    /**
     * @return a task that reads (IO operation) from latest modified file with extension [saveFileExt] in [dirName]
     */
    fun readLastModifiedSaveFileTask(dirName: String, saveFileExt: String): IOTask<Optional<SaveFile>> {
        log.debug("Reading last modified save file from $dirName with ext: $saveFileExt")

        return readSaveFilesTask(dirName, saveFileExt).then { files ->
            IOTask.of("findLastSave") {
                if (files.isEmpty()) {
                    log.warning("No save files found")
                    return@of Optional.empty<SaveFile>()
                }

                Collections.sort(files, SaveFile.RECENT_FIRST)
                Optional.of(files[0])
            }
        }
    }

    /**
     * Reads (IO operation) save files with extension [saveFileExt] from given directory [dirName].
     */
    fun readSaveFilesTask(dirName: String, saveFileExt: String): IOTask<List<SaveFile>> {
        log.debug("Reading save files from $dirName")

        return fs.loadFileNamesTask(dirName, true, listOf(FileExtension(saveFileExt)))
                .then { fileNames ->
                    IOTask.of<List<SaveFile>>("readSaveFiles") {

                        val list = ArrayList<SaveFile>()
                        for (name in fileNames) {
                            val file = fs.readDataTask<SaveFile>("$dirName/$name").run()
                            if (file != null) {
                                list.add(file)
                            }
                        }
                        list
                    }
                }
    }
}