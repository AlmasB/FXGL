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

import org.junit.Test
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.*
import org.junit.Before

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class RechargeableComponentTest {

    private class HPComponent : RechargeableComponent(100.0) {

    }

    private lateinit var hp: HPComponent

    @Before
    fun setUp() {
        hp = HPComponent()
    }

    @Test
    fun `Creation`() {
        assertThat(hp.maxValue, `is`(100.0))
        assertThat(hp.value, `is`(100.0))
        assertThat(hp.isZero, `is`(false))
    }

    @Test
    fun `Modification`() {
        hp.value = 100.0
        assertThat(hp.value, `is`(100.0))

        hp.damage(30.0)
        assertThat(hp.value, `is`(70.0))

        hp.damagePercentageCurrent(10.0)
        assertThat(hp.value, `is`(63.0))

        hp.damagePercentageMax(50.0)
        assertThat(hp.value, `is`(13.0))

        hp.restore(37.0)
        assertThat(hp.value, `is`(50.0))

        hp.restorePercentageCurrent(50.0)
        assertThat(hp.value, `is`(75.0))

        hp.restorePercentageMax(15.0)
        assertThat(hp.value, `is`(90.0))
        assertThat(hp.isZero, `is`(false))

        hp.damage(100.0)
        assertThat(hp.value, `is`(0.0))
        assertThat(hp.isZero, `is`(true))
    }
}