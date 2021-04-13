/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.cutscene

import com.almasb.fxgl.core.EngineService
import com.almasb.fxgl.core.asset.AssetLoaderService
import com.almasb.fxgl.core.collection.PropertyMap
import com.almasb.fxgl.core.util.EmptyRunnable
import com.almasb.fxgl.cutscene.dialogue.DialogueGraph
import com.almasb.fxgl.cutscene.dialogue.DialogueScene
import com.almasb.fxgl.cutscene.dialogue.FunctionCallHandler
import com.almasb.fxgl.logging.Logger
import com.almasb.fxgl.scene.SceneService

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class CutsceneService : EngineService() {

    private lateinit var assetLoader: AssetLoaderService
    private lateinit var sceneService: SceneService

    private var gameVars: PropertyMap? = null

    private val scene by lazy { CutsceneScene(sceneService) }
    val dialogueScene by lazy { DialogueScene(sceneService) }

    fun startCutscene(cutscene: Cutscene) {
        scene.assetLoader = assetLoader
        scene.start(cutscene)
    }

    @JvmOverloads fun startDialogueScene(dialogueGraph: DialogueGraph, functionHandler: FunctionCallHandler = EmptyFunctionCallHandler, onFinished: Runnable = EmptyRunnable) {
        dialogueScene.gameVars = gameVars ?: throw IllegalStateException("Cannot start dialogue scene. The game is not initialized yet.")
        dialogueScene.assetLoader = assetLoader
        dialogueScene.start(dialogueGraph, functionHandler, onFinished)
    }

    override fun onGameReady(vars: PropertyMap) {
        gameVars = vars
    }
}

private object EmptyFunctionCallHandler : FunctionCallHandler {
    private val log = Logger.get(javaClass)

    override fun handle(functionName: String, args: Array<String>): Any {
        log.warning("Function call from dialogue graph via EmptyFunctionCallHandler:")
        log.warning("$functionName ${args.toList()}")
        return 0
    }
}