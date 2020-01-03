/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics

import com.almasb.fxgl.core.math.Vec2
import com.almasb.fxgl.entity.Entity
import com.almasb.fxgl.entity.GameWorld
import com.almasb.fxgl.entity.components.CollidableComponent
import com.almasb.fxgl.physics.box2d.dynamics.BodyType
import javafx.geometry.Point2D
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.closeTo
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class PhysicsWorldTest {

    private enum class EntityType {
        TYPE1, TYPE2
    }

    private lateinit var physicsWorld: PhysicsWorld

    @BeforeEach
    fun setUp() {
        physicsWorld = PhysicsWorld(600, 50.0)
    }

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
    fun `Entity addition and removal updates jbox2d world`() {
        val e1 = Entity().also {
            it.addComponent(PhysicsComponent())
        }

        val e2 = Entity().also {
            it.addComponent(PhysicsComponent())
        }

        val gameWorld = GameWorld()
        gameWorld.addEntities(e1, e2)

        assertThat(physicsWorld.jBox2DWorld.bodies.size(), `is`(0))

        physicsWorld.onEntityAdded(e1)
        physicsWorld.onEntityAdded(e2)

        assertThat(physicsWorld.jBox2DWorld.bodies.size(), `is`(2))

        physicsWorld.onEntityRemoved(e1)
        physicsWorld.onEntityRemoved(e2)

        assertThat(physicsWorld.jBox2DWorld.bodies.size(), `is`(0))
    }

    // onEntityRemoved should be called from the game world
    @Test
    fun `Clear does not remove jbox2d bodies`() {
        val e1 = Entity().also {
            it.addComponent(PhysicsComponent())
        }

        physicsWorld.onEntityAdded(e1)

        physicsWorld.clear()

        assertThat(physicsWorld.jBox2DWorld.bodies.size(), `is`(1))
    }

    @Test
    fun `Making entity not collidable mid-collision correctly triggers onCollisionEnd`() {
        val e1 = Entity()
        e1.type = EntityType.TYPE1
        e1.position = Point2D(100.0, 100.0)
        e1.boundingBoxComponent.addHitBox(HitBox("Test1", BoundingShape.box(40.0, 40.0)))
        e1.addComponent(CollidableComponent(true))

        val e2 = Entity()
        e2.type = EntityType.TYPE2
        e2.position = Point2D(150.0, 100.0)
        e2.boundingBoxComponent.addHitBox(HitBox("Test2", BoundingShape.box(40.0, 40.0)))
        e2.addComponent(CollidableComponent(true))

        // entities that are not part of any world are not active
        // so we add them to _some_ world
        val gameWorld = GameWorld()
        gameWorld.addEntity(e1)
        gameWorld.addEntity(e2)

        var hitboxCount = 0
        var collisionBeginCount = 0
        var collisionCount = 0
        var collisionEndCount = 0

        val handler = object : CollisionHandler(EntityType.TYPE1, EntityType.TYPE2) {

            override fun onHitBoxTrigger(a: Entity, b: Entity, boxA: HitBox, boxB: HitBox) {
                assertTrue(a === e1)
                assertTrue(b === e2)

                assertThat(boxA.name, `is`("Test1"))
                assertThat(boxB.name, `is`("Test2"))

                hitboxCount++
            }

            override fun onCollisionBegin(a: Entity, b: Entity) {
                assertTrue(a === e1)
                assertTrue(b === e2)
                collisionBeginCount++
            }

            override fun onCollision(a: Entity, b: Entity) {
                assertTrue(a === e1)
                assertTrue(b === e2)
                collisionCount++
            }

            override fun onCollisionEnd(a: Entity, b: Entity) {
                assertTrue(a === e1)
                assertTrue(b === e2)
                collisionEndCount++
            }
        }

        physicsWorld.addCollisionHandler(handler)

        physicsWorld.onEntityAdded(e1)
        physicsWorld.onEntityAdded(e2)
        physicsWorld.onUpdate(0.016)

        // no collision happened, entities are apart
        assertThat(hitboxCount, `is`(0))
        assertThat(collisionBeginCount, `is`(0))
        assertThat(collisionCount, `is`(0))
        assertThat(collisionEndCount, `is`(0))

        // move 2nd entity closer to first, colliding with it
        e2.translateX(-30.0)

        physicsWorld.onUpdate(0.016)

        // hit box and collision begin triggered, entities are now colliding
        assertThat(hitboxCount, `is`(1))
        assertThat(collisionBeginCount, `is`(1))
        assertThat(collisionCount, `is`(1))
        assertThat(collisionEndCount, `is`(0))

        // remove collidable component, e2 is now not collidable but we should
        // correctly notify that collision ended
        e2.removeComponent(CollidableComponent::class.java)

        physicsWorld.onUpdate(0.016)

        assertThat(hitboxCount, `is`(1))
        assertThat(collisionBeginCount, `is`(1))
        assertThat(collisionCount, `is`(1))
        assertThat(collisionEndCount, `is`(1))
    }

    @Test
    fun `Collision notification`() {
        val e1 = Entity()
        e1.type = EntityType.TYPE1
        e1.position = Point2D(100.0, 100.0)
        e1.boundingBoxComponent.addHitBox(HitBox("Test1", BoundingShape.box(40.0, 40.0)))
        e1.addComponent(CollidableComponent(true))

        val e2 = Entity()
        e2.type = EntityType.TYPE2
        e2.position = Point2D(150.0, 100.0)
        e2.boundingBoxComponent.addHitBox(HitBox("Test2", BoundingShape.box(40.0, 40.0)))
        e2.addComponent(CollidableComponent(true))

        // entities that are not part of any world are not active
        // so we add them to _some_ world
        val gameWorld = GameWorld()
        gameWorld.addEntity(e1)
        gameWorld.addEntity(e2)

        var hitboxCount = 0
        var collisionBeginCount = 0
        var collisionCount = 0
        var collisionEndCount = 0

        val handler = object : CollisionHandler(EntityType.TYPE1, EntityType.TYPE2) {

            override fun onHitBoxTrigger(a: Entity, b: Entity, boxA: HitBox, boxB: HitBox) {
                assertTrue(a === e1)
                assertTrue(b === e2)

                assertThat(boxA.name, `is`("Test1"))
                assertThat(boxB.name, `is`("Test2"))

                hitboxCount++
            }

            override fun onCollisionBegin(a: Entity, b: Entity) {
                assertTrue(a === e1)
                assertTrue(b === e2)
                collisionBeginCount++
            }

            override fun onCollision(a: Entity, b: Entity) {
                assertTrue(a === e1)
                assertTrue(b === e2)
                collisionCount++
            }

            override fun onCollisionEnd(a: Entity, b: Entity) {
                assertTrue(a === e1)
                assertTrue(b === e2)
                collisionEndCount++
            }
        }

        physicsWorld.addCollisionHandler(handler)

        physicsWorld.onEntityAdded(e1)
        physicsWorld.onEntityAdded(e2)
        physicsWorld.onUpdate(0.016)

        // no collision happened, entities are apart
        assertThat(hitboxCount, `is`(0))
        assertThat(collisionBeginCount, `is`(0))
        assertThat(collisionCount, `is`(0))
        assertThat(collisionEndCount, `is`(0))

        // move 2nd entity closer to first, colliding with it
        e2.translateX(-30.0)

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
        e2.translateX(30.0)

        physicsWorld.onUpdate(0.016)

        // collision end
        assertThat(hitboxCount, `is`(1))
        assertThat(collisionBeginCount, `is`(1))
        assertThat(collisionCount, `is`(3))
        assertThat(collisionEndCount, `is`(1))

        physicsWorld.removeCollisionHandler(handler)

        // move 2nd entity closer to 1st, colliding with it
        e2.translateX(-30.0)

        physicsWorld.onUpdate(0.016)

        // no change in collision
        assertThat(hitboxCount, `is`(1))
        assertThat(collisionBeginCount, `is`(1))
        assertThat(collisionCount, `is`(3))
        assertThat(collisionEndCount, `is`(1))
    }

    @Test
    fun `Collision notification with physics components`() {
        val e1 = Entity()
        e1.type = EntityType.TYPE1
        e1.position = Point2D(100.0, 100.0)
        e1.boundingBoxComponent.addHitBox(HitBox("Test1", BoundingShape.box(40.0, 40.0)))
        e1.addComponent(CollidableComponent(true))
        e1.addComponent(PhysicsComponent())

        val c2 = PhysicsComponent()
        c2.setBodyType(BodyType.DYNAMIC)

        val e2 = Entity()
        e2.type = EntityType.TYPE2
        e2.position = Point2D(150.0, 100.0)
        e2.boundingBoxComponent.addHitBox(HitBox("Test2", BoundingShape.box(40.0, 40.0)))
        e2.addComponent(CollidableComponent(true))
        e2.addComponent(c2)

        // entities that are not part of any world are not active
        // so we add them to _some_ world
        val gameWorld = GameWorld()
        gameWorld.addEntity(e1)
        gameWorld.addEntity(e2)

        var hitboxCount = 0
        var collisionBeginCount = 0
        var collisionCount = 0
        var collisionEndCount = 0

        val handler = object : CollisionHandler(EntityType.TYPE1, EntityType.TYPE2) {

            override fun onHitBoxTrigger(a: Entity, b: Entity, boxA: HitBox, boxB: HitBox) {
                assertTrue(a === e1)
                assertTrue(b === e2)

                assertThat(boxA.name, `is`("Test1"))
                assertThat(boxB.name, `is`("Test2"))

                hitboxCount++
            }

            override fun onCollisionBegin(a: Entity, b: Entity) {
                assertTrue(a === e1)
                assertTrue(b === e2)
                collisionBeginCount++
            }

            override fun onCollision(a: Entity, b: Entity) {
                assertTrue(a === e1)
                assertTrue(b === e2)
                collisionCount++
            }

            override fun onCollisionEnd(a: Entity, b: Entity) {
                assertTrue(a === e1)
                assertTrue(b === e2)
                collisionEndCount++
            }
        }

        physicsWorld.setGravity(0.0, 0.0)
        physicsWorld.addCollisionHandler(handler)

        physicsWorld.onEntityAdded(e1)
        physicsWorld.onEntityAdded(e2)
        physicsWorld.onUpdate(0.016)

        // no collision happened, entities are apart
        assertThat(hitboxCount, `is`(0))
        assertThat(collisionBeginCount, `is`(0))
        assertThat(collisionCount, `is`(0))
        assertThat(collisionEndCount, `is`(0))

        // move 2nd entity closer to first, colliding with it
        c2.velocityX = (-30.0) * 60

        physicsWorld.onUpdate(0.016)
        c2.onUpdate(0.016)

        // hit box and collision begin triggered, entities are now colliding
        assertThat(hitboxCount, `is`(1))
        assertThat(collisionBeginCount, `is`(1))
        assertThat(collisionCount, `is`(1))
        assertThat(collisionEndCount, `is`(0))

        physicsWorld.onUpdate(0.016)
        c2.onUpdate(0.016)

        // collision continues
        assertThat(hitboxCount, `is`(1))
        assertThat(collisionBeginCount, `is`(1))
        assertThat(collisionCount, `is`(2))
        assertThat(collisionEndCount, `is`(0))

        physicsWorld.onUpdate(0.016)
        c2.onUpdate(0.016)

        // collision continues
        assertThat(hitboxCount, `is`(1))
        assertThat(collisionBeginCount, `is`(1))
        assertThat(collisionCount, `is`(3))
        assertThat(collisionEndCount, `is`(0))

        // move 2nd entity away from 1st
        c2.overwritePosition(Point2D(150.0, 100.0))

        physicsWorld.onUpdate(0.016)
        c2.onUpdate(0.016)

        // collision end
        assertThat(hitboxCount, `is`(1))
        assertThat(collisionBeginCount, `is`(1))
        assertThat(collisionCount, `is`(3))
        assertThat(collisionEndCount, `is`(1))

        physicsWorld.removeCollisionHandler(handler)

        // move 2nd entity closer to 1st, colliding with it
        c2.velocityX = (-30.0) * 60

        physicsWorld.onUpdate(0.016)
        c2.onUpdate(0.016)

        // no change in collision
        assertThat(hitboxCount, `is`(1))
        assertThat(collisionBeginCount, `is`(1))
        assertThat(collisionCount, `is`(3))
        assertThat(collisionEndCount, `is`(1))
    }

    @Test
    fun `Sensor collisions`() {
        val e1 = Entity()
        e1.type = EntityType.TYPE1
        e1.position = Point2D(100.0, 100.0)
        e1.boundingBoxComponent.addHitBox(HitBox("Test1", BoundingShape.box(40.0, 40.0)))
        e1.addComponent(CollidableComponent(true))
        e1.addComponent(PhysicsComponent())

        val c2 = PhysicsComponent()
        c2.setBodyType(BodyType.DYNAMIC)

        val e2 = Entity()
        e2.type = EntityType.TYPE2
        e2.position = Point2D(150.0, 100.0)
        e2.boundingBoxComponent.addHitBox(HitBox("Test2", BoundingShape.box(40.0, 40.0)))
        e2.addComponent(CollidableComponent(true))
        e2.addComponent(c2)

        // entities that are not part of any world are not active
        // so we add them to _some_ world
        val gameWorld = GameWorld()
        gameWorld.addEntity(e1)
        gameWorld.addEntity(e2)

        var hitboxCount = 0
        var collisionBeginCount = 0
        var collisionCount = 0
        var collisionEndCount = 0

        val handler = object : CollisionHandler(EntityType.TYPE1, EntityType.TYPE2) {

            override fun onHitBoxTrigger(a: Entity, b: Entity, boxA: HitBox, boxB: HitBox) {
                assertTrue(a === e1)
                assertTrue(b === e2)

                assertThat(boxA.name, `is`("Test1"))
                assertThat(boxB.name, `is`("Test2"))

                hitboxCount++
            }

            override fun onCollisionBegin(a: Entity, b: Entity) {
                assertTrue(a === e1)
                assertTrue(b === e2)
                collisionBeginCount++
            }

            override fun onCollision(a: Entity, b: Entity) {
                assertTrue(a === e1)
                assertTrue(b === e2)
                collisionCount++
            }

            override fun onCollisionEnd(a: Entity, b: Entity) {
                assertTrue(a === e1)
                assertTrue(b === e2)
                collisionEndCount++
            }
        }

        var sensorCount = 0

        val box1 = HitBox(Point2D(-50.0, 0.0), BoundingShape.box(50.0, 20.0))

        c2.addSensor(box1, object : SensorCollisionHandler() {
            override fun onCollisionBegin(other: Entity) {
                assertThat(other, `is`(e1))
                sensorCount++
            }

            override fun onCollisionEnd(other: Entity) {
                assertThat(other, `is`(e1))
                sensorCount--
            }
        })

        physicsWorld.setGravity(0.0, 0.0)
        physicsWorld.addCollisionHandler(handler)

        physicsWorld.onEntityAdded(e1)
        physicsWorld.onEntityAdded(e2)
        physicsWorld.onUpdate(0.016)

        // no collision happened, entities are apart
        assertThat(hitboxCount, `is`(0))
        assertThat(collisionBeginCount, `is`(0))
        assertThat(collisionCount, `is`(0))
        assertThat(collisionEndCount, `is`(0))

        // move 2nd entity closer to first, colliding with it
        c2.velocityX = (-30.0) * 20

        physicsWorld.onUpdate(0.016)
        c2.onUpdate(0.016)

        // no collision happened, entities are apart, but sensor should be triggered
        assertThat(hitboxCount, `is`(0))
        assertThat(collisionBeginCount, `is`(0))
        assertThat(collisionCount, `is`(0))
        assertThat(collisionEndCount, `is`(0))
        assertThat(sensorCount, `is`(1))

        physicsWorld.onUpdate(0.016)
        c2.onUpdate(0.016)

        // hit box and collision begin triggered, entities are now colliding
        assertThat(hitboxCount, `is`(1))
        assertThat(collisionBeginCount, `is`(1))
        assertThat(collisionCount, `is`(1))
        assertThat(collisionEndCount, `is`(0))

        // sensor still at 1
        assertThat(sensorCount, `is`(1))

        physicsWorld.onUpdate(0.016)
        c2.onUpdate(0.016)

        // collision continues
        assertThat(hitboxCount, `is`(1))
        assertThat(collisionBeginCount, `is`(1))
        assertThat(collisionCount, `is`(2))
        assertThat(collisionEndCount, `is`(0))

        physicsWorld.onUpdate(0.016)
        c2.onUpdate(0.016)

        // collision continues
        assertThat(hitboxCount, `is`(1))
        assertThat(collisionBeginCount, `is`(1))
        assertThat(collisionCount, `is`(3))
        assertThat(collisionEndCount, `is`(0))

        // sensor still at 1
        assertThat(sensorCount, `is`(1))

        // move 2nd entity away from 1st
        c2.velocityX = (30.0) * 60

        physicsWorld.onUpdate(0.5)
        c2.onUpdate(0.5)

        // last frame when colliding, so collision still continues
        assertThat(hitboxCount, `is`(1))
        assertThat(collisionBeginCount, `is`(1))
        assertThat(collisionCount, `is`(4))
        assertThat(collisionEndCount, `is`(0))

        assertThat(sensorCount, `is`(1))

        physicsWorld.onUpdate(0.016)
        c2.onUpdate(0.016)

        // collision end
        assertThat(hitboxCount, `is`(1))
        assertThat(collisionBeginCount, `is`(1))
        assertThat(collisionCount, `is`(4))
        assertThat(collisionEndCount, `is`(1))

        // sensor collision also end
        assertThat(sensorCount, `is`(0))

        c2.removeSensor(box1)

        // move 2nd entity closer to 1st, colliding with it
        c2.velocityX = (-30.0) * 60

        physicsWorld.onUpdate(0.016)
        c2.onUpdate(0.016)

        // no change in sensor
        assertThat(sensorCount, `is`(0))
    }

    @Test
    fun `Raycast`() {
        val e1 = Entity()
        e1.position = Point2D(100.0, 100.0)
        e1.boundingBoxComponent.addHitBox(HitBox("Test1", BoundingShape.box(40.0, 40.0)))
        e1.addComponent(PhysicsComponent())

        val e2 = Entity()
        e2.position = Point2D(200.0, 100.0)
        e2.boundingBoxComponent.addHitBox(HitBox("Test2", BoundingShape.box(40.0, 40.0)))
        e2.addComponent(PhysicsComponent())

        val e3 = Entity()
        e3.position = Point2D(300.0, 100.0)
        e3.boundingBoxComponent.addHitBox(HitBox("Test2", BoundingShape.box(40.0, 40.0)))
        e3.addComponent(PhysicsComponent())

        // entities that are not part of any world are not active
        // so we add them to _some_ world
        val gameWorld = GameWorld()
        gameWorld.addWorldListener(physicsWorld)

        gameWorld.addEntity(e1)
        gameWorld.addEntity(e2)
        gameWorld.addEntity(e3)

        // -->   X    Y     Z

        var result = physicsWorld.raycast(Point2D(0.0, 120.0), Point2D(500.0, 120.0))

        assertThat(result.entity.get(), `is`(e1))
        assertThat(result.point.get().x, closeTo(100.0, 1.0))
        assertThat(result.point.get().y, closeTo(120.0, 1.0))

        //    X  -->  Y     Z

        result = physicsWorld.raycast(Point2D(150.0, 120.0), Point2D(500.0, 120.0))

        assertThat(result.entity.get(), `is`(e2))
        assertThat(result.point.get().x, closeTo(200.0, 1.0))
        assertThat(result.point.get().y, closeTo(120.0, 1.0))

        //    X    Y  -->   Z

        result = physicsWorld.raycast(Point2D(250.0, 120.0), Point2D(500.0, 120.0))

        assertThat(result.entity.get(), `is`(e3))
        assertThat(result.point.get().x, closeTo(300.0, 1.0))
        assertThat(result.point.get().y, closeTo(120.0, 1.0))

        // ignore Z:     X    Y  -->   (Z)
        e3.getComponent(PhysicsComponent::class.java).isRaycastIgnored = true

        result = physicsWorld.raycast(Point2D(250.0, 120.0), Point2D(500.0, 120.0))

        assertFalse(result.entity.isPresent)
        assertFalse(result.point.isPresent)

        //    X    Y     Z  -->

        result = physicsWorld.raycast(Point2D(450.0, 120.0), Point2D(500.0, 120.0))

        assertFalse(result.entity.isPresent)
        assertFalse(result.point.isPresent)
    }
}