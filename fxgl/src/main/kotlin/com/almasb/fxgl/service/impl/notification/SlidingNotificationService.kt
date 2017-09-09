/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.service.impl.notification

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.gameplay.Notification
import com.almasb.fxgl.gameplay.NotificationEvent
import com.almasb.fxgl.service.NotificationService
import com.almasb.fxgl.ui.Position
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.util.Duration
import org.controlsfx.control.NotificationPane
import java.util.*

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class SlidingNotificationService : NotificationService {

    private val queue = ArrayDeque<String>()

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
        if (position == Position.BOTTOM) {
            notificationImpl.isShowFromTop = false
            notificationImpl.translateY = FXGL.getSettings().height - 50.0
        } else {
            notificationImpl.isShowFromTop = true
            notificationImpl.translateY = 0.0
        }
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

    private val pane = Pane()
    private val notificationImpl = NotificationPane()

    init {
        notificationImpl.styleClass.add(NotificationPane.STYLE_CLASS_DARK)
        notificationImpl.content = pane
        notificationImpl.setOnHidden { popNotification() }
    }

    private fun popNotification() {
        val removed = FXGL.getApp().gameScene.removeUINode(notificationImpl)

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
        pane.setPrefSize(FXGL.getSettings().width.toDouble(), 50.0)

        if (showing)
            queue.add(text)
        else
            showNotification(text)
    }

    private var counter = 0

    private fun showNotification(text: String) {
        counter++
        showing = true
        FXGL.getApp().gameScene.addUINode(notificationImpl)

        // controlsFX notification pane cannot show in the same tick
        // so we wait a little bit
        FXGL.getMasterTimer().runOnceAfter( {
            notificationImpl.show(text)
        }, Duration.seconds(0.03));

        FXGL.getEventBus().fireEvent(NotificationEvent(Notification(text)))

        val id = counter

        FXGL.getMasterTimer().runOnceAfter( {
            if (id == counter)
                notificationImpl.hide()
        }, Duration.seconds(3.0))
    }
}