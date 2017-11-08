/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.gameplay.notification

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.core.reflect.ReflectionUtils
import com.almasb.fxgl.scene.GameScene
import com.almasb.fxgl.ui.Position
import javafx.scene.paint.Color
import javafx.util.Duration
import java.util.*

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class NotificationServiceProvider : NotificationService {

    private val gameScene by lazy { FXGL.getApp().gameScene }

    private val notificationView by lazy { ReflectionUtils.newInstance(FXGL.getSettings().notificationViewFactory) }

    private val queue = ArrayDeque<Notification>()

    private var position = Position.TOP

    override fun getPosition() = position

    override fun setPosition(position: Position) {
        this.position = position
    }

    private var backgroundColor = Color.LIGHTGREEN
    private var textColor = Color.WHITE

    override fun getBackgroundColor() = backgroundColor

    override fun setBackgroundColor(backgroundColor: Color) {
        this.backgroundColor = backgroundColor
    }

    override fun getTextColor(): Color = textColor

    override fun setTextColor(textColor: Color) {
        this.textColor = textColor
    }

    private var showing = false

    /**
     * Shows a notification with given text.
     * Only 1 notification can be shown at a time.
     * If a notification is being shown already, next notifications
     * will be queued to be shown as soon as space available.
     *
     * @param text the text to show
     */
    override fun pushNotification(text: String) {
        val notification = Notification(text, textColor, backgroundColor, position)

        if (showing)
            queue.add(notification)
        else
            showFirstNotification(notification)
    }

    private fun showFirstNotification(notification: Notification) {
        showing = true
        gameScene.addUINode(notificationView)

        // TODO: showFirstTime
        notificationView.showFirst(notification)

        FXGL.getEventBus().fireEvent(NotificationEvent(notification))

        // schedule next
        FXGL.getMasterTimer().runOnceAfter(Runnable {
            popNotification()
        }, Duration.seconds(3.0))
    }

    private fun popNotification() {
        if (!queue.isEmpty()) {
            showRepeatedNotification(queue.poll())
        } else {
            val removed = gameScene.removeUINode(notificationView)

            showing = false

            // this is called asynchronously so we have to check manually
//            if (!removed) {
//                return
//            }
        }
    }

    private fun showRepeatedNotification(notification: Notification) {
        // TODO: showRepeated
        notificationView.showRepeated(notification)

        FXGL.getEventBus().fireEvent(NotificationEvent(notification))

        // schedule next
        FXGL.getMasterTimer().runOnceAfter(Runnable {
            popNotification()
        }, Duration.seconds(3.0))
    }
}