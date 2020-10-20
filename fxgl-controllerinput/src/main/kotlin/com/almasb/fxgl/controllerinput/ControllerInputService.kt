/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.controllerinput

import com.almasb.fxgl.core.EngineService
import com.almasb.fxgl.input.Input
import com.almasb.fxgl.input.virtual.VirtualButton
import com.almasb.fxgl.logging.Logger
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class ControllerInputService : EngineService() {

    private val log = Logger.get(javaClass)

    private var isNativeLibLoaded = false

    private val states = EnumMap<VirtualButton, Boolean>(VirtualButton::class.java).also { map ->
        VirtualButton.values().forEach { map[it] = false }
    }

    private var controller: GameController = GameController()

    private val inputHandlers = arrayListOf<Input>()

    override fun onInit() {
        try {
            log.debug("Loading nativeLibs")

            // copy native libs to cache if needed
            // use openjfx dir since we know it is (will be) there

            val fxglCacheDir = Paths.get(System.getProperty("user.home")).resolve(".openjfx").resolve("cache").resolve("fxgl-11")

            if (Files.notExists(fxglCacheDir)) {
                log.debug("Creating FXGL native libs cache: $fxglCacheDir")

                Files.createDirectories(fxglCacheDir)
            }

            // TODO: use Platform to calc native lib paths
            val sdlDLL = fxglCacheDir.resolve("SDL2.dll")
            val fxglDLL = fxglCacheDir.resolve("fxgl_controllerinput.dll")

            if (Files.notExists(sdlDLL)) {
                log.debug("Copying SDL2.dll into cache")

                javaClass.getResource("/nativeLibs/windows64/SDL2.dll").openStream().use {
                    Files.copy(it, sdlDLL)
                }
            }

            if (Files.notExists(fxglDLL)) {
                log.debug("Copying fxgl_controllerinput.dll into cache")

                javaClass.getResource("/nativeLibs/windows64/fxgl_controllerinput.dll").openStream().use {
                    Files.copy(it, fxglDLL)
                }
            }

            log.debug("Loading $sdlDLL")

            System.load(sdlDLL.toAbsolutePath().toString())

            log.debug("Loading $fxglDLL")

            System.load(fxglDLL.toAbsolutePath().toString())

            isNativeLibLoaded = true

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