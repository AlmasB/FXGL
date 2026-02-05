/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
@file:Suppress("JAVA_MODULE_DOES_NOT_DEPEND_ON_MODULE")
package com.almasb.fxgl.notification

import com.almasb.fxgl.core.collection.PropertyMap
import com.almasb.fxgl.event.EventBus
import com.almasb.fxgl.input.Input
import com.almasb.fxgl.notification.impl.NotificationServiceProvider
import com.almasb.fxgl.notification.view.XboxNotificationView
import com.almasb.fxgl.scene.Scene
import com.almasb.fxgl.scene.SceneService
import com.almasb.fxgl.scene.SubScene
import com.almasb.fxgl.test.InjectInTest
import com.almasb.fxgl.time.Timer
import javafx.beans.property.ReadOnlyDoubleProperty
import javafx.beans.property.ReadOnlyDoubleWrapper
import javafx.scene.Group
import javafx.scene.paint.Color
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.lang.invoke.MethodHandles
import java.util.*

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

        val sceneService = object : SceneService() {
            override val overlayRoot: Group
                get() = theRoot

            override fun prefWidthProperty(): ReadOnlyDoubleProperty {
                return ReadOnlyDoubleWrapper(800.0).readOnlyProperty
            }

            override fun prefHeightProperty(): ReadOnlyDoubleProperty {
                return ReadOnlyDoubleWrapper(600.0).readOnlyProperty
            }
            override val eventBus: EventBus
                get() = EventBus()
            override val input: Input
                get() = Input()
            override val timer: Timer
                get() = theTimer
            override val worldProperties: PropertyMap
                get() = PropertyMap()

            override val currentScene: Scene
                get() = object : Scene() {}

            override val introScene: Optional<Scene>
                get() = Optional.ofNullable(object : Scene() {})

            override val loadingScene: Optional<Scene>
                get() = Optional.ofNullable(object : Scene() {})

            override val mainMenuScene: Optional<Scene>
                get() = Optional.ofNullable(object : Scene() {})

            override val gameMenuScene: Optional<Scene>
                get() = Optional.ofNullable(object : Scene() {})

            override val gameScene: Scene
                get() = object : Scene() {}

            override fun isInHierarchy(scene: Scene): Boolean {
                return false
            }

            override fun pushSubScene(subScene: SubScene) {
            }

            override fun popSubScene() {
            }
        }

        val provider = NotificationServiceProvider()

        val lookup = MethodHandles.lookup()
        val injectMap = mapOf(
                "sceneService" to sceneService,
                "notificationViewClass" to XboxNotificationView::class.java)

        InjectInTest.inject(lookup, provider, injectMap)

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
        notificationService.onUpdate(1.0)

        // notification up
        theTimer.update(3.0)
        notificationService.onUpdate(3.0)

        // animation out
        theTimer.update(1.0)
        notificationService.onUpdate(1.0)

        assertTrue(theRoot.children.isEmpty())
    }

    @Test
    fun `Test push multiple notifications`() {
        assertTrue(theRoot.children.isEmpty())
        notificationService.pushNotification("")
        notificationService.pushNotification("")

        assertTrue(theRoot.children.isNotEmpty())

        // animation in
        theTimer.update(1.0)
        notificationService.onUpdate(1.0)

        // notification 1 up
        theTimer.update(3.0)
        notificationService.onUpdate(3.0)

        assertTrue(theRoot.children.isNotEmpty())

        // notification 2 up
        theTimer.update(3.0)
        notificationService.onUpdate(3.0)

        // animation out
        theTimer.update(1.0)
        notificationService.onUpdate(1.0)

        assertTrue(theRoot.children.isEmpty())
    }
}