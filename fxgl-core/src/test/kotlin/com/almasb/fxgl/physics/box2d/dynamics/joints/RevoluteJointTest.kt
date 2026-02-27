/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
@file:Suppress("JAVA_MODULE_DOES_NOT_DEPEND_ON_MODULE")
package com.almasb.fxgl.physics.box2d.dynamics.joints

import com.almasb.fxgl.core.math.Vec2
import com.almasb.fxgl.physics.box2d.collision.shapes.CircleShape
import com.almasb.fxgl.physics.box2d.dynamics.*
import com.almasb.fxgl.physics.box2d.dynamics.contacts.Position
import com.almasb.fxgl.physics.box2d.dynamics.contacts.Velocity
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.closeTo
import org.junit.jupiter.api.Test
import java.util.*

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class RevoluteJointTest {

    @Test
    fun `Solve velocity constraints`() {
        val world = World(Vec2(0f, 9.8f))

        val body1 = world.createBody(BodyDef()
                .also {
                    it.type = BodyType.DYNAMIC
                }
        )
        body1.createFixture(CircleShape(1f), 0.2f)

        val body2 = world.createBody(BodyDef()
                .also {
                    it.type = BodyType.DYNAMIC
                }
        )
        body2.createFixture(CircleShape(1f), 0.12f)

        val def = RevoluteJointDef()
        def.enableLimit = true
        def.lowerAngle = 0f
        def.upperAngle = 2f
        def.initialize(body1, body2, Vec2(5f, 0f))

        val joint = world.createJoint(def)

        world.step(0.016f, 8, 3)

        val data = SolverData()
        data.step = TimeStep().also {
            it.dt = 0.5f
            it.inv_dt = 1.0f / it.dt
            it.velocityIterations = 8
            it.positionIterations = 3
            it.dtRatio = it.inv_dt * it.dt
        }

        data.positions = arrayOf(Position().also { it.c.set(1.0f, 0.0f) }, Position().also { it.c.set(8.0f, 0.0f) })
        data.velocities = arrayOf(
                Velocity().also {
                    it.v.set(15.0f, 0.0f)
                    it.w = 2f
                },
                Velocity().also {
                    it.v.set(-13.0f, 0.0f)
                }
        )

        joint.initVelocityConstraints(data)

        joint.solveVelocityConstraints(data)

        assertThat(data.velocities[0].v.x.toDouble(), `is`(closeTo(4.5, 0.01)))
        assertThat(data.velocities[0].v.y.toDouble(), `is`(closeTo(-3.370567E-7, 0.01)))
        assertThat(data.velocities[1].v.x.toDouble(), `is`(closeTo(4.5, 0.01)))
        assertThat(data.velocities[1].v.y.toDouble(), `is`(closeTo(5.6176117E-7, 0.01)))

        assertThat(data.velocities[0].w.toDouble(), `is`(closeTo(1.25, 0.01)))
        assertThat(data.velocities[1].w.toDouble(), `is`(closeTo(1.25, 0.01)))
    }
}