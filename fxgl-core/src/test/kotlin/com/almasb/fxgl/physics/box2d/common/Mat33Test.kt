/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics.box2d.common

import com.almasb.fxgl.core.math.Vec3
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * @author Ben Highgate (ben.highgate@gmail.com)
 */
class Mat33Test {

    @Test
    fun `Matrix mul vector`() {
        val mat = Mat33()

        mat.ex.set(3f, 4f, 2f)
        mat.ey.set(6f, 2f, 5f)
        mat.ez.set(4f, 3f, 3f)

        val v = Vec3(2f, 1f, 4f)

        // [ 3 6 4 ] x [ 2 ] = [ 28 ]
        // [ 4 2 3 ]   [ 1 ]   [ 22 ]
        // [ 2 5 3 ]   [ 4 ]   [ 21 ]

        val out = Vec3()

        Mat33.mulToOutUnsafe(mat, v, out)

        assertThat(out, `is`(Vec3(28f, 22f, 21f)))
    }

    @Test
    fun `Solve Ax = b`() {
        val A = Mat33()
        A.ex.set(3.0f, 4.0f, 2.0f)
        A.ey.set(6.0f, 2.0f, 5.0f)
        A.ez.set(4.0f, 3.0f, 3.0f)

        val b = Vec3(28.0f, 22.0f, 21.0f)
        val out = Vec3()

        // [ 3 6 4 ] x [ 2 ] = [ 28 ]
        // [ 4 2 3 ]   [ 1 ]   [ 22 ]
        // [ 2 5 3 ]   [ 4 ]   [ 21 ]

        A.solve33ToOut(b, out)

        assertEquals(2.0f, out.x, 0.0001f)
        assertEquals(1.0f, out.y, 0.0001f)
        assertEquals(4.0f, out.z, 0.0001f)
    }

    @Test
    fun `Symmetrical Invert`() {
        val A = Mat33()
        A.ex.set(3.0f, 4.0f, 2.0f)
        A.ey.set(6.0f, 2.0f, 5.0f)
        A.ez.set(4.0f, 3.0f, 3.0f)

        val b = Mat33()
        A.getSymInverse33(b)

        assertThat(b.ex, `is`(Vec3(-3.0f, -6.0f, 10.0f)))
        assertThat(b.ey, `is`(Vec3(-6.0f, -7.0f, 15.0f)))
        assertThat(b.ez, `is`(Vec3(10.0f, 15.0f, -30.0f)))
    }
}