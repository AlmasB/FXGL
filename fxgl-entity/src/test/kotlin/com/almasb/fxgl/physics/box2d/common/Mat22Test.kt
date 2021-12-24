/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics.box2d.common

import com.almasb.fxgl.core.math.Vec2
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.*
import org.junit.jupiter.api.Test

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class Mat22Test {

    @Test
    fun `Matrix mul vector`() {
        val mat = Mat22()

        mat.ex.set(2f, 3f)
        mat.ey.set(4f, 7f)

        val v = Vec2(1f, 2f)

        // [ 2 4 ] x [ 1 ] = [ 10  ]
        // [ 3 7 ]   [ 2 ]   [ 17 ]

        val out = Vec2()

        Mat22.mulToOutUnsafe(mat, v, out)

        assertThat(out, `is`(Vec2(10f, 17f)))
    }
}