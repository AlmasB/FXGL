/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
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

import com.almasb.fxgl.app.FXGL
import com.google.inject.Inject
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
class SlidingNotificationService
@Inject private constructor(private val gameScene: com.almasb.fxgl.scene.GameScene): com.almasb.fxgl.gameplay.NotificationService {

    private val log = FXGL.getLogger(javaClass)

    private val queue = ArrayDeque<String>()

    private var position = com.almasb.fxgl.ui.Position.TOP

    /**
     * @return notification position
     */
    override fun getPosition() = position

    /**
     * Set position of future notifications.
     *
     * @param position where to show notification
     */
    override fun setPosition(position: com.almasb.fxgl.ui.Position) {
        this.position = position
        if (position == com.almasb.fxgl.ui.Position.BOTTOM) {
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

    private val notificationImpl = NotificationPane()

    init {
        val pane = Pane()
        pane.setPrefSize(FXGL.getSettings().width.toDouble(), 50.0)

        //gameScene.addUINode(notificationImpl)

        //notificationImpl.getStylesheets().add(FXGL.getAssetLoader().loadCSS("test.css").externalForm)
        //notificationImpl.getStyleClass().add("fxgl")

        notificationImpl.styleClass.add(NotificationPane.STYLE_CLASS_DARK)
        notificationImpl.content = pane
        notificationImpl.setOnHidden { popNotification() }

        log.debug { "Service [NotificationService] initialized" }
    }

    private fun popNotification() {
        val removed = gameScene.removeUINode(notificationImpl)

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
        if (showing)
            queue.add(text)
        else
            showNotification(text)
    }

    private var counter = 0

    private fun showNotification(text: String) {
        counter++
        showing = true
        gameScene.addUINode(notificationImpl)

        // controlsFX notification pane cannot show in the same tick
        // so we wait a little bit
        FXGL.getMasterTimer().runOnceAfter( {
            notificationImpl.show(text)
        }, Duration.seconds(0.03));

        FXGL.getEventBus().fireEvent(com.almasb.fxgl.event.NotificationEvent(Notification(text)))

        val id = counter

        FXGL.getMasterTimer().runOnceAfter( {
            if (id == counter)
                notificationImpl.hide()
        }, Duration.seconds(3.0))
    }
}