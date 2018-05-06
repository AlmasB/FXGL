/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core.math

import com.almasb.fxgl.app.FXGLMock
import com.almasb.fxgl.core.math.FXGLMath.*
import com.almasb.fxgl.entity.Entity
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.number.IsCloseTo.closeTo
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class FXGLMathTest {

    companion object {
        @BeforeAll
        @JvmStatic fun before() {
            FXGLMock.mock()
        }
    }

    @Test
    fun `Sin rad returns correct values`() {
        for (deg in -360..360) {
            val rad = Math.toRadians(deg.toDouble())

            assertThat(sin(rad), closeTo(Math.sin(rad), 0.001))
        }

        assertThat(sin(-3 * PI), closeTo(Math.sin(-3 * PI), 0.001))
    }

    @Test
    fun `Cos rad returns correct values`() {
        for (deg in -360..360) {
            val rad = Math.toRadians(deg.toDouble())

            assertThat(cos(rad), closeTo(Math.cos(rad), 0.001))
        }

        assertThat(cos(-3 * PI), closeTo(Math.cos(-3 * PI), 0.001))
    }

    @Test
    fun `Sin deg returns correct values`() {
        for (deg in -360..360) {
            assertThat(sinDeg(deg.toDouble()), closeTo(Math.sin(Math.toRadians(deg.toDouble())), 0.001))
        }
    }

    @Test
    fun `Cos deg returns correct values`() {
        for (deg in -360..360) {
            assertThat(cosDeg(deg.toDouble()), closeTo(Math.cos(Math.toRadians(deg.toDouble())), 0.001))
        }
    }

    @Test
    fun `To radians`() {
        for (deg in -360..360) {
            assertThat(toRadians(deg.toDouble()), closeTo(Math.toRadians(deg.toDouble()), 0.001))
        }
    }

    @Test
    fun `To degrees`() {
        for (deg in -360..360) {

            val radians = Math.toRadians(deg.toDouble())

            assertThat(toDegrees(radians), closeTo(Math.toDegrees(radians), 0.001))
        }
    }

//    @Test
//    fun `Normalize angle deg`() {
//        assertThat(normalizeAngleDeg(0.0), `is`(0.0))
//        assertThat(normalizeAngleDeg(90.0), `is`(90.0))
//        assertThat(normalizeAngleDeg(100.0), `is`(80.0))
//    }

    @Test
    fun `atan2 deg`() {
        //                   y / x
        assertThat(atan2Deg(0.0, 1.0), `is`(0.0))
        assertThat(atan2Deg(1.0, 0.0), closeTo(90.0, 1.0))
        assertThat(atan2Deg(1.0, 1.0), closeTo(45.0, 1.0))
        assertThat(atan2Deg(-1.0, 1.0), closeTo(-45.0, 1.0))
        assertThat(atan2Deg(-1.0, -1.0), closeTo(-135.0, 1.0))
    }

    @Test
    fun `Random array element returns null if array is empty`() {
        val array = Array<Entity>(0, { Entity() })

        val element = random(array)
        assertFalse(element.isPresent)
    }

    @Test
    fun `Random array element returns it, if single item`() {
        val e = Entity()
        val array = Array<Entity>(1, { e })

        val element = random(array)
        assertThat(element.get(), `is`(e))
    }

    @Test
    fun `Random list element returns null if list is empty`() {
        val list = listOf<Entity>()

        val element = random(list)
        assertFalse(element.isPresent)
    }

    @Test
    fun `Random list element returns it, if single item`() {
        val e = Entity()
        val list = listOf<Entity>(e)

        val element = random(list)
        assertThat(element.get(), `is`(e))
    }

    @Test
    fun `floor`() {
        assertThat(FXGLMath.floor(0.5), `is`(0))
        assertThat(FXGLMath.floor(0.9), `is`(0))
        assertThat(FXGLMath.floor(1.0), `is`(1))
        assertThat(FXGLMath.floor(-0.5), `is`(-1))
        assertThat(FXGLMath.floor(-1.5), `is`(-2))
    }

    @Test
    fun `floor positive`() {
        assertThat(FXGLMath.floorPositive(0.5), `is`(0))
        assertThat(FXGLMath.floorPositive(0.9), `is`(0))
        assertThat(FXGLMath.floorPositive(1.0), `is`(1))

        // this is the diff between just "floor"
        assertThat(FXGLMath.floorPositive(-0.5), `is`(0))
        assertThat(FXGLMath.floorPositive(-1.5), `is`(-1))
    }

    @Test
    fun `Is close to under tolerance`() {
        assertTrue(isCloseToZero(0.0, 0.0))
        assertFalse(isCloseToZero(0.1, 0.0))

        assertTrue(isCloseToZero(0.1, 0.1))
        assertFalse(isCloseToZero(-0.2, 0.1))

        assertTrue(isCloseToZero(0.01, 0.1))

        assertTrue(isCloseToZero(0.09, 0.1))

        assertFalse(isCloseToZero(0.11, 0.1))
    }
}