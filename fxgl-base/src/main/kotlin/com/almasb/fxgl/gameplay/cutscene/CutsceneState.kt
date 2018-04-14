/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.gameplay.cutscene

import com.almasb.fxgl.animation.Animation
import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.app.State
import com.almasb.fxgl.app.SubState
import com.almasb.fxgl.app.translate
import javafx.geometry.Point2D
import javafx.scene.shape.Rectangle
import javafx.util.Duration

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
internal abstract class CutsceneState : SubState() {

    private val animation: Animation<*>
    private val animation2: Animation<*>

    init {
        val topLine = Rectangle(FXGL.getAppWidth().toDouble(), 150.0)
        topLine.translateY = -150.0

        val botLine = Rectangle(FXGL.getAppWidth().toDouble(), 200.0)
        botLine.translateY = FXGL.getAppHeight().toDouble()

        children.addAll(topLine, botLine)

        animation = translate(topLine, Point2D.ZERO, Duration.seconds(0.5))
        animation2 = translate(botLine, Point2D(0.0, FXGL.getAppHeight() - 200.0), Duration.seconds(0.5))
    }

    override fun onEnter(prevState: State?) {
        animation2.onFinished = Runnable {
            onOpen()
        }
        animation.start(this)
        animation2.start(this)
    }

    internal fun endCutscene() {
        onClose()
        animation2.onFinished = Runnable {
            FXGL.getApp().stateMachine.popState()
        }
        animation.startReverse(this)
        animation2.startReverse(this)
    }

    abstract fun onOpen()
    abstract fun onClose()
}