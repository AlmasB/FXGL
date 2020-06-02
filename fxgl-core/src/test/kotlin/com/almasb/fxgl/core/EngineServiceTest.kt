/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core

import com.almasb.fxgl.core.collection.PropertyMap
import com.almasb.fxgl.core.serialization.Bundle
import org.junit.jupiter.api.Test

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class EngineServiceTest {

    @Test
    fun `Default methods are noop`() {
        val engine = object : EngineService() { }

        engine.onInit()
        engine.onMainLoopStarting()
        engine.onGameReady(PropertyMap())
        engine.onUpdate(1.0)
        engine.onGameUpdate(1.0)
        engine.onExit()
        engine.write(Bundle("test"))
        engine.read(Bundle("test"))
    }
}