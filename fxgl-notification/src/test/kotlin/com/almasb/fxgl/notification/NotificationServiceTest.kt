/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.notification

import com.almasb.fxgl.notification.impl.NotificationServiceProvider
import com.almasb.fxgl.notification.view.XboxNotificationView
import com.almasb.fxgl.scene.SceneService
import com.almasb.fxgl.scene.SubScene
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

    private lateinit var theRoot: Group
    private lateinit var theTimer: Timer

    @BeforeEach
    fun setUp() {
        theRoot = Group()
        theTimer = Timer()

        // TODO: mock scene service?
        val sceneService = object : SceneService() {
            override val overlayRoot: Group
                get() = theRoot
            override val appWidth: Int
                get() = 800
            override val appHeight: Int
                get() = 600
            override val timer: Timer
                get() = theTimer

            override fun pushSubScene(subScene: SubScene) {
            }

            override fun popSubScene() {
            }
        }

        val provider = NotificationServiceProvider()

        val lookup = MethodHandles.lookup()

        InjectInTest.inject(lookup, provider, "sceneService", sceneService)
        InjectInTest.inject(lookup, provider, "notificationViewClass", XboxNotificationView::class.java)

        notificationService = provider
    }

    @Test
    fun `Test settings`() {
        notificationService.backgroundColor = Color.NAVY
        assertThat(notificationService.backgroundColor, `is`(Color.NAVY))
    }

    @Test
    fun `Test push notification`() {
        assertTrue(theRoot.children.isEmpty())
        notificationService.pushNotification("")

        assertTrue(theRoot.children.isNotEmpty())

        // animation in
        theTimer.update(1.0)

        // notification up
        theTimer.update(3.0)

        // animation out
        theTimer.update(1.0)

        assertTrue(theRoot.children.isEmpty())
    }
}