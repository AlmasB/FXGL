/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics.box2d.dynamics

import com.almasb.fxgl.core.math.Vec2
import com.almasb.fxgl.physics.box2d.dynamics.joints.RevoluteJointDef
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.*
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

    @Test
    fun `joint count`() {
        val world = World(Vec2())

        val jointDef = RevoluteJointDef()
        jointDef.initialize(Body(BodyDef(), world), Body(BodyDef(), world), Vec2())

        val joint = world.createJoint(jointDef)

        assertThat(world.jointCount, `is`(1))

        world.destroyJoint(joint)

        assertThat(world.jointCount, `is`(0))
    }
}