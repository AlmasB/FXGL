/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics.handler

import com.almasb.fxgl.ecs.Entity
import com.almasb.fxgl.ecs.GameWorld
import com.almasb.fxgl.entity.component.BoundingBoxComponent
import com.almasb.fxgl.entity.component.CollidableComponent
import com.almasb.fxgl.entity.component.PositionComponent
import com.almasb.fxgl.entity.component.TypeComponent
import com.almasb.fxgl.physics.BoundingShape
import com.almasb.fxgl.physics.HitBox
import com.almasb.fxgl.physics.PhysicsWorld
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.*
import org.junit.jupiter.api.Test
import java.util.function.Consumer

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class CollectibleHandlerTest {

    enum class Type {
        COLLECTOR, COLLECTIBLE
    }

    @Test
    fun `Collectible is collected`() {
        val gameWorld = GameWorld(2)
        val physicsWorld = PhysicsWorld(600, 50.0)

        var count = 0

        gameWorld.addWorldListener(physicsWorld)

        val collector = Entity()
        collector.addComponent(TypeComponent(Type.COLLECTOR))
        collector.addComponent(PositionComponent(100.0, 100.0))
        collector.addComponent(BoundingBoxComponent(HitBox("main", BoundingShape.box(40.0, 40.0))))
        collector.addComponent(CollidableComponent(true))

        val collectible = Entity()
        collectible.addComponent(TypeComponent(Type.COLLECTIBLE))
        collectible.addComponent(PositionComponent(100.0, 100.0))
        collectible.addComponent(BoundingBoxComponent(HitBox("main", BoundingShape.box(40.0, 40.0))))
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