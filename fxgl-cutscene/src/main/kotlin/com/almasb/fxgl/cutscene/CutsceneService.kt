/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.cutscene

import com.almasb.fxgl.core.EngineService
import com.almasb.fxgl.core.Inject
import com.almasb.fxgl.core.collection.PropertyMap
import com.almasb.fxgl.core.serialization.Bundle
import com.almasb.fxgl.scene.SubSceneStack

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class CutsceneService : EngineService {

    @Inject("width")
    private var appWidth: Int = 0

    @Inject("height")
    private var appHeight: Int = 0

    @Inject("sceneStack")
    private lateinit var sceneStack: SubSceneStack

    private val scene by lazy { CutsceneScene(sceneStack, appWidth, appHeight) }

    fun startCutscene(cutscene: Cutscene) {
        scene.start(cutscene)
    }

    override fun onMainLoopStarting() {
    }

    override fun onGameReady(vars: PropertyMap) {
    }

    override fun onExit() {
    }

    override fun onUpdate(tpf: Double) {
    }

    override fun write(bundle: Bundle) {
    }

    override fun read(bundle: Bundle) {
    }
}