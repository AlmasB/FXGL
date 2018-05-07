/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.components

import javafx.geometry.Point2D
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class RotationComponentTest {

    @Test
    fun `Creation`() {
        val rot1 = RotationComponent(35.0)

        assertThat(rot1.value, `is`(35.0))
    }

    @Test
    fun `Copy`() {
        val rot1 = RotationComponent(35.0)
        val rot2 = rot1.copy()

        assertThat(rot2.value, `is`(35.0))
        assertTrue(rot1 !== rot2)
    }

    @Test
    fun `Rotation`() {
        val rot1 = RotationComponent(35.0)

        rot1.rotateBy(30.0)
        assertThat(rot1.value, `is`(65.0))

        rot1.rotateBy(-65.0)
        assertThat(rot1.value, `is`(0.0))

        rot1.rotateToVector(Point2D(-1.0, 0.0))
        assertThat(rot1.value, `is`(180.0))

        rot1.rotateToVector(Point2D(1.0, 0.0))
        assertThat(rot1.value, `is`(0.0))

        rot1.rotateToVector(Point2D(0.0, -1.0))
        assertThat(rot1.value, `is`(-90.0))

        rot1.rotateToVector(Point2D(0.0, 1.0))
        assertThat(rot1.value, `is`(90.0))
    }

    @Test
    fun `Equality`() {
        val rot1 = RotationComponent(35.0)
        val rot2 = RotationComponent(90.0)

        assertTrue(rot1 != rot2)
    }
}