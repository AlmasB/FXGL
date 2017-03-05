/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.almasb.fxgl.entity.component

import javafx.geometry.Point2D
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class RotationComponentTest {

    @Test
    fun `Creation`() {
        val rot1 = com.almasb.fxgl.entity.component.RotationComponent(35.0)

        assertThat(rot1.value, `is`(35.0))
    }

    @Test
    fun `Copy`() {
        val rot1 = com.almasb.fxgl.entity.component.RotationComponent(35.0)
        val rot2 = rot1.copy()

        assertThat(rot2.value, `is`(35.0))
        assertTrue(rot1 !== rot2)
    }

    @Test
    fun `Rotation`() {
        val rot1 = com.almasb.fxgl.entity.component.RotationComponent(35.0)

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
        val rot1 = com.almasb.fxgl.entity.component.RotationComponent(35.0)
        val rot2 = com.almasb.fxgl.entity.component.RotationComponent(90.0)

        assertTrue(rot1 != rot2)
    }
}