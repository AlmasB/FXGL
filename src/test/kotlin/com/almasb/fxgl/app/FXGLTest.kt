/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2016 AlmasB (almaslvl@gmail.com)
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

package com.almasb.fxgl.app

import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Assert.assertTrue
import org.junit.BeforeClass
import org.junit.Test

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class FXGLTest {

    companion object {
        @BeforeClass
        @JvmStatic fun before() {
            FXGL.mockServices(MockServicesModule())
        }
    }

    @Test
    fun `GameApplication subclass is a singleton across FXGL`() {
        assertTrue(FXGL.getApp() === FXGL.getAppCast<MockGameApplication>())
        assertTrue(FXGL.getApp() === MockGameApplication.INSTANCE)
    }

    @Test
    fun `fail if property not found`() {
        var count = 0

        try {
            FXGL.getBoolean("TestBoolean")
        } catch (e: IllegalArgumentException) {
            count++
        }

        assertThat(count, `is`(1))

        try {
            FXGL.getInt("TestInt")
        } catch (e: IllegalArgumentException) {
            count++
        }

        assertThat(count, `is`(2))

        try {
            FXGL.getDouble("TestDouble")
        } catch (e: IllegalArgumentException) {
            count++
        }

        assertThat(count, `is`(3))

        try {
            FXGL.getString("TestString")
        } catch (e: IllegalArgumentException) {
            count++
        }

        assertThat(count, `is`(4))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Property key is case sensitive`() {
        FXGL.setProperty("UPPERCASE", "value")

        // must throw
        FXGL.getString("uppercase")
    }

    @Test
    fun `Set and Get property`() {
        FXGL.setProperty("StringKey", "StringValue")
        assertThat(FXGL.getString("StringKey"), `is`("StringValue"))

        FXGL.setProperty("KeyForInt", 1234)
        assertThat(FXGL.getInt("KeyForInt"), `is`(1234))

        FXGL.setProperty("KeyForDouble", 4321.5)
        assertThat(FXGL.getDouble("KeyForDouble"), `is`(4321.5))

        FXGL.setProperty("KeyForBoolean", true)
        assertThat(FXGL.getBoolean("KeyForBoolean"), `is`(true))
    }
}