/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core

import com.almasb.fxgl.core.collection.PropertyMap
import com.almasb.fxgl.core.serialization.SerializableType

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
interface EngineService : Updatable, SerializableType {

    /**
     * Called when the engine is fully initialized and just before the main loop.
     * This occurs once per application lifetime.
     */
    fun onMainLoopStarting()

    /**
     * Called when initGame(), initPhysics(), initUI() all completed and
     * the game is ready to be played.
     */
    fun onGameReady(vars: PropertyMap)

    /**
     * Called just before the engine exits and the application shuts down.
     */
    fun onExit()
}