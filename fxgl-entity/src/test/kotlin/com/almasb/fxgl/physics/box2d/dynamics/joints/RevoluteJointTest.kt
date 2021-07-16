/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
@file:Suppress("JAVA_MODULE_DOES_NOT_DEPEND_ON_MODULE")
package com.almasb.fxgl.physics.box2d.dynamics.joints

import com.almasb.fxgl.core.math.Vec2
import com.almasb.fxgl.physics.box2d.dynamics.SolverData
import com.almasb.fxgl.physics.box2d.dynamics.World
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class RevoluteJointTest {

    @Test
    fun `Solve position constraints`() {
        val def = RevoluteJointDef()

        val world = World(Vec2())

        // TODO:
        val joint = RevoluteJoint(world.pool, def)

        // TODO:
        val data = SolverData()

        //joint.solvePositionConstraints(data)
    }
}