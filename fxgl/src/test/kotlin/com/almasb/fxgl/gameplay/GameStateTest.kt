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

package com.almasb.fxgl.gameplay

import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class GameStateTest {

    private lateinit var gameState: GameState

    @Before
    fun setUp() {
        gameState = GameState()
    }

    @Test
    fun `Test put get`() {
        gameState.put("testInt", 5)
        gameState.put("testDouble", 10.5)
        gameState.put("testObject", Dummy("ObjectData"))

        assertThat(gameState.getInt("testInt"), `is`(5))
        assertThat(gameState.getDouble("testDouble"), `is`(10.5))
        assertThat(gameState.getObject<Dummy>("testObject").data, `is`("ObjectData"))

        assertThat(gameState.getInt("testInt"), `is`(gameState.intProperty("testInt").value))
        assertThat(gameState.getDouble("testDouble"), `is`(gameState.doubleProperty("testDouble").value))
        assertThat(gameState.getObject<Dummy>("testObject"), `is`(gameState.objectProperty<Dummy>("testObject").value))
    }

    @Test
    fun `Test set`() {
        gameState.put("testInt", 100)
        assertThat(gameState.getInt("testInt"), `is`(100))

        gameState.setValue("testInt", 200)
        assertThat(gameState.getInt("testInt"), `is`(200))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Do not allow duplicates`() {
        gameState.put("testInt", 1)
        gameState.put("testInt", 2)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Throw if property name not found`() {
        gameState.getBoolean("notFound")
    }

    @Test
    fun `Test increment`() {
        gameState.put("testInt", 1)
        assertThat(gameState.getInt("testInt"), `is`(1))

        gameState.increment("testInt", +9)
        assertThat(gameState.getInt("testInt"), `is`(10))

        gameState.increment("testInt", -10)
        assertThat(gameState.getInt("testInt"), `is`(0))
    }

    @Test
    fun `Test listeners`() {
        gameState.put("testInt", 10)

        var count = 0

        gameState.addListenerKt<Int>("testInt", { prev, now ->
            assertThat(prev, `is`(10))
            count += now
        })

        gameState.setValue("testInt", 25)
        assertThat(count, `is`(25))
    }

    private class Dummy(var data: String) {}
}