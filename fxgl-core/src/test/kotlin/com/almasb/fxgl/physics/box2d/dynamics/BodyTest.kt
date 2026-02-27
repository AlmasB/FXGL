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
class BodyTest {

    @Test
    fun `Body def sets body flags`() {
        val def = BodyDef()
        def.isActive = true
        def.isAllowSleep = true
        def.isBullet = true
        def.isFixedRotation = true
        def.isAwake = true

        val world = World(Vec2())

        val body = Body(def, world)

        assertTrue(body.isActive)
        assertTrue(body.isSleepingAllowed)
        assertTrue(body.isBullet)
        assertTrue(body.isFixedRotation)
        assertTrue(body.isAwake)

        body.isActive = false
        body.isSleepingAllowed = false
        body.isBullet = false
        body.isFixedRotation = false
        body.isAwake = false

        assertFalse(body.isActive)
        assertFalse(body.isSleepingAllowed)
        assertFalse(body.isBullet)
        assertFalse(body.isFixedRotation)
        assertFalse(body.isAwake)
    }
}