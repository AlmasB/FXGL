/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app.services

import com.almasb.fxgl.core.EngineService
import com.almasb.fxgl.core.Inject
import com.almasb.fxgl.core.serialization.Bundle
import com.almasb.fxgl.io.FileSystemService
import com.almasb.fxgl.logging.Logger

internal class SystemBundleService : EngineService() {

    private val log = Logger.get(javaClass)

    @Inject("isNative")
    private var isNative = false

    @Inject("isFileSystemWriteAllowed")
    private var isFileSystemWriteAllowed = true

    private lateinit var fs: FileSystemService

    internal lateinit var bundle: Bundle

    override fun onInit() {
        val isFirstRun = !fs.exists("system/")

        if (!isNative && isFileSystemWriteAllowed) {
            if (isFirstRun) {
                createRequiredDirs()
                loadDefaultSystemData()
            } else {
                loadSystemData()
            }
        } else {
            loadDefaultSystemData()
        }
    }

    override fun onExit() {
        if (!isFileSystemWriteAllowed)
            return

        if (!isNative) {
            saveSystemData()
        }
    }

    private fun createRequiredDirs() {
        fs.createDirectoryTask("system/")
                .then { fs.writeDataTask(listOf("This directory contains FXGL system data files."), "system/Readme.txt") }
                .onFailure { e ->
                    log.warning("Failed to create system dir: $e")
                    Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e)
                }
                .run()
    }

    private fun saveSystemData() {
        log.debug("Saving FXGL system data")

        fs.writeDataTask(bundle, "system/fxgl.bundle")
                .onFailure { log.warning("Failed to save: $it") }
                .run()
    }

    private fun loadSystemData() {
        log.debug("Loading FXGL system data")

        fs.readDataTask<Bundle>("system/fxgl.bundle")
                .onSuccess {
                    bundle = it
                    log.debug("$bundle")
                }
                .onFailure {
                    log.warning("Failed to load: $it")
                    loadDefaultSystemData()
                }
                .run()
    }

    private fun loadDefaultSystemData() {
        log.debug("Loading default FXGL system data")

        // populate with default info
        bundle = Bundle("FXGL")
    }
}