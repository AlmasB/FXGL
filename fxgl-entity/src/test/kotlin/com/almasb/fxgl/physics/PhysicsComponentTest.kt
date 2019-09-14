/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics

import com.almasb.fxgl.core.math.Vec2
import com.almasb.fxgl.entity.Entity
import com.almasb.fxgl.physics.box2d.dynamics.BodyType
import javafx.geometry.Point2D
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.closeTo
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows


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
    fun `Reposition entity with physics`() {
        val c = PhysicsComponent()
        val world = PhysicsWorld(600, 50.0)

        val e = Entity()
        e.boundingBoxComponent.addHitBox(HitBox(BoundingShape.box(10.0, 10.0)))
        e.addComponent(c)

        world.onEntityAdded(e)

        c.overwritePosition(Point2D(100.0, 15.0))

        assertThat(e.position, `is`(Point2D(0.0, 0.0)))

        c.onUpdate(0.016)

        assertThat(e.position, `is`(Point2D(100.0, 15.0)))
    }

    @Test
    fun `Overwrite angle of entity with physics`() {
        val c = PhysicsComponent()
        c.setBodyType(BodyType.DYNAMIC)

        val world = PhysicsWorld(600, 50.0)
        world.setGravity(0.0, 0.0)

        val e = Entity()
        e.boundingBoxComponent.addHitBox(HitBox(BoundingShape.box(10.0, 10.0)))
        e.addComponent(c)

        world.onEntityAdded(e)

        c.setAngularVelocity(15.0)

        world.onUpdate(1.0)
        c.onUpdate(1.0)

        assertThat(e.rotation, closeTo(15.0, 0.5))

        c.overwriteAngle(30.0)

        c.onUpdate(0.016)

        assertThat(e.rotation, closeTo(30.0, 0.5))
    }

    @Test
    fun `Update does not fail if body not inited yet`() {
        val c = PhysicsComponent()
        c.onUpdate(0.016)
    }

    @Test
    fun `On init physics`() {
        var count = 0

        val c = PhysicsComponent()
        c.setOnPhysicsInitialized {
            count++
        }

        val world = PhysicsWorld(600, 50.0)

        val e = Entity()
        e.boundingBoxComponent.addHitBox(HitBox(BoundingShape.box(10.0, 10.0)))
        e.addComponent(c)

        assertThat(count, `is`(0))

        world.onEntityAdded(e)

        assertThat(count, `is`(1))
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

    @Test
    fun `Set linear velocity to dynamic`() {
        val c = PhysicsComponent()
        c.setBodyType(BodyType.DYNAMIC)

        val world = PhysicsWorld(600, 50.0)
        world.setGravity(0.0, 0.0)

        val e = Entity()
        e.boundingBoxComponent.addHitBox(HitBox(BoundingShape.box(10.0, 10.0)))
        e.addComponent(c)

        world.onEntityAdded(e)

        assertThat(e.position, `is`(Point2D(0.0, 0.0)))
        assertFalse(c.isMoving)
        assertFalse(c.isMovingX)
        assertFalse(c.isMovingY)

        c.setLinearVelocity(10.0, 0.0)

        assertTrue(c.isMoving)
        assertTrue(c.isMovingX)
        assertFalse(c.isMovingY)

        world.onUpdate(1.0)
        c.onUpdate(1.0)

        assertThat(e.position, `is`(Point2D(10.0, 0.0)))

        c.setBodyLinearVelocity(Vec2(100.0, 0.0))

        world.onUpdate(1.0)
        c.onUpdate(1.0)

        assertThat(e.position, `is`(Point2D(110.0, 0.0)))

        c.velocityY = 5.0

        assertTrue(c.isMoving)
        assertTrue(c.isMovingX)
        assertTrue(c.isMovingY)

        c.velocityX = 0.0

        assertThat(c.velocityX, `is`(0.0))
        assertThat(c.velocityY, closeTo(5.0, 0.1))

        assertTrue(c.isMoving)
        assertFalse(c.isMovingX)
        assertTrue(c.isMovingY)
    }

    @Test
    fun `Apply linear impulse to dynamic`() {
        val c = PhysicsComponent()
        c.setBodyType(BodyType.DYNAMIC)

        val world = PhysicsWorld(600, 50.0)
        world.setGravity(0.0, 0.0)

        val e = Entity()
        e.boundingBoxComponent.addHitBox(HitBox(BoundingShape.box(10.0, 10.0)))
        e.addComponent(c)

        world.onEntityAdded(e)

        c.applyLinearImpulse(Point2D(50.0, 0.0), Point2D(0.0, 0.0), true)

        world.onUpdate(1.0)
        c.onUpdate(1.0)

        assertThat(e.position, `is`(Point2D(50.0, 0.0)))

        world.onUpdate(1.0)
        c.onUpdate(1.0)

        assertThat(e.position, `is`(Point2D(100.0, 0.0)))
    }

    @Test
    fun `Apply angular velocity to dynamic`() {
        val c = PhysicsComponent()
        c.setBodyType(BodyType.DYNAMIC)

        val world = PhysicsWorld(600, 50.0)
        world.setGravity(0.0, 0.0)

        val e = Entity()
        e.boundingBoxComponent.addHitBox(HitBox(BoundingShape.box(10.0, 10.0)))
        e.addComponent(c)

        world.onEntityAdded(e)

        c.setAngularVelocity(5.0)

        world.onUpdate(1.0)
        c.onUpdate(1.0)

        assertThat(e.rotation, closeTo(5.0, 0.5))
    }
}