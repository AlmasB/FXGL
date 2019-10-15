/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.dsl.handlers

import com.almasb.fxgl.entity.Entity
import com.almasb.fxgl.entity.GameWorld
import com.almasb.fxgl.entity.components.CollidableComponent
import com.almasb.fxgl.physics.BoundingShape
import com.almasb.fxgl.physics.HitBox
import com.almasb.fxgl.physics.PhysicsWorld
import com.almasb.fxgl.core.util.Consumer
import javafx.geometry.Point2D
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class CollectibleHandlerTest {

    enum class Type {
        COLLECTOR, COLLECTIBLE
    }

    @Test
    fun `Collectible is collected`() {
        val gameWorld = GameWorld()
        val physicsWorld = PhysicsWorld(600, 50.0)

        var count = 0

        gameWorld.addWorldListener(physicsWorld)

        val collector = Entity()
        collector.type = Type.COLLECTOR
        collector.position = Point2D(100.0, 100.0)
        collector.boundingBoxComponent.addHitBox(HitBox("main", BoundingShape.box(40.0, 40.0)))
        collector.addComponent(CollidableComponent(true))

        val collectible = Entity()
        collectible.type = Type.COLLECTIBLE
        collectible.position = Point2D(100.0, 100.0)
        collectible.boundingBoxComponent.addHitBox(HitBox("main", BoundingShape.box(40.0, 40.0)))
        collectible.addComponent(CollidableComponent(true))

        physicsWorld.addCollisionHandler(CollectibleHandler(Type.COLLECTOR, Type.COLLECTIBLE, "", Consumer {
            assertThat(collectible, `is`(it))
            assertThat(gameWorld.entities, hasItems<Entity>(collectible))
            count++
        }))

        gameWorld.addEntities(collector, collectible)

        assertThat(gameWorld.entities, hasItems<Entity>(collector, collectible))

        gameWorld.onUpdate(0.016)
        physicsWorld.onUpdate(0.016)

        assertThat(count, `is`(1))
        assertThat(gameWorld.entities, hasItems<Entity>(collector))
        assertThat(gameWorld.entities, not(hasItems<Entity>(collectible)))
    }
}