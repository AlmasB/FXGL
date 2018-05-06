/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics

import com.almasb.fxgl.app.FXGLMock
import com.almasb.fxgl.core.math.Vec2
import com.almasb.fxgl.entity.Entities
import com.almasb.fxgl.entity.Entity
import com.almasb.fxgl.entity.GameWorld
import com.almasb.fxgl.entity.components.CollidableComponent
import javafx.geometry.Point2D
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class PhysicsWorldTest {

    companion object {
        @BeforeAll
        @JvmStatic fun before() {
            FXGLMock.mock()
        }
    }

    private enum class EntityType {
        TYPE1, TYPE2
    }

    private val physicsWorld = PhysicsWorld(600, 50.0)

    @Test
    fun `Jbox world`() {
        assertThat(physicsWorld.jBox2DWorld.bodyCount, `is`(0))
    }

    @Test
    fun `Pixels to meters`() {
        assertThat(physicsWorld.toMeters(100.0), `is`(2.0))
        assertThat(physicsWorld.toMetersF(100.0), `is`(2.0f))
        assertThat(physicsWorld.toPoint(Point2D(100.0, 50.0)), `is`(Vec2(2.0, 11.0)))
        assertThat(physicsWorld.toVector(Point2D(100.0, 50.0)), `is`(Vec2(2.0, -1.0)))
    }

    @Test
    fun `Meters to pixels`() {
        assertThat(physicsWorld.toPixels(2.0), `is`(100.0))
        assertThat(physicsWorld.toPixelsF(2.0), `is`(100.0f))
        assertThat(physicsWorld.toPoint(Vec2(2.0, 11.0)), `is`(Point2D(100.0, 50.0)))
        assertThat(physicsWorld.toVector(Vec2(2.0, -1.0)), `is`(Point2D(100.0, 50.0)))
    }

    @Test
    fun `Gravity`() {
        physicsWorld.setGravity(50.0, 10.0)

        assertThat(physicsWorld.jBox2DWorld.gravity, `is`(Vec2(1.0, -0.2)))
    }

    @Test
    fun `Collision notification`() {
        val entity1 = Entities.builder()
                .type(EntityType.TYPE1)
                .at(100.0, 100.0)
                .bbox(HitBox("Test1", BoundingShape.box(40.0, 40.0)))
                .with(CollidableComponent(true))
                .build()

        val entity2 = Entities.builder()
                .type(EntityType.TYPE2)
                .at(150.0, 100.0)
                .bbox(HitBox("Test2", BoundingShape.box(40.0, 40.0)))
                .with(CollidableComponent(true))
                .build()

        // entities that are not part of any world are not active
        // so we add them to _some_ world
        val gameWorld = GameWorld()
        gameWorld.addEntity(entity1)
        gameWorld.addEntity(entity2)

        var hitboxCount = 0
        var collisionBeginCount = 0
        var collisionCount = 0
        var collisionEndCount = 0

        val handler = object : CollisionHandler(EntityType.TYPE1, EntityType.TYPE2) {

            override fun onHitBoxTrigger(a: Entity, b: Entity, boxA: HitBox, boxB: HitBox) {
                assertTrue(a === entity1)
                assertTrue(b === entity2)

                assertThat(boxA.name, `is`("Test1"))
                assertThat(boxB.name, `is`("Test2"))

                hitboxCount++
            }

            override fun onCollisionBegin(a: Entity, b: Entity) {
                assertTrue(a === entity1)
                assertTrue(b === entity2)
                collisionBeginCount++
            }

            override fun onCollision(a: Entity, b: Entity) {
                assertTrue(a === entity1)
                assertTrue(b === entity2)
                collisionCount++
            }

            override fun onCollisionEnd(a: Entity, b: Entity) {
                assertTrue(a === entity1)
                assertTrue(b === entity2)
                collisionEndCount++
            }
        }

        physicsWorld.addCollisionHandler(handler)

        physicsWorld.onEntityAdded(entity1)
        physicsWorld.onEntityAdded(entity2)
        physicsWorld.onUpdate(0.016)

        // no collision happened, entities are apart
        assertThat(hitboxCount, `is`(0))
        assertThat(collisionBeginCount, `is`(0))
        assertThat(collisionCount, `is`(0))
        assertThat(collisionEndCount, `is`(0))

        // move 2nd entity closer to first, colliding with it
        entity2.translateX(-30.0)

        physicsWorld.onUpdate(0.016)

        // hit box and collision begin triggered, entities are now colliding
        assertThat(hitboxCount, `is`(1))
        assertThat(collisionBeginCount, `is`(1))
        assertThat(collisionCount, `is`(1))
        assertThat(collisionEndCount, `is`(0))

        physicsWorld.onUpdate(0.016)

        // collision continues
        assertThat(hitboxCount, `is`(1))
        assertThat(collisionBeginCount, `is`(1))
        assertThat(collisionCount, `is`(2))
        assertThat(collisionEndCount, `is`(0))

        physicsWorld.onUpdate(0.016)

        // collision continues
        assertThat(hitboxCount, `is`(1))
        assertThat(collisionBeginCount, `is`(1))
        assertThat(collisionCount, `is`(3))
        assertThat(collisionEndCount, `is`(0))

        // move 2nd entity away from 1st
        entity2.translateX(30.0)

        physicsWorld.onUpdate(0.016)

        // collision end
        assertThat(hitboxCount, `is`(1))
        assertThat(collisionBeginCount, `is`(1))
        assertThat(collisionCount, `is`(3))
        assertThat(collisionEndCount, `is`(1))

        physicsWorld.removeCollisionHandler(handler)

        // move 2nd entity closer to 1st, colliding with it
        entity2.translateX(-30.0)

        physicsWorld.onUpdate(0.016)

        // no change in collision
        assertThat(hitboxCount, `is`(1))
        assertThat(collisionBeginCount, `is`(1))
        assertThat(collisionCount, `is`(3))
        assertThat(collisionEndCount, `is`(1))
    }
}