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
    fun `Copy ctor`() {
        val v1 = Vec2(Vec2(3f, 5f))

        assertTrue(v1.x == 3f)
        assertTrue(v1.y == 5f)
    }

    @Test
    fun `Set zero`() {
        val v1 = Vec2(13.0f, 10.0f)
        v1.setZero()

        assertTrue(v1.x == 0.0f)
        assertTrue(v1.y == 0.0f)

        v1.x = 3.0f
        v1.y = 2.0f

        v1.reset()

        assertTrue(v1.x == 0.0f)
        assertTrue(v1.y == 0.0f)
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

        v7.negateLocal()
        assertThat(v7, `is`(Vec2(-15.0, 0.0)))

        v7.addLocal(Vec2(3.0, 3.0))
        assertThat(v7, `is`(Vec2(-12.0, 3.0)))

        v7.addLocal(4.0, 3.0)
        assertThat(v7, `is`(Vec2(-8.0, 6.0)))

        v7.subLocal(Vec2(4.0, 2.0))
        assertThat(v7, `is`(Vec2(-12.0, 4.0)))

        v7.subLocal(4.0, 3.0)
        assertThat(v7, `is`(Vec2(-16.0, 1.0)))
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
    fun `Distance squared`() {
        val v1 = Vec2(4f, 3f)

        assertThat(v1.distanceSquared(0.0, 0.0), `is`(25.0))
    }

    @Test
    fun `Normalize`() {


        val v = Vec2(1f, 1f).normalize()

        assertThat(v.x.toDouble(), closeTo(0.7, 0.01))
        assertThat(v.y.toDouble(), closeTo(0.7, 0.01))

        v.x = 0.0f
        v.y = 0.0f

        val v2 = v.normalize()

        assertTrue(v2 == v)
    }

    @Test
    fun `getLengthAndNormalize should return 0 if square of the hypotenuse vector is less than FXGLMath epsilon constant`(){
        val v = Vec2().getLengthAndNormalize()
        assertThat(v, `is`(0f))
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

        assertThat(v.angle(Vec2(0f, 1f)), `is`(45f))

        assertThat(v.angle(Point2D(0.0, -1.0)), `is`(225f))
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

    @Test
    fun `Midpoint`() {
        val v1 = Vec2(5.0f, 2.0f)
        val v2 = Vec2(0.0f, 0.0f)

        assertThat(v1.midpoint(v2), `is`(Vec2(2.5f, 1.0f)))
        assertThat(v1.midpoint(Point2D.ZERO), `is`(Vec2(2.5f, 1.0f)))
    }

    @Test
    fun `To Point2D`() {
        val v1 = Vec2(5.0f, 2.0f)

        assertThat(v1.toPoint2D(), `is`(Point2D(5.0, 2.0)))
    }

    @Test
    fun `To String`() {
        val v1 = Vec2(5.0f, 2.0f)

        assertThat(v1.toString(), `is`("(5.0,2.0)"))
    }

    @Test
    fun `From angle`() {
        val v1 = Vec2.fromAngle(45.0)

        assertThat(v1, `is`(Vec2(0.70697117, 0.70724237)))
    }

    @Test
    fun `Dot product`() {
        assertThat(Vec2.dot(Vec2(2f, 3f), Vec2(4f, 7f)), `is`(29f))
    }

    @Test
    fun `Cross product`() {
        assertThat(Vec2.cross(Vec2(2f, 3f), Vec2(4f, 7f)), `is`(2f))
    }

    @Test
    fun `Min to out should return min x and y from both vector when min x and y are in one vector`() {
        val v1 = Vec2(2f, 3f)
        val v2 = Vec2(4f, 5f)
        val v3 = Vec2()

        Vec2.minToOut(v1, v2, v3)
        assertThat(v3, `is`(v1))

        Vec2.minToOut(v2, v1, v3)
        assertThat(v3, `is`(v1))
    }

    @Test
    fun `Min to out should return min x and y from both vectors when min x and y are in different vectors`() {
        val v1 = Vec2(2f, 5f)
        val v2 = Vec2(4f, 3f)
        val v3 = Vec2()

        Vec2.minToOut(v1, v2, v3)
        assertThat(v3, `is`(Vec2(2f, 3f)))

        Vec2.minToOut(v2, v1, v3)
        assertThat(v3, `is`(Vec2(2f, 3f)))
    }

    @Test
    fun `Max to out should return max x and y from both vector when max x and y are in one vector`() {
        val v1 = Vec2(2f, 3f)
        val v2 = Vec2(4f, 5f)
        val v3 = Vec2()

        Vec2.maxToOut(v1, v2, v3)
        assertThat(v3, `is`(v2))

        Vec2.maxToOut(v2, v1, v3)
        assertThat(v3, `is`(v2))
    }

    @Test
    fun `Max to out should return max x and y from both vectors when max x and y are in different vectors`() {
        val v1 = Vec2(2f, 5f)
        val v2 = Vec2(4f, 3f)
        val v3 = Vec2()

        Vec2.maxToOut(v1, v2, v3)
        assertThat(v3, `is`(Vec2(4f, 5f)))

        Vec2.maxToOut(v2, v1, v3)
        assertThat(v3, `is`(Vec2(4f, 5f)))
    }

    @Test
    fun `Equals should return true if passed the same instance`() {
        val v1 = Vec2()
        assertTrue(v1 == v1)
    }

    @Test
    fun `Equals should return true if passed the same type and coordinates`() {
        val v1 = Vec2(1f, 1f)
        val v2 = Vec2(1f, 1f)
        assertTrue(v1 == v2)
    }

    @Test
    fun `Equals should return false if passed the same type and different y coordinates`() {
        val v1 = Vec2(1f, 1f)
        val v2 = Vec2(1f, 2f)
        assertFalse(v1 == v2)
    }

    @Test
    fun `Equals should return false if passed the same type and different x coordinates`() {
        val v1 = Vec2(1f, 1f)
        val v2 = Vec2(2f, 1f)
        assertFalse(v1 == v2)
    }

    @Test
    fun `Equals should return false if passed a different type`() {
        val v1 = Vec2(1f, 1f)
        assertFalse(v1.equals(1))
    }

    @Test
    fun `Cross to out unsafe`() {
        val v1 = Vec2(1f, 1f)
        val v2 = Vec2()

        Vec2.crossToOutUnsafe(v1, 5.0f, v2)

        assertThat(v2, `is`(Vec2(5.0, -5.0)))

        Vec2.crossToOutUnsafe(5.0f, v1, v2)

        assertThat(v2, `is`(Vec2(-5.0, 5.0)))
    }
}