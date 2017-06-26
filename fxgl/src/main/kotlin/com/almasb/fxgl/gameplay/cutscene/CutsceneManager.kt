/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.gameplay.cutscene

import com.almasb.fxgl.app.FXGL

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class CutsceneManager {

    private val cachedScripts = hashMapOf<String, Cutscene>()

    private val state = CutsceneState()

    fun startCutscene(scriptName: String) {
        startCutscene(cachedScripts[scriptName] ?: Cutscene(scriptName))
    }

    private fun startCutscene(cutscene: Cutscene) {
        cachedScripts[cutscene.scriptName] = cutscene

        state.start(cutscene)

        FXGL.getApp().stateMachine.pushState(state)
    }

    fun startRPGCutscene() {
        // TODO: hardcoded
        state.start(RPGCutscene())

        FXGL.getApp().stateMachine.pushState(state)
    }
}