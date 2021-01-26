/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
@file:Suppress("JAVA_MODULE_DOES_NOT_DEPEND_ON_MODULE")
package com.almasb.fxgl.physics.box2d.collision.shapes

import com.almasb.fxgl.core.math.Vec2
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class PolygonShapeTest {

    private lateinit var polygon: PolygonShape

    @BeforeEach
    fun `setUp`() {
        polygon = PolygonShape()
    }

    @Test
    fun `Set as box`() {
        polygon.setAsBox(1f, 1f)

        assertThat(polygon.centroid, `is`(Vec2()))
    }

    @Test
    fun `Set as polygon`() {
        // example 1
        polygon.set(arrayOf(
                Vec2(-1f, 0f),
                Vec2(-1f, 1f),
                Vec2(2f, 0.5f),
                Vec2(3f, 0.25f),
                Vec2(1f, 0f)
        ))

        assertThat(polygon.centroid, `is`(Vec2(0.4561403f, 0.3903509f)))

        // example 2
        polygon = PolygonShape()
        polygon.set(arrayOf(
                Vec2(-2f, 2f),
                Vec2(-3f, 2f),
                Vec2(0f, 3.5f),
                Vec2(3f, 0.25f),
                Vec2(1.5f, 0.15f)
        ))

        assertThat(polygon.centroid, `is`(Vec2(0.092274696f, 1.7105868f)))

        // example 3
        polygon = PolygonShape()
        polygon.set(arrayOf(
                Vec2(-4f, -2f),
                Vec2(-5f, -2f),
                Vec2(-5f, 3.5f),
                Vec2(-3f, 2.25f),
                Vec2(-1.5f, 5.15f),
                Vec2(8f, -4f),
                Vec2(0f, 0f)
        ))

        assertThat(polygon.centroid, `is`(Vec2(-0.19980428f, 0.12039991f)))
    }
}