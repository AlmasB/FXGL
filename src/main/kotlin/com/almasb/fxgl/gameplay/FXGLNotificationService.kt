/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2016 AlmasB (almaslvl@gmail.com)
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

import com.almasb.fxgl.app.GameApplication
import com.almasb.fxgl.app.ServiceType
import com.almasb.fxgl.event.AchievementEvent
import com.almasb.fxgl.event.NotificationEvent
import com.almasb.fxgl.logging.FXGLLogger
import com.almasb.fxgl.scene.GameScene
import com.almasb.fxgl.ui.Position
import com.almasb.fxgl.ui.UIFactory
import com.google.inject.Inject
import com.google.inject.Singleton
import javafx.animation.ScaleTransition
import javafx.scene.paint.Color
import javafx.util.Duration
import java.util.*

/**
 * Allows to easily push notifications.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
@Singleton
class FXGLNotificationService
@Inject
private constructor(private val gameScene: GameScene) : NotificationService {

    companion object {
        private val log = FXGLLogger.getLogger("FXGL.NotificationService")
    }

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

    init {
        GameApplication.getService(ServiceType.EVENT_BUS)
                .addEventHandler(AchievementEvent.ANY) { event ->
                    pushNotification("You got an achievement! ${event.achievement.name}") }

        log.finer { "Service [NotificationService] initialized" }
    }

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

        GameApplication.getService(ServiceType.EVENT_BUS).fireEvent(NotificationEvent(notificationView.notification))
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
                y = gameScene.height / 2 - (UIFactory.heightOf(text, 12.0) + 10) / 2
            }
            Position.RIGHT -> {
                x = gameScene.width - (UIFactory.widthOf(text, 12.0) + 20) - 50.0
                y = gameScene.height / 2 - (UIFactory.heightOf(text, 12.0) + 10) / 2
            }
            Position.TOP -> {
                x = gameScene.width / 2 - (UIFactory.widthOf(text, 12.0) + 20) / 2
                y = 50.0
            }
            Position.BOTTOM -> {
                x = gameScene.width / 2 - (UIFactory.widthOf(text, 12.0) + 20) / 2
                y = gameScene.height - (UIFactory.heightOf(text, 12.0) + 10) - 50.0
            }
        }

        notificationView.translateX = x
        notificationView.translateY = y

        `in`.node = notificationView
        out.node = notificationView
        out.setOnFinished { e -> popNotification(notificationView) }

        return notificationView
    }
}
