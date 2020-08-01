/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
@file:Suppress("JAVA_MODULE_DOES_NOT_DEPEND_ON_MODULE")

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
        assertThat(comp.acceleration, `is`(Point2D(0.0, 0.0)))
    }

    @Test
    fun `Creation with acceleration`() {
        comp = ProjectileComponent(Point2D(0.0, 0.0), 0.0, Point2D(1.0, 1.0))
        e.addComponent(comp)

        assertThat(comp.acceleration, `is`(Point2D(1.0, 1.0)))
        assertThat(comp.direction, `is`(Point2D(0.0, 0.0)))
        assertThat(comp.speed, `is`(0.0))
    }

    @Test
    fun `Acceleration change direction and speed`(){
        val tpf = .01
        val acceleration = Point2D(100.0, 0.0)

        comp = ProjectileComponent(Point2D(0.0, 0.0), 0.0, acceleration)
        e.addComponent(comp)

        assertThat(comp.acceleration, `is`(acceleration))

        comp.onUpdate(tpf)

        assertThat(comp.direction, `is`(acceleration.normalize()))
        assertThat(comp.speed, `is`(acceleration.magnitude().times(tpf)))
        assertThat(comp.acceleration, `is`(acceleration))

        comp.onUpdate(tpf)

        assertThat(comp.direction, `is`(acceleration.normalize()))
        assertThat(comp.speed, `is`(acceleration.magnitude().times(2*tpf)))
        assertThat(comp.acceleration, `is`(acceleration))
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