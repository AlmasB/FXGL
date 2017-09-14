/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core.math

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThat
import org.junit.Test

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class Vec2Test {

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
}