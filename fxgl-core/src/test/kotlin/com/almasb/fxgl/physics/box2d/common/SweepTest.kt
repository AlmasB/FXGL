/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics.box2d.common

import com.almasb.fxgl.core.math.Vec2
import org.hamcrest.MatcherAssert.*
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class SweepTest {

    private lateinit var sweep: Sweep

    @BeforeEach
    fun `setUp`() {
        sweep = Sweep()
    }

    @Test
    fun `normalize`() {
        // > 2 PI
        sweep.a0 = 7f

        sweep.normalize()

        assertThat(sweep.a0, `is`(0.7168145f))
        assertThat(sweep.a, `is`(-6.2831855f))
    }

    @Test
    fun `get transform at given time`() {
        sweep.localCenter.set(3.5f, 1.2f)
        sweep.c.set(2f, 0f)
        sweep.a0 = 2f
        sweep.a = 4f

        val xf = Transform()

        sweep.getTransform(xf, 0.5f)

        assertThat(xf.p, `is`(Vec2(4.634288f, 0.694177f)))

        // Rotation(3f)
        assertThat(xf.q.c, `is`(-0.9899941f))
        assertThat(xf.q.s, `is`(0.14109027f))
    }
}