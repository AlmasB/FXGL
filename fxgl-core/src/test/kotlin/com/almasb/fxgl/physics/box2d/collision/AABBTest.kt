/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics.box2d.collision

import com.almasb.fxgl.core.math.Vec2
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class AABBTest {

    private lateinit var aabb: AABB

    @BeforeEach
    fun setUp() {
        aabb = AABB(Vec2(5.0, 3.0), Vec2(10.0, 15.0))
    }

    @Test
    fun `Center`() {
        assertThat(aabb.center, `is`(Vec2(7.5, 9.0)))
    }

    @Test
    fun `Extents`() {
        assertThat(aabb.extents, `is`(Vec2(2.5, 6.0)))
    }

    @Test
    fun `String`() {
        assertThat(aabb.toString(), `is`("AABB[(5.0,3.0) . (10.0,15.0)]"))
    }
}