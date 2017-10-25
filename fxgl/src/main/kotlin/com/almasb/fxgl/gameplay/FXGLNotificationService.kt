/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.gameplay

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.gameplay.Notification
import com.almasb.fxgl.gameplay.NotificationEvent
import com.almasb.fxgl.gameplay.NotificationView
import com.almasb.fxgl.scene.GameScene
import com.almasb.fxgl.gameplay.NotificationService
import com.almasb.fxgl.ui.Position
import javafx.animation.ScaleTransition
import javafx.scene.paint.Color
import javafx.util.Duration
import java.util.*

/**
 * Allows to easily push notifications.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
class FXGLNotificationService : NotificationService {

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

    private var backgroundColor = Color.BLACK

    override fun getBackgroundColor() = backgroundColor

    /**
     * Set background color of notifications.
     *
     * @param backgroundColor the color
     */
    override fun setBackgroundColor(backgroundColor: Color) {
        this.backgroundColor = backgroundColor
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
        gameScene = FXGL.getApp().gameScene
        val notificationView = createNotificationView(text)

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

    private fun createNotificationView(text: String): NotificationView {
        val `in` = ScaleTransition(Duration.seconds(0.3))
        `in`.fromX = 0.0
        `in`.fromY = 0.0
        `in`.toX = 1.0
        `in`.toY = 1.0

        val out = ScaleTransition(Duration.seconds(0.3))
        out.fromX = 1.0
        out.fromY = 1.0
        out.toX = 0.0
        out.toY = 0.0

        val notificationView = NotificationView(Notification(text), backgroundColor, `in`, out)
        notificationView.scaleX = 0.0
        notificationView.scaleY = 0.0

        var x = 0.0
        var y = 0.0

        when (position) {
            Position.LEFT -> {
                x = 50.0
                y = gameScene.height / 2 - (heightOf(text, 12.0) + 10) / 2
            }
            Position.RIGHT -> {
                x = gameScene.width - (widthOf(text, 12.0) + 20) - 50.0
                y = gameScene.height / 2 - (heightOf(text, 12.0) + 10) / 2
            }
            Position.TOP -> {
                x = gameScene.width / 2 - (widthOf(text, 12.0) + 20) / 2
                y = 50.0
            }
            Position.BOTTOM -> {
                x = gameScene.width / 2 - (widthOf(text, 12.0) + 20) / 2
                y = gameScene.height - (heightOf(text, 12.0) + 10) - 50.0
            }
        }

        notificationView.translateX = x
        notificationView.translateY = y

        `in`.node = notificationView
        out.node = notificationView
        out.setOnFinished { e -> popNotification(notificationView) }

        return notificationView
    }

    fun widthOf(text: String, fontSize: Double): Double {
        return FXGL.getUIFactory().newText(text, fontSize).getLayoutBounds().getWidth()
    }

    fun heightOf(text: String, fontSize: Double): Double {
        return FXGL.getUIFactory().newText(text, fontSize).getLayoutBounds().getHeight()
    }
}
