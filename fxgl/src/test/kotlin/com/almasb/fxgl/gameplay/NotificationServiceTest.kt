/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.gameplay

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.app.FXGL.Companion.configure
import com.almasb.fxgl.app.MockApplicationModule
import com.almasb.fxgl.service.NotificationService
import com.almasb.fxgl.ui.Position.BOTTOM
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
            configure(MockApplicationModule.get())
        }
    }

    @Before
    fun setUp() {
        notificationService = FXGL.getNotificationService()
    }

    @Test
    fun `Test settings`() {
        notificationService.backgroundColor = Color.NAVY;
        assertThat(notificationService.backgroundColor, `is`(Color.NAVY))

        notificationService.position = BOTTOM
        assertThat(notificationService.position, `is`(BOTTOM))
    }

    // uncommented until notification view does not use master timer
//    @Test
//    fun `Test push notification`() {
//        var count = 0
//        val notificationText = "Test"
//
//        FXGL.getEventBus().addEventHandler(NotificationEvent.ANY, {
//
//            assertThat(it.notification.message, `is`(notificationText))
//            count++
//        })
//
//        notificationService.pushNotification(notificationText)
//        assertThat(count, `is`(1))
//    }
}