/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.gameplay

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.app.FXGLMock
import com.almasb.fxgl.gameplay.notification.NotificationService
import com.almasb.fxgl.ui.Position.BOTTOM
import javafx.scene.paint.Color
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class NotificationServiceTest {

    private lateinit var notificationService: NotificationService

    companion object {
        @BeforeAll
        @JvmStatic fun before() {
            FXGLMock.mock()
        }
    }

    @BeforeEach
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