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
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.contains
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * @author Johan Dykström
 */
class OffscreenInvisibleComponentTest {

    private lateinit var viewport: Viewport

    @BeforeEach
    fun setUp() {
        viewport = Viewport(800.0, 600.0)
    }

    @Test
    fun `Entity is hidden when entity moves offscreen`() {
        val e = Entity()
        e.addComponent(OffscreenInvisibleComponent(viewport))

        val world = GameWorld()
        world.addEntity(e)
        assertThat(world.entities, contains(e))
        assertTrue(e.isVisible)

        world.onUpdate(0.016)
        assertThat(world.entities, contains(e))
        assertTrue(e.isVisible)

        e.x = -15.0
        world.onUpdate(0.016)
        // World still contains entity, but it is invisible
        assertThat(world.entities, contains(e))
        assertFalse(e.isVisible)
    }

    @Test
    fun `Entity is hidden when entity with bbox moves offscreen`() {
        val e = Entity()
        e.addComponent(OffscreenInvisibleComponent(viewport))
        e.boundingBoxComponent.addHitBox(HitBox(BoundingShape.box(30.0, 30.0)))

        val world = GameWorld()
        world.addEntity(e)
        assertThat(world.entities, contains(e))
        assertTrue(e.isVisible)

        world.onUpdate(0.016)
        assertThat(world.entities, contains(e))
        assertTrue(e.isVisible)

        e.x = -25.0
        world.onUpdate(0.016)
        assertThat(world.entities, contains(e))
        assertTrue(e.isVisible)

        e.x = -30.0
        world.onUpdate(0.016)
        assertThat(world.entities, contains(e))
        assertTrue(e.isVisible)

        e.x = -31.0
        world.onUpdate(0.016)
        // World still contains entity, but it is invisible
        assertThat(world.entities, contains(e))
        assertFalse(e.isVisible)
    }

    @Test
    fun `Entity is hidden when viewport moves offscreen`() {
        val e = Entity()
        e.addComponent(OffscreenInvisibleComponent(viewport))

        val world = GameWorld()
        world.addEntity(e)
        assertThat(world.entities, contains(e))
        assertTrue(e.isVisible)

        world.onUpdate(0.016)
        assertThat(world.entities, contains(e))
        assertTrue(e.isVisible)

        viewport.x = 5.0
        world.onUpdate(0.016)
        // World still contains entity, but it is invisible
        assertThat(world.entities, contains(e))
        assertFalse(e.isVisible)
    }

    @Test
    fun `Entity is displayed again when entity moves back onscreen`() {
        val e = Entity()
        e.addComponent(OffscreenInvisibleComponent(viewport))

        val world = GameWorld()
        world.addEntity(e)
        assertThat(world.entities, contains(e))
        assertTrue(e.isVisible)

        e.x = -15.0
        world.onUpdate(0.016)
        // World still contains entity, but it is invisible
        assertThat(world.entities, contains(e))
        assertFalse(e.isVisible)

        e.x = 10.0
        world.onUpdate(0.016)
        // Entity is visible again
        assertThat(world.entities, contains(e))
        assertTrue(e.isVisible)
    }

    @Test
    fun `Entity created offscreen becomes visible when it moves onscreen`() {
        val e = Entity()
        e.x = -100.0
        e.addComponent(OffscreenInvisibleComponent(viewport))

        val world = GameWorld()
        world.addEntity(e)
        assertThat(world.entities, contains(e))
        assertFalse(e.isVisible)

        e.x = 10.0
        world.onUpdate(0.016)
        // Entity is visible again
        assertThat(world.entities, contains(e))
        assertTrue(e.isVisible)
    }
}
