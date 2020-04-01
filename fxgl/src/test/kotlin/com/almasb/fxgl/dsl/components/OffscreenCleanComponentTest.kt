/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.dsl.components

import com.almasb.fxgl.app.scene.Viewport
import com.almasb.fxgl.entity.Entity
import com.almasb.fxgl.entity.GameWorld
import com.almasb.fxgl.physics.BoundingShape
import com.almasb.fxgl.physics.HitBox
import javafx.util.Duration
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.contains
import org.hamcrest.Matchers.lessThan
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class OffscreenCleanComponentTest {

    private lateinit var viewport: Viewport

    @BeforeEach
    fun setUp() {
        viewport = Viewport(800.0, 600.0)
    }

    @Test
    fun `Entity is removed from world when entity moves offscreen`() {
        val e = Entity()
        e.addComponent(OffscreenCleanComponent(viewport))

        val world = GameWorld()
        world.addEntity(e)

        assertThat(world.entities, contains(e))

        world.onUpdate(0.016)
        assertThat(world.entities, contains(e))

        e.x = -15.0
        world.onUpdate(0.016)

        assertTrue(world.entities.isEmpty())
    }

    @Test
    fun `Entity is removed from world when entity with bbox moves offscreen`() {
        val e = Entity()
        e.addComponent(OffscreenCleanComponent(viewport))
        e.boundingBoxComponent.addHitBox(HitBox(BoundingShape.box(30.0, 30.0)))

        val world = GameWorld()
        world.addEntity(e)

        assertThat(world.entities, contains(e))

        world.onUpdate(0.016)
        assertThat(world.entities, contains(e))

        e.x = -25.0
        world.onUpdate(0.016)
        assertThat(world.entities, contains(e))

        e.x = -30.0
        world.onUpdate(0.016)
        assertThat(world.entities, contains(e))

        e.x = -31.0
        world.onUpdate(0.016)

        assertTrue(world.entities.isEmpty())
    }

    @Test
    fun `Entity is removed from world when viewport moves offscreen`() {
        val e = Entity()
        e.addComponent(OffscreenCleanComponent(viewport))

        val world = GameWorld()
        world.addEntity(e)

        assertThat(world.entities, contains(e))

        world.onUpdate(0.016)
        assertThat(world.entities, contains(e))

        viewport.x = 5.0
        world.onUpdate(0.016)

        assertTrue(world.entities.isEmpty())
    }
}