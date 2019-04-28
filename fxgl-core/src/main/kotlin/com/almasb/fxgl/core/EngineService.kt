/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core

import com.almasb.fxgl.core.serialization.SerializableType

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
interface EngineService : Updatable, SerializableType {

    /**
     * Called when the engine is fully initialized and just before the main loop.
     */
    fun onMainLoopStarting()

    /**
     * Called just before the engine exits and the application shuts down.
     */
    fun onExit()
}