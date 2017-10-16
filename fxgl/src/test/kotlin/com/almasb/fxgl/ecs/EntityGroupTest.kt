/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ecs

import com.almasb.fxgl.core.collection.Predicate
import com.almasb.fxgl.entity.component.TypeComponent
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.function.Consumer

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class EntityGroupTest {

    private enum class EntityType {
        T1, T2
    }

    private lateinit var world: GameWorld
    private lateinit var group: EntityGroup<Entity>

    @BeforeEach
    fun `init`() {
        world = GameWorld()
        group = EntityGroup(world, emptyList(), EntityType.T1, EntityType.T2)
    }

    @Test
    fun `Add`() {
        val e = Entity()
        e.addComponent(TypeComponent(EntityType.T1))

        world.addEntity(e)

        var count = 0

        group.forEach(Predicate { true }, Consumer {
            assertThat(it, `is`(e))
            count++
        })

        assertThat(count, `is`(1))
    }

    @Test
    fun `Remove`() {
        val e = Entity()
        e.addComponent(TypeComponent(EntityType.T1))

        world.addEntity(e)
        world.removeEntity(e)

        var count = 0

        group.forEach(Consumer {
            count++
        })

        assertThat(count, `is`(0))
    }

    @Test
    fun `Dispose`() {
        val e = Entity()
        e.addComponent(TypeComponent(EntityType.T1))

        group.dispose()

        world.addEntity(e)

        var count = 0

        group.forEach(Consumer {
            count++
        })

        assertThat(count, `is`(0))
    }
}