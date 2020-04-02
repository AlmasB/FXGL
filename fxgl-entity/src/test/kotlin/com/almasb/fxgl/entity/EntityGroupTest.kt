/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class EntityGroupTest {

    private enum class EntityType {
        T1, T2, T3
    }

    private lateinit var world: GameWorld
    private lateinit var group: EntityGroup

    @BeforeEach
    fun `init`() {
        world = GameWorld()
        group = EntityGroup(world, emptyList(), EntityType.T1, EntityType.T2)
    }

    @Test
    fun `Add`() {
        val e = Entity()
        e.type = EntityType.T1

        world.addEntity(e)

        var count = 0

        group.forEach {
            assertThat(it, `is`(e))
            count++
        }

        assertThat(count, `is`(1))
    }

    @Test
    fun `Remove`() {
        val e = Entity()
        e.type = EntityType.T1

        world.addEntity(e)
        world.removeEntity(e)

        var count = 0

        group.forEach {
            count++
        }

        assertThat(count, `is`(0))
    }

    @Test
    fun `Dispose`() {
        val e = Entity()
        e.type = EntityType.T1

        group.dispose()

        world.addEntity(e)

        var count = 0

        group.forEach {
            count++
        }

        assertThat(count, `is`(0))
    }

    @Test
    fun `Group only contains given entity types`() {
        val e1 = Entity()
        e1.type = EntityType.T1

        val e3 = Entity()
        e3.type = EntityType.T3

        world.addEntities(e1, e3)

        var count = 0

        group.forEach {
            assertThat(it, `is`(e1))
            count++
        }

        assertThat(count, `is`(1))

        world.removeEntities(e1, e3)

        group.forEach {
            count++
        }

        assertThat(count, `is`(1))
    }

    @Test
    fun `Group only contains active entities`() {
        val e1 = Entity()
        e1.type = EntityType.T1

        val e2 = Entity()
        e2.type = EntityType.T2

        world.addEntities(e1, e2)

        var count = 0

        group.forEach {
            count++
        }

        assertThat(count, `is`(2))

        world.removeEntities(e1)

        group.forEach {
            count++
        }

        assertThat(count, `is`(3))
    }
}