/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.cutscene

import com.almasb.fxgl.core.EngineService
import com.almasb.fxgl.core.collection.PropertyMap
import com.almasb.fxgl.cutscene.dialogue.DialogueGraph
import com.almasb.fxgl.cutscene.dialogue.DialogueScene
import com.almasb.fxgl.scene.SceneService

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class CutsceneService : EngineService() {

    private lateinit var sceneService: SceneService

    private var gameVars: PropertyMap? = null

    private val scene by lazy { CutsceneScene(sceneService) }
    private val dialogueScene by lazy { DialogueScene(sceneService) }

    fun startCutscene(cutscene: Cutscene) {
        scene.start(cutscene)
    }

    fun startDialogueScene(dialogueGraph: DialogueGraph) {
        dialogueScene.gameVars = gameVars ?: throw IllegalStateException("Cannot start dialogue scene. The game is not initialized yet.")
        dialogueScene.start(dialogueGraph)
    }

    override fun onGameReady(vars: PropertyMap) {
        gameVars = vars
    }
}