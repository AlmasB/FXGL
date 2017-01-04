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

package com.almasb.fxgl.event

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.app.MockApplicationModule
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.containsString
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class EventBusTest {

    companion object {
        @BeforeClass
        @JvmStatic fun before() {
            FXGL.configure(com.almasb.fxgl.app.MockApplicationModule.get())
        }
    }

    private lateinit var eventBus: com.almasb.fxgl.event.EventBus

    @Before
    fun setUp() {
        eventBus = FXGL.getInstance(com.almasb.fxgl.event.EventBus::class.java)
    }

    @Test
    fun `Test handler scan syntax`() {
        eventBus.scanForHandlers(validObject)

        var count = 0

        try {
            eventBus.scanForHandlers(invalidObject0)
        } catch (e: IllegalArgumentException) {
            assertThat(e.message, containsString("is not of type EventType"))
            count++
        }

        assertThat(count, `is`(1))

        try {
            eventBus.scanForHandlers(invalidObject1)
        } catch (e: IllegalArgumentException) {
            assertThat(e.message, containsString("public static field not found"))
            count++
        }

        assertThat(count, `is`(2))

        try {
            eventBus.scanForHandlers(invalidObject2)
        } catch (e: IllegalArgumentException) {
            assertThat(e.message, containsString("public static field not found"))
            count++
        }

        assertThat(count, `is`(3))

        try {
            eventBus.scanForHandlers(invalidObject3)
        } catch (e: IllegalArgumentException) {
            assertThat(e.message, containsString("must have a single parameter"))
            count++
        }

        assertThat(count, `is`(4))

        try {
            eventBus.scanForHandlers(invalidObject4)
        } catch (e: IllegalAccessException) {
            assertThat(e.message, containsString("can not access"))
            count++
        }

        assertThat(count, `is`(5))
    }

    object validObject {
        @com.almasb.fxgl.event.Handles(eventType = "ANY")
        fun handles(event: com.almasb.fxgl.event.TestEvent) {

        }
    }

    object invalidObject0 {
        @com.almasb.fxgl.event.Handles(eventType = "FAIL0")
        fun handles(event: com.almasb.fxgl.event.TestEvent) {

        }
    }

    object invalidObject1 {
        @com.almasb.fxgl.event.Handles(eventType = "FAIL1")
        fun handles(event: com.almasb.fxgl.event.TestEvent) {

        }
    }

    object invalidObject2 {
        @com.almasb.fxgl.event.Handles(eventType = "FAIL2")
        fun handles(event: com.almasb.fxgl.event.TestEvent) {

        }
    }

    object invalidObject3 {
        @com.almasb.fxgl.event.Handles(eventType = "FAIL3")
        fun handles() {

        }
    }

    object invalidObject4 {
        @com.almasb.fxgl.event.Handles(eventType = "HIDDEN")
        fun handles(event: com.almasb.fxgl.event.TestEvent) {

        }
    }
}