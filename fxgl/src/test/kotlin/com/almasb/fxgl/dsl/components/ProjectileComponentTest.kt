/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.dsl.components

import com.almasb.fxgl.entity.Entity
import javafx.geometry.Point2D
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class ProjectileComponentTest {

    private lateinit var comp: ProjectileComponent
    private lateinit var e: Entity

    @BeforeEach
    fun setUp() {
        e = Entity()
    }

    @Test
    fun `Creation`() {
        comp = ProjectileComponent(Point2D(1.0, 0.0), 100.0)
        e.addComponent(comp)

        assertThat(comp.direction, `is`(Point2D(1.0, 0.0)))
        assertThat(comp.speed, `is`(100.0))
        assertThat(comp.velocity, `is`(Point2D(100.0, 0.0)))
    }

    @Test
    fun `Setting direction and speed updates velocity`() {
        comp = ProjectileComponent(Point2D(1.0, 0.0), 100.0)
        e.addComponent(comp)

        comp.direction = Point2D(0.0, 1.0)
        assertThat(comp.velocity, `is`(Point2D(0.0, 100.0)))

        comp.speed = 50.0
        assertThat(comp.velocity, `is`(Point2D(0.0, 50.0)))
    }

    @Test
    fun `Component rotates the entity in moving direction`() {
        comp = ProjectileComponent(Point2D(1.0, 1.0), 100.0).allowRotation(true)
        e.addComponent(comp)

        assertThat(e.rotation, `is`(45.0))
    }

    @Test
    fun `Component moves the entity by its velocity`() {
        comp = ProjectileComponent(Point2D(1.0, 0.0), 100.0)
        e.addComponent(comp)

        assertThat(e.position, `is`(Point2D(0.0, 0.0)))

        comp.onUpdate(1.0)

        assertThat(e.position, `is`(Point2D(100.0, 0.0)))
    }
}