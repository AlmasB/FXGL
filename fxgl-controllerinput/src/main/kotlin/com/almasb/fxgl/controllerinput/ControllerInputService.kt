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

            // TODO: use Platform to calc native lib paths
            System.load(File(javaClass.getResource("/nativeLibs/windows64/SDL2.dll").toURI()).absolutePath)

            val dllFile = javaClass.getResource("/nativeLibs/windows64/fxgl_controllerinput.dll")

            log.debug("Found dll file: $dllFile")

            val absPath = File(dllFile.toURI()).absolutePath

            log.debug("Absolute path: $absPath")

            System.load(absPath)

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