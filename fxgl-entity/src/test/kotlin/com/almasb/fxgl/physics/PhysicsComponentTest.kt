/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics

import com.almasb.fxgl.entity.Entity
import com.almasb.fxgl.physics.box2d.dynamics.BodyType
import javafx.geometry.Point2D
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.util.stream.Stream
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.lang.IllegalStateException


/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class PhysicsComponentTest {

    @Test
    fun `Get body throws if accessing before physics ready`() {
        assertThrows<IllegalStateException> {
            val c = PhysicsComponent()
            c.getBody()
        }
    }

    @Test
    fun `Body`() {
        val c = PhysicsComponent()
        assertNull(c.body)

        val world = PhysicsWorld(600, 50.0)

        val e = Entity()
        e.boundingBoxComponent.addHitBox(HitBox(BoundingShape.box(10.0, 10.0)))
        e.addComponent(c)

        world.onEntityAdded(e)
        assertNotNull(c.body)
    }

    @Test
    fun `Apply force to dynamic`() {
        val c = PhysicsComponent()
        c.setBodyType(BodyType.DYNAMIC)

        val world = PhysicsWorld(600, 50.0)
        world.setGravity(0.0, 0.0)

        val e = Entity()
        e.boundingBoxComponent.addHitBox(HitBox(BoundingShape.box(10.0, 10.0)))
        e.addComponent(c)

        world.onEntityAdded(e)

        assertThat(e.position, `is`(Point2D(0.0, 0.0)))

        c.applyForceToCenter(Point2D(100.0, 0.0))

        world.onUpdate(1.0)
        c.onUpdate(1.0)

        assertThat(e.position, `is`(Point2D(100.0, 0.0)))
    }
}