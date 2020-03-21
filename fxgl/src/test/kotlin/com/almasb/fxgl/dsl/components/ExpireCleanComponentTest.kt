/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.dsl.components

import com.almasb.fxgl.entity.Entity
import com.almasb.fxgl.entity.GameWorld
import javafx.util.Duration
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.contains
import org.hamcrest.Matchers.lessThan
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class ExpireCleanComponentTest {

    @Test
    fun `Entity is removed from world when expired`() {
        val e = Entity()
        e.addComponent(ExpireCleanComponent(Duration.seconds(1.0)))

        val world = GameWorld()
        world.addEntity(e)
        assertThat(world.entities, contains(e))

        world.onUpdate(0.5)
        assertThat(world.entities, contains(e))

        world.onUpdate(0.5)
        assertTrue(world.entities.isEmpty())
    }

    @Test
    fun `Opacity is animated`() {
        val e = Entity()
        e.addComponent(ExpireCleanComponent(Duration.seconds(1.0)).animateOpacity())

        val world = GameWorld()
        world.addEntity(e)
        assertThat(e.opacity, `is`(1.0))

        world.onUpdate(0.5)
        assertThat(e.opacity, lessThan(1.0))

        world.onUpdate(0.5)
        assertThat(e.opacity, `is`(0.0))
    }
}