/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core.math

import javafx.geometry.Point2D
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.closeTo
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class Vec2Test {

    @Test
    fun `Copy`() {
        val v1 = Vec2()
        val v2 = v1.copy()

        assertTrue(v1 == v2)
        assertFalse(v1 === v2)
    }

    @Test
    fun `Copy and set from Point2D`() {
        val v = Vec2(Point2D(10.0, 15.5))
        assertThat(v, `is`(Vec2(10.0, 15.5)))

        v.set(Point2D(-5.0, 3.0))
        assertThat(v, `is`(Vec2(-5.0, 3.0)))
    }

    @Test
    fun `Set from angle`() {
        val v = Vec2().setFromAngle(45.0)

        assertThat(v.x.toDouble(), closeTo(0.7, 0.01))
        assertThat(v.y.toDouble(), closeTo(0.7, 0.01))
    }

    @Test
    fun `Operations`() {
        val v1 = Vec2(5.0, 5.0)

        val v2 = v1.add(Point2D(5.0, 5.0))
        assertThat(v2, `is`(Vec2(10.0, 10.0)))

        val v3 = v2.add(Vec2(-5.0, -10.0))
        assertThat(v3, `is`(Vec2(5.0, 0.0)))

        val v4 = v3.sub(Point2D(5.0, 5.0))
        assertThat(v4, `is`(Vec2(0.0, -5.0)))

        val v5 = v4.sub(Vec2(5.0, -5.0))
        assertThat(v5, `is`(Vec2(-5.0, 0.0)))

        val v6 = v5.mul(3.0)
        assertThat(v6, `is`(Vec2(-15.0, 0.0)))

        val v7 = v6.negate()
        assertThat(v7, `is`(Vec2(15.0, -0.0)))
    }

    @Test
    fun `Perpendicular CCW`() {
        val v1 = Vec2(10f, 5f)
        val v2 = v1.perpendicularCCW()

        assertThat(v2, `is`(Vec2(5f, -10f)))
    }

    @Test
    fun `Perpendicular CW`() {
        val v1 = Vec2(10f, 5f)
        val v2 = v1.perpendicularCW()

        assertThat(v2, `is`(Vec2(-5f, 10f)))
    }

    @Test
    fun `Length`() {
        val v = Vec2(3f, -4f)
        assertThat(v.length(), `is`(5f))

        assertThat(v.lengthSquared(), `is`(25f))
    }

    @Test
    fun `Distance`() {
        val v = Vec2(3f, -4f)
        assertThat(v.distance(Vec2(5f, 4f)), closeTo(8.246, 0.01))
    }

    @Test
    fun `Normalize`() {
        val v = Vec2(1f, 1f).normalize()

        assertThat(v.x.toDouble(), closeTo(0.7, 0.01))
        assertThat(v.y.toDouble(), closeTo(0.7, 0.01))
    }

    @Test
    fun `Absolute`() {
        val v = Vec2(-1f, 1f).abs()
        assertThat(v, `is`(Vec2(1f, 1f)))

        assertThat(Vec2(-3f, -4f).absLocal(), `is`(Vec2(3f, 4f)))
    }

    @Test
    fun `Angle`() {
        val v = Vec2(-1f, 1f)
        assertThat(v.angle(), `is`(135f))
    }

    @Test
    fun `Set length`() {
        val v = Vec2(3f, -4f)
        v.setLength(13.0)

        assertEquals(13f, v.length(), 0.0001f)
    }

    @Test
    fun `Test equality`() {
        val v1 = Vec2()
        val v2 = Vec2()

        assertThat(v1, `is`(v1))
        assertThat(v1, `is`(v2))
        assertThat(v2, `is`(v1))
        assertThat(v1.hashCode(), `is`(v2.hashCode()))

        v2.x = 10.0f
        assertThat(v1, `is`(not(v2)))
        assertThat(v1.hashCode(), `is`(not(v2.hashCode())))

        v1.x = 10.0f
        assertThat(v1, `is`(v2))
        assertThat(v1.hashCode(), `is`(v2.hashCode()))

        v2.y = -3.0f
        assertThat(v1, `is`(not(v2)))
        assertThat(v1.hashCode(), `is`(not(v2.hashCode())))

        v1.y = -3.0f
        assertThat(v1, `is`(v2))
        assertThat(v1.hashCode(), `is`(v2.hashCode()))
    }

    @Test
    fun `Equals and close to Point2D`() {
        val v = Vec2(0.0f, 0.0f)
        assertTrue(v.isCloseTo(Point2D.ZERO, 0.0))

        v.set(15.0f, -33.5f)
        assertTrue(v.isCloseTo(Point2D(15.0, -33.5), 0.0))

        v.set(15.0f, -33.4f)
        assertFalse(v.isCloseTo(Point2D(15.0, -33.5), 0.0))

        v.set(15.0f, -33.5001f)
        assertTrue(v.isCloseTo(Point2D(15.0, -33.5), 0.001))

        v.set(15.0f, -33.50001f)
        assertTrue(v.isCloseTo(Point2D(15.0, -33.5), 0.01))
        assertTrue(v.isCloseTo(Point2D(15.0, -33.5), 0.001))
        assertTrue(v.isCloseTo(Point2D(15.0, -33.5), 0.0001))
        assertTrue(v.isCloseTo(Point2D(15.0, -33.5), 0.00002))

        assertFalse(v.isCloseTo(Point2D(15.0, -33.5), 0.000002))
        assertFalse(v.isCloseTo(Point2D(15.0, -33.5), 0.000001))

        v.set(15.01f, -33.49f)

        assertTrue(v.isCloseTo(Point2D(15.0, -33.5), 0.1))

        v.set(Vec2(15.01f, 8.99f))

        assertTrue(v.isCloseTo(Point2D(15.0, 9.0), 0.1))

        v.set(900.001f, -1501.330f)
        assertFalse(v.isNearlyEqualTo(Point2D(900.0, -1501.0)))
        assertTrue(v.isNearlyEqualTo(Point2D(900.0, -1501.3)))
    }
}