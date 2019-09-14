/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.notification

import com.almasb.fxgl.notification.impl.NotificationServiceProvider
import com.almasb.fxgl.notification.view.XboxNotificationView
import com.almasb.fxgl.test.InjectInTest
import com.almasb.fxgl.time.Timer
import javafx.scene.Group
import javafx.scene.paint.Color
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.lang.invoke.MethodHandles

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class NotificationServiceTest {

    private lateinit var notificationService: NotificationService

    private lateinit var overlayRoot: Group
    private lateinit var timer: Timer

    @BeforeEach
    fun setUp() {
        overlayRoot = Group()
        timer = Timer()

        val provider = NotificationServiceProvider()

        val lookup = MethodHandles.lookup()

        InjectInTest.inject(lookup, provider, "timer", timer)
        InjectInTest.inject(lookup, provider, "notificationViewClass", XboxNotificationView::class.java)
        InjectInTest.inject(lookup, provider, "root", overlayRoot)

        notificationService = provider
    }

    @Test
    fun `Test settings`() {
        notificationService.backgroundColor = Color.NAVY
        assertThat(notificationService.backgroundColor, `is`(Color.NAVY))
    }

    @Test
    fun `Test push notification`() {
        assertTrue(overlayRoot.children.isEmpty())
        notificationService.pushNotification("")

        assertTrue(overlayRoot.children.isNotEmpty())

        // animation in
        timer.update(1.0)

        // notification up
        timer.update(3.0)

        // animation out
        timer.update(1.0)

        assertTrue(overlayRoot.children.isEmpty())
    }
}