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

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.app.MockApplicationModule
import com.almasb.fxgl.event.NotificationEvent
import com.almasb.fxgl.ui.Position
import javafx.scene.paint.Color
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class NotificationServiceTest {

    private lateinit var notificationService: NotificationService

    companion object {
        @BeforeClass
        @JvmStatic fun before() {
            FXGL.configure(MockApplicationModule.get())
        }
    }

    @Before
    fun setUp() {
        notificationService = FXGL.getInstance(NotificationService::class.java)
    }

    @Test
    fun `Test settings`() {
        notificationService.backgroundColor = Color.NAVY;
        assertThat(notificationService.backgroundColor, `is`(Color.NAVY))

        notificationService.position = Position.BOTTOM
        assertThat(notificationService.position, `is`(Position.BOTTOM))
    }

    @Test
    fun `Test push notification`() {
        var count = 0
        val notificationText = "Test"

        FXGL.getEventBus().addEventHandler(NotificationEvent.ANY, {

            assertThat(it.notification.message, `is`(notificationText))
            count++
        })

        notificationService.pushNotification(notificationText)
        assertThat(count, `is`(1))
    }
}