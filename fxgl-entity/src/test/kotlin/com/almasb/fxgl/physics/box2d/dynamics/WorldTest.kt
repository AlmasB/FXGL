/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics.box2d.dynamics

import com.almasb.fxgl.core.math.Vec2
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class WorldTest {

    @Test
    fun `broadphase is not null`() {
        val world = World(Vec2())

        assertNotNull(world.contactManager.broadPhase)
    }
}