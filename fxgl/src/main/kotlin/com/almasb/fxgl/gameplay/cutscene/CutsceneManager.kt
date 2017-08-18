/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.gameplay.cutscene

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.core.logging.Logger

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class CutsceneManager {

    private val log = Logger.get(javaClass)

    private val cachedScripts = hashMapOf<String, RPGCutscene>()

    private val rpgState = RPGCutsceneState()
    private val jrpgState = JRPGCutsceneState()

    /**
     * Supports .js and .txt
     */
    fun startCutscene(scriptName: String) {
        if (FXGL.getApp().stateMachine.currentState is CutsceneState) {
            log.warning("Cannot start more than 1 cutscene")
            return
        }

        if (scriptName.endsWith(".js")) {
            startRPG(cachedScripts[scriptName] ?: RPGCutscene(scriptName))
        } else if (scriptName.endsWith(".txt")) {
            startJRPG(JRPGCutscene(scriptName))
        } else {
            throw IllegalArgumentException("Unsupported cutscene format")
        }
    }

    private fun startRPG(cutscene: RPGCutscene) {
        cachedScripts[cutscene.scriptName] = cutscene

        rpgState.start(cutscene)

        FXGL.getApp().stateMachine.pushState(rpgState)
    }

    private fun startJRPG(cutscene: JRPGCutscene) {
        jrpgState.start(cutscene)

        FXGL.getApp().stateMachine.pushState(jrpgState)
    }
}