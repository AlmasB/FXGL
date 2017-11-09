/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.gameplay.notification

import javafx.scene.layout.Pane

/**
 * A notification view / pane is added when a notification is pushed and is being
 * displayed.
 * Each notification lasts 3 seconds.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
abstract class NotificationView : Pane() {

    abstract fun showFirst()
    abstract fun showRepeated(notification: Notification)
    abstract fun showLast()
}