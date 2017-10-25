/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.gameplay.notification

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.scene.GameScene
import com.almasb.fxgl.ui.Position
import javafx.scene.paint.Color
import java.util.*

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class NotificationServiceProvider : NotificationService {

    private lateinit var notificationViewFactory: NotificationViewFactory
    private lateinit var gameScene: GameScene

    private val queue = ArrayDeque<NotificationView>()

    private var position = Position.TOP

    /**
     * @return notification position
     */
    override fun getPosition() = position

    /**
     * Set position of future notifications.
     *
     * @param position where to show notification
     */
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

    private fun popNotification(notificationView: NotificationView) {
        val removed = gameScene.removeUINode(notificationView)

        // this is called asynchronously so we have to check manually
        if (!removed) {
            return
        }

        if (!queue.isEmpty()) {
            showNotification(queue.poll())
        } else {
            showing = false
        }
    }

    /**
     * Shows a notification with given text.
     * Only 1 notification can be shown at a time.
     * If a notification is being shown already, next notifications
     * will be queued to be shown as soon as space available.
     *
     * @param text the text to show
     */
    override fun pushNotification(text: String) {
        notificationViewFactory = FXGL.getSettings().notificationViewFactory
        gameScene = FXGL.getApp().gameScene

        val notification = Notification(text, textColor, backgroundColor, position)
        val notificationView = notificationViewFactory.newView(notification)
        notificationView.notification = notification
        notificationView.onFinished = Runnable { popNotification(notificationView) }

        if (showing)
            queue.add(notificationView)
        else
            showNotification(notificationView)
    }

    private fun showNotification(notificationView: NotificationView) {
        showing = true
        gameScene.addUINode(notificationView)
        notificationView.show()

        FXGL.getEventBus().fireEvent(NotificationEvent(notificationView.notification))
    }
}