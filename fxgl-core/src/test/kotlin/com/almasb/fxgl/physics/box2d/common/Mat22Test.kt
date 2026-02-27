/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics.box2d.common

import com.almasb.fxgl.core.math.Vec2
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class Mat22Test {

    @Test
    fun `Set to zero`() {
        val mat = Mat22()

        mat.ex.set(2f, 3f)
        mat.ey.set(4f, 7f)

        mat.setZero()

        assertThat(mat.ex, `is`(Vec2()))
        assertThat(mat.ey, `is`(Vec2()))
    }

    @Test
    fun `Invert`() {
        val A = Mat22()
        A.ex.set(1f, 2f)
        A.ey.set(3f, 4f)

        val B = Mat22()
        A.invertToOut(B)

        val eps = 1e-6f
        assertEquals(-2.0f, B.ex.x, eps)
        assertEquals(1.5f, B.ey.x, eps)
        assertEquals(1.0f, B.ex.y, eps)
        assertEquals(-0.5f, B.ey.y, eps)
    }

    @Test
    fun `Solve Ax = b`() {
        val A = Mat22()
        A.ex.set(2.0f, 3.0f)
        A.ey.set(4.0f, 7.0f)

        val b = Vec2(10.0f, 17.0f)
        val out = Vec2()

        //    A        x   =   b
        // [ 2 4 ] x [ 1 ] = [ 10 ]
        // [ 3 7 ]   [ 2 ]   [ 17 ]

        A.solveToOut(b, out)

        assertEquals(1.0f, out.x, 0.0001f)
        assertEquals(2.0f, out.y, 0.0001f)
    }

    @Test
    fun `Matrix mul vector`() {
        val mat = Mat22()

        mat.ex.set(2f, 3f)
        mat.ey.set(4f, 7f)

        val v = Vec2(1f, 2f)

        // [ 2 4 ] x [ 1 ] = [ 10 ]
        // [ 3 7 ]   [ 2 ]   [ 17 ]

        val out = Vec2()

        Mat22.mulToOutUnsafe(mat, v, out)

        assertThat(out, `is`(Vec2(10f, 17f)))
    }
}