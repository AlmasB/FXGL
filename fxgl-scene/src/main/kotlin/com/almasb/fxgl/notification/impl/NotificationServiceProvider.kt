/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.notification.impl

import com.almasb.fxgl.core.Inject
import com.almasb.fxgl.core.reflect.ReflectionUtils
import com.almasb.fxgl.notification.Notification
import com.almasb.fxgl.notification.NotificationService
import com.almasb.fxgl.notification.view.NotificationView
import com.almasb.fxgl.scene.SceneService
import javafx.scene.paint.Color
import javafx.util.Duration
import java.util.*

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class NotificationServiceProvider : NotificationService() {

    private val ANIMATION_DURATION = Duration.seconds(1.0)
    private val NOTIFICATION_DURATION = Duration.seconds(3.0)

    override var backgroundColor: Color = Color.BLACK
    override var textColor: Color = Color.WHITE

    private val queue = ArrayDeque<Notification>()

    private var showing = false

    private lateinit var sceneService: SceneService

    private val overlayRoot
        get() = sceneService.overlayRoot

    private val timer
        get() = sceneService.timer

    @Inject("notificationViewClass")
    private lateinit var notificationViewClass: Class<out NotificationView>

    private val notificationView by lazy {
        ReflectionUtils.newInstance(notificationViewClass).also {
            it.appWidth = sceneService.prefWidth.toInt()
            it.appHeight = sceneService.prefHeight.toInt()
        }
    }

    /**
     * Shows a notification with given text.
     * Only 1 notification can be shown at a time.
     * If a notification is being shown already, next notifications
     * will be queued to be shown as soon as space available.
     *
     * @param message the text to show
     */
    override fun pushNotification(message: String) {
        val notification = Notification(message)

        queue.add(notification)

        if (!showing) {
            showFirstNotification()
        }
    }

    private fun nextNotification() {
        if (queue.isNotEmpty()) {
            val n = queue.poll()
            notificationView.push(n)

            fireAndScheduleNextNotification(n)
        } else {
            notificationView.playOutAnimation()

            timer.runOnceAfter(this::checkLastPop, ANIMATION_DURATION)
        }
    }

    private fun checkLastPop() {
        if (queue.isEmpty()) {
            overlayRoot.children -= notificationView
            showing = false
        } else {
            // play in animation
            notificationView.playInAnimation()

            timer.runOnceAfter(this::nextNotification, ANIMATION_DURATION)
        }
    }

    private fun showFirstNotification() {
        showing = true
        overlayRoot.children += notificationView

        // play in animation
        notificationView.playInAnimation()

        timer.runOnceAfter(this::nextNotification, ANIMATION_DURATION)
    }

    private fun fireAndScheduleNextNotification(notification: Notification) {
        // schedule next
        timer.runOnceAfter(this::nextNotification, NOTIFICATION_DURATION)
    }

    override fun onUpdate(tpf: Double) {
        notificationView.onUpdate(tpf)
    }
}