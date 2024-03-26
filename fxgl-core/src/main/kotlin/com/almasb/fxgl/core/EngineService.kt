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
     * This is called on a background thread.
     */
    open fun onInit() {}

    /**
     * Called when the engine is fully initialized and just before the main loop.
     * This occurs once per application lifetime.
     * This is called on a JavaFX thread.
     */
    open fun onMainLoopStarting() { }

    /**
     * Called after initGameVars() is completed.
     * This is called on a background thread.
     */
    open fun onVarsInitialized(vars: PropertyMap) { }

    /**
     * Called when initGame(), initPhysics(), initUI() all completed and
     * the game is ready to be played.
     * This is called on a background thread.
     */
    open fun onGameReady(vars: PropertyMap) { }

    /**
     * Called on a JavaFX thread at each engine tick in any scene.
     */
    override fun onUpdate(tpf: Double) { }

    /**
     * Called on a JavaFX thread at each engine tick _only_ in game scene.
     */
    open fun onGameUpdate(tpf: Double) { }

    /**
     * Called on the JavaFX thread after the game scene has been reset,
     * when starting a new game or loading the game from a save file.
     */
    open fun onGameReset() { }

    /**
     * Called just before the engine exits and the application shuts down.
     */
    open fun onExit() { }

    /**
     * Called just before the main loop is paused.
     */
    open fun onMainLoopPausing() { }

    /**
     * Called just after the main loop is resumed.
     */
    open fun onMainLoopResumed() { }

    override fun write(bundle: Bundle) { }

    override fun read(bundle: Bundle) { }
}