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

package com.almasb.fxgl.input

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.app.MockServicesModule
import javafx.scene.input.KeyCode
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.hasItem
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class InputTest {

    private lateinit var input: Input

    companion object {
        @BeforeClass
        @JvmStatic fun before() {
            FXGL.mockServices(MockServicesModule())
        }
    }

    @Before
    fun setUp() {
        input = FXGL.getInstance(Input::class.java)
    }

    @Test
    fun `Test registering input does not affect mocking`() {
        assertThat(input.isRegisterInput, `is`(true))

        var calls = 0

        input.addAction(object : UserAction("Test") {
            override fun onActionBegin() {
                calls = 1
            }

            override fun onActionEnd() {
                calls = -1
            }
        }, KeyCode.A)

        input.mockKeyPress(KeyCode.A)
        assertThat(calls, `is`(1))

        input.mockKeyRelease(KeyCode.A)
        assertThat(calls, `is`(-1))

        // this must not affect mocking
        input.isRegisterInput = false

        input.mockKeyPress(KeyCode.A)
        assertThat(calls, `is`(1))

        input.mockKeyRelease(KeyCode.A)
        assertThat(calls, `is`(-1))
    }

    @Test
    fun `Test processing input affects mocking`() {
        assertThat(input.isProcessInput, `is`(true))

        var calls = 0

        input.addAction(object : UserAction("Test") {
            override fun onActionBegin() {
                calls = 1
            }

            override fun onActionEnd() {
                calls = -1
            }
        }, KeyCode.A)

        input.mockKeyPress(KeyCode.A)
        assertThat(calls, `is`(1))

        input.mockKeyRelease(KeyCode.A)
        assertThat(calls, `is`(-1))

        // this must affect mocking
        input.isProcessInput = false

        input.mockKeyPress(KeyCode.A)
        assertThat(calls, `is`(-1))

        input.mockKeyRelease(KeyCode.A)
    }

    @Test
    fun `Mocking must not trigger isHeld`() {
        assertThat(input.isHeld(KeyCode.A), `is`(false))

        input.mockKeyPress(KeyCode.A)

        assertThat(input.isHeld(KeyCode.A), `is`(false))

        input.mockKeyRelease(KeyCode.A)
    }

    @Test
    fun testAddAction() {
        val action = object : UserAction("Action1") {}

        input.addAction(action, KeyCode.A)

        assertThat(input.bindings.keys, hasItem(action))

        val trigger = input.bindings[action]

        assertThat(trigger is KeyTrigger, `is`(true))
        assertThat((trigger as KeyTrigger).key, `is`(KeyCode.A))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Do not allow UserActions with same name`() {
        input.addAction(object : UserAction("Action1") {}, KeyCode.A)
        input.addAction(object : UserAction("Action1") {}, KeyCode.B)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Do not allow bindings to same key`() {
        input.addAction(object : UserAction("Action1") {}, KeyCode.A)
        input.addAction(object : UserAction("Action2") {}, KeyCode.A)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Do not allow binding to Ctrl`() {
        input.addAction(object : UserAction("Test") {}, KeyCode.CONTROL)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Do not allow binding to Shift`() {
        input.addAction(object : UserAction("Test") {}, KeyCode.SHIFT)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Do not allow binding to Alt`() {
        input.addAction(object : UserAction("Test") {}, KeyCode.ALT)
    }

    @Test
    fun testRebind() {
        val action = object : UserAction("Action1") {}

        input.addAction(action, KeyCode.A)
        input.addAction(object : UserAction("Action2") {}, KeyCode.B)

        // binding to existing key must not succeed
        var ok = input.rebind(action, KeyCode.B)
        assertThat(ok, `is`(false))

        ok = input.rebind(action, KeyCode.C)
        assertThat(ok, `is`(true))

        val trigger = input.bindings[action]

        assertThat(trigger is KeyTrigger, `is`(true))
        assertThat((trigger as KeyTrigger).key, `is`(KeyCode.C))
    }

    @Test
    fun testAddInputMapping() {
        assertThat(input.bindings.size, `is`(0))

        input.addInputMapping(InputMapping("TestAction", KeyCode.A))

        input.scanForUserActions(this)

        assertThat(input.bindings.size, `is`(1))

        val trigger = input.bindings.values.single()

        assertThat(trigger is KeyTrigger, `is`(true))
        assertThat((trigger as KeyTrigger).key, `is`(KeyCode.A))
    }

    @OnUserAction(name = "TestAction", type = ActionType.ON_ACTION_BEGIN)
    fun onCall() {

    }
}
