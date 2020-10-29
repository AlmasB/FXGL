/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.controllerinput

import com.almasb.fxgl.controllerinput.impl.GameControllerImpl
import com.almasb.fxgl.core.EngineService
import com.almasb.fxgl.core.Inject
import com.almasb.fxgl.core.util.Platform
import com.almasb.fxgl.core.util.Platform.*
import com.almasb.fxgl.logging.Logger
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import java.nio.file.Files
import java.nio.file.Paths

/**
 * Provides access to game controllers. Currently support is limited: no hot-swaps/reloads and
 * the controller(s) must be plugged in before the start of the engine.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class ControllerInputService : EngineService() {

    private val log = Logger.get(javaClass)

    @Inject("platform")
    private lateinit var platform: Platform

    private var isNativeLibLoaded = false

    private val resourceDirNames = hashMapOf(
            WINDOWS to "windows64",
            LINUX to "linux64",
            MAC to "mac64"
    )

    private val nativeLibNames = hashMapOf(
            WINDOWS to listOf("SDL2.dll", "fxgl_controllerinput.dll"),
            LINUX to listOf("libSDL2.so", "libfxgl_controllerinput.so")
            //TODO: MAC to listOf("", "")
    )

    private val controllers = FXCollections.observableArrayList<GameController>()

    val gameControllers: ObservableList<GameController> by lazy { FXCollections.unmodifiableObservableList(controllers) }

    override fun onInit() {
        try {
            log.debug("Loading nativeLibs for $platform")

            // copy native libs to cache if needed
            // use openjfx dir since we know it is (will be) there

            val fxglCacheDir = Paths.get(System.getProperty("user.home")).resolve(".openjfx").resolve("cache").resolve("fxgl-11")

            if (Files.notExists(fxglCacheDir)) {
                log.debug("Creating FXGL native libs cache: $fxglCacheDir")

                Files.createDirectories(fxglCacheDir)
            }

            nativeLibNames[platform]?.forEach { libName ->
                val nativeLibPath = fxglCacheDir.resolve(libName)

                if (Files.notExists(nativeLibPath)) {
                    log.debug("Copying $libName into cache")

                    val dirName = resourceDirNames[platform]!!

                    javaClass.getResource("/nativeLibs/$dirName/$libName").openStream().use {
                        Files.copy(it, nativeLibPath)
                    }
                }

                log.debug("Loading $nativeLibPath")

                System.load(nativeLibPath.toAbsolutePath().toString())
            }

            isNativeLibLoaded = true

            log.debug("Successfully loaded nativeLibs. Calling to check back-end version.")

            val version = GameControllerImpl.getBackendVersion()

            log.info("Controller back-end version: $version")

            log.debug("Connecting to plugged-in controllers")

            GameControllerImpl.connectControllers()

        } catch (e: Exception) {
            log.warning("Loading nativeLibs for controller support failed", e)
        }
    }

    override fun onUpdate(tpf: Double) {
        if (!isNativeLibLoaded || controllers.isEmpty())
            return

        controllers.forEach { it.updateState() }
    }

    override fun onExit() {
        if (!isNativeLibLoaded || controllers.isEmpty())
            return

        GameControllerImpl.disconnectControllers()
    }
}