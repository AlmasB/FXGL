/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics.box2d.common

import org.hamcrest.MatcherAssert.*
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class RotationTest {

    @Test
    fun `Rotation basics`() {
        val rot = Rotation(Math.toRadians(90.0).toFloat())

        assertThat(rot.s.toDouble(), closeTo(1.0, 0.1))
        assertThat(rot.c.toDouble(), closeTo(0.0, 0.1))
    }
}