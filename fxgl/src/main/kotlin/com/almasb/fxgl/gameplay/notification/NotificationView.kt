/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.gameplay.notification

import com.almasb.fxgl.animation.Animation
import com.almasb.fxgl.app.FXGL
import javafx.scene.layout.Pane
import javafx.util.Duration

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
abstract class NotificationView : Pane() {

    internal lateinit var onFinished: Runnable
    internal lateinit var notification: Notification

    abstract fun inAnimation(): Animation<*>
    abstract fun outAnimation(): Animation<*>
    abstract fun duration(): Duration

    private var hideCalled = false

    fun show() {
        val anim = inAnimation()
        // TODO: when we have superstate, use that
        anim.onFinished = Runnable {
            FXGL.getMasterTimer().runOnceAfter({ hide() }, duration())
        }
        anim.startInPlayState()
    }

    fun hide() {
        if (hideCalled)
            return

        hideCalled = true

        val anim = outAnimation()
        anim.onFinished = onFinished
        // TODO: when we have superstate, use that
        anim.startInPlayState()
    }
}