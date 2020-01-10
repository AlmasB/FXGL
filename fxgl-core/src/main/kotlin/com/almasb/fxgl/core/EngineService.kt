/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core

import com.almasb.fxgl.core.collection.PropertyMap
import com.almasb.fxgl.core.serialization.Bundle
import com.almasb.fxgl.core.serialization.SerializableType

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
abstract class EngineService : Updatable, SerializableType {

    /**
     * Called during the engine initialization phase, after
     * all services were added and dependencies marked with [Inject] injected.
     */
    open fun onInit() {}

    /**
     * Called when the engine is fully initialized and just before the main loop.
     * This occurs once per application lifetime.
     */
    open fun onMainLoopStarting() { }

    /**
     * Called when initGame(), initPhysics(), initUI() all completed and
     * the game is ready to be played.
     */
    open fun onGameReady(vars: PropertyMap) { }

    override fun onUpdate(tpf: Double) { }

    /**
     * Called just before the engine exits and the application shuts down.
     */
    open fun onExit() { }

    override fun write(bundle: Bundle) { }

    override fun read(bundle: Bundle) { }
}