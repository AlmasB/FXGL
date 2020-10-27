/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.controllerinput

import com.almasb.fxgl.core.EngineService
import com.almasb.fxgl.core.Inject
import com.almasb.fxgl.core.util.Platform
import com.almasb.fxgl.core.util.Platform.*
import com.almasb.fxgl.input.Input
import com.almasb.fxgl.input.virtual.VirtualButton
import com.almasb.fxgl.logging.Logger
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class ControllerInputService : EngineService() {

    private val log = Logger.get(javaClass)

    @Inject("platform")
    private lateinit var platform: Platform

    private var isNativeLibLoaded = false

    private val states = EnumMap<VirtualButton, Boolean>(VirtualButton::class.java).also { map ->
        VirtualButton.values().forEach { map[it] = false }
    }

    private var controller: GameController = GameController()

    private val inputHandlers = arrayListOf<Input>()

    private val resourceDirNames = hashMapOf(
            WINDOWS to "windows64",
            LINUX to "linux64",
            MAC to "mac64"
    )

    private val nativeLibNames = hashMapOf(
            WINDOWS to listOf("SDL2.dll", "fxgl_controllerinput.dll"),
            LINUX to listOf("libSDL2.so", "libfxgl_controllerinput.so")
            //MAC to listOf("", "")
    )

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

            log.debug("Successfully loaded nativeLibs")

            controller.connect()

        } catch (e: Exception) {
            log.warning("Loading nativeLibs for controller failed", e)
        }
    }

    override fun onUpdate(tpf: Double) {
        if (!isNativeLibLoaded || inputHandlers.isEmpty())
            return

        // TODO: not very efficient
        VirtualButton.values().forEach {
            val wasPressed = states[it]!!
            val isPressed = controller.isPressed(it)

            if (isPressed && !wasPressed) {
                inputHandlers.forEach { input -> input.pressVirtual(it) }
            }

            if (!isPressed && wasPressed) {
                inputHandlers.forEach { input -> input.releaseVirtual(it) }
            }

            states[it] = isPressed
        }
    }

    override fun onExit() {
        if (!isNativeLibLoaded)
            return

        controller.disconnect()
    }

    fun addInputHandler(input: Input) {
        inputHandlers += input
    }
}