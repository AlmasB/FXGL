/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core.math

import com.almasb.fxgl.core.math.FXGLMath.*
import javafx.geometry.Point2D
import javafx.geometry.Rectangle2D
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.collection.IsIn
import org.hamcrest.collection.IsIn.isOneOf
import org.hamcrest.number.IsCloseTo.closeTo
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.*

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class FXGLMathTest {

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
    fun `atan2`() {
        //                   y / x
        assertThat(atan2(0.0, 0.0), `is`(0.0))
        assertThat(atan2(-1.0, 0.0), closeTo(-HALF_PI, 0.01))
        assertThat(atan2(1.0, 0.0), closeTo(HALF_PI, 0.01))
        assertThat(atan2(1.0, -1.0), closeTo(2.352, 0.01))
    }

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
    fun `Random values`() {
        val originalRandom = getRandom()

        val random = getRandom(17092019)

        setRandom(random)

        val bools = arrayListOf<Boolean>()
        val chanceBools = arrayListOf<Boolean>()

        repeat(100) {
            // assert does not fail
            randomColor()

            assertThat(randomSign(), isOneOf(1, -1))
            assertThat(FXGLMath.random(-1, 2), isOneOf(-1, 0, 1, 2))
            assertThat(FXGLMath.random(-1L, 2L), isOneOf(-1L, 0L, 1L, 2L))

            var value = FXGLMath.random(0.0, 2.5)

            assertThat(value, Matchers.greaterThan(-0.0))
            assertThat(value, Matchers.lessThan(2.5))

            value = randomDouble()

            assertThat(value, Matchers.greaterThanOrEqualTo(0.0))
            assertThat(value, Matchers.lessThan(1.0))

            val value2 = randomFloat()

            assertThat(value2, Matchers.greaterThanOrEqualTo(0.0f))
            assertThat(value2, Matchers.lessThan(1.0f))

            assertThat(randomPoint2D().magnitude(), closeTo(1.0, 0.0001))
            assertThat(randomVec2().length().toDouble(), closeTo(1.0, 0.0001))

            val p = randomPoint(Rectangle2D(0.0, 0.0, 2.0, 5.0))

            assertThat(p.x, Matchers.greaterThanOrEqualTo(0.0))
            assertThat(p.x, Matchers.lessThan(2.0))
            assertThat(p.y, Matchers.greaterThanOrEqualTo(0.0))
            assertThat(p.y, Matchers.lessThan(5.0))

            bools += randomBoolean()
            chanceBools += randomBoolean(0.35)
        }

        assertThat(bools.filter { it }.size, `is`(not(100)))
        assertThat(chanceBools.filter { it }.size, `is`(not(100)))

        setRandom(originalRandom)
    }

    @Test
    fun `Random array element returns null if array is empty`() {
        val array = Array(0) { "" }

        val element = random(array)
        assertFalse(element.isPresent)
    }

    @Test
    fun `Random array element returns it, if single item`() {
        val e = ""
        val array = Array(1, { e })

        val element = random(array)
        assertThat(element.get(), `is`(e))
    }

    @Test
    fun `Random list element returns null if list is empty`() {
        val list = listOf<String>()

        val element = random(list)
        assertFalse(element.isPresent)
    }

    @Test
    fun `Random list element returns it, if single item`() {
        val e = ""
        val list = listOf(e)

        val element = random(list)
        assertThat(element.get(), `is`(e))
    }

    @Test
    fun `Map test`() {
        assertThat(map(0.5, 0.0, 1.0, 100.0, 200.0), closeTo(150.0, 0.1))
    }

    @Test
    fun `abs test double`() {
        assertThat(abs(0.0), isOneOf(0.0, -0.0))
        assertThat(abs(1.0), `is`(1.0))
        assertThat(abs(-1.0), `is`(1.0))
    }

    @Test
    fun `abs test float`() {
        assertThat(abs(0.0f), isOneOf(0.0f, -0.0f))
        assertThat(abs(1.0f), `is`(1.0f))
        assertThat(abs(-1.0f), `is`(1.0f))
    }

    @Test
    fun `Bezier`() {
        val p1 = bezier(Point2D(0.0, 0.0), Point2D(50.0, 0.0), Point2D(100.0, 0.0), 0.3)

        assertThat(p1, `is`(Point2D(30.0, 0.0)))

        val p2 = bezier(Point2D(0.0, 0.0), Point2D(20.0, 0.0), Point2D(80.0, 0.0), Point2D(100.0, 0.0), 0.5)

        assertThat(p2, `is`(Point2D(50.0, 0.0)))
    }

    @Test
    fun `Noises`() {
        for (y in 0..250) {
            for (x in 0..250) {
                val v1 = noise2D(x.toDouble(), y.toDouble())
                val v2 = noise3D(x.toDouble(), y.toDouble(), x*y.toDouble())

                assertThat(v1, Matchers.allOf(Matchers.greaterThan(-1.0), Matchers.lessThan(1.0)))
                assertThat(v2, Matchers.allOf(Matchers.greaterThan(-1.0), Matchers.lessThan(1.0)))

                val v3 = noise1D(x+y.toDouble())

                assertThat(v3, Matchers.allOf(Matchers.greaterThanOrEqualTo(-0.0), Matchers.lessThan(1.0)))
            }
        }
    }
}