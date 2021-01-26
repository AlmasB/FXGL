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

    // example polygons
    private lateinit var p0: PolygonShape
    private lateinit var p1: PolygonShape
    private lateinit var p2: PolygonShape
    private lateinit var p3: PolygonShape

    @BeforeEach
    fun `setUp`() {
        polygon = PolygonShape()

        p0 = PolygonShape()
        p0.setAsBox(0.5f, 0.5f)

        p1 = PolygonShape()
        p1.set(arrayOf(
                Vec2(-1f, 0f),
                Vec2(-1f, 1f),
                Vec2(2f, 0.5f),
                Vec2(3f, 0.25f),
                Vec2(1f, 0f)
        ))

        p2 = PolygonShape()
        p2.set(arrayOf(
                Vec2(-2f, 2f),
                Vec2(-3f, 2f),
                Vec2(0f, 3.5f),
                Vec2(3f, 0.25f),
                Vec2(1.5f, 0.15f)
        ))

        p3 = PolygonShape()
        p3.set(arrayOf(
                Vec2(-4f, -2f),
                Vec2(-5f, -2f),
                Vec2(-5f, 3.5f),
                Vec2(-3f, 2.25f),
                Vec2(-1.5f, 5.15f),
                Vec2(8f, -4f),
                Vec2(0f, 0f)
        ))
    }

    @Test
    fun `Set as box`() {
        polygon.setAsBox(1f, 1f)

        assertThat(polygon.centroid, `is`(Vec2()))
    }

    @Test
    fun `Set as polygon centroid`() {
        assertThat(p1.centroid, `is`(Vec2(0.4561403f, 0.3903509f)))
        assertThat(p2.centroid, `is`(Vec2(0.092274696f, 1.7105868f)))
        assertThat(p3.centroid, `is`(Vec2(-0.19980428f, 0.12039991f)))
    }

    @Test
    fun `Compute mass`() {
        var massData = MassData()

        p0.computeMass(massData, 0.2f)

        assertThat(massData.mass, `is`(0.2f))
        assertThat(massData.I, `is`(0.033333335f))
        assertThat(massData.center, `is`(Vec2(0.0, 0.0)))

        // eg1

        massData = MassData()

        p1.computeMass(massData, 0.2f)

        assertThat(massData.mass, `is`(0.475f))
        assertThat(massData.I, `is`(0.6999059f))
        assertThat(massData.center, `is`(Vec2(0.45614037, 0.39035088)))

        // eg2

        massData = MassData()

        p2.computeMass(massData, 0.2f)

        assertThat(massData.mass, `is`(1.7475001f))
        assertThat(massData.I, `is`(9.106726f))
        assertThat(massData.center, `is`(Vec2(0.092274666,1.7105865)))

        // eg3

        massData = MassData()

        p3.computeMass(massData, 0.2f)

        assertThat(massData.mass, `is`(11.92f))
        assertThat(massData.I, `is`(169.71835f))
        assertThat(massData.center, `is`(Vec2(-0.19980425,0.12040007)))

        // eg with different density

        massData = MassData()

        p3.computeMass(massData, 1.25f)

        assertThat(massData.mass, `is`(74.5f))
        assertThat(massData.I, `is`(1060.7396f))
        assertThat(massData.center, `is`(Vec2(-0.19980425,0.12040007)))
    }
}