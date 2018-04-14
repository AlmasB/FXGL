/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ui

import com.almasb.fxgl.animation.ParallelAnimation
import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.app.fadeIn
import com.almasb.fxgl.app.fadeOut
import com.almasb.fxgl.app.translate
import javafx.geometry.Point2D
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.paint.CycleMethod
import javafx.scene.paint.LinearGradient
import javafx.scene.paint.Stop
import javafx.scene.shape.Rectangle
import javafx.scene.text.Text
import javafx.util.Duration

/**
 * By default level text is invisible and [animateIn] is used to reveal the text
 * via an animation.
 * Then [animateOut] can be used to make this node invisible again.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class LevelText(levelName: String) : StackPane() {

    private val text: Text = FXGL.getUIFactory().newText(levelName, Color.WHITESMOKE, 46.0)
    private val bg = Rectangle(text.layoutBounds.width + 100, text.layoutBounds.height + 20)

    init {
        translateX = FXGL.getAppWidth() / 2 - text.layoutBounds.width / 2
        translateY = FXGL.getAppHeight() / 3.0
        opacity = 0.0

        with(bg) {
            arcWidth = 35.0
            arcHeight = 35.0
            fill = LinearGradient(0.0, 0.0, 1.0, 0.0, true, CycleMethod.NO_CYCLE,
                    Stop(0.0, Color.TRANSPARENT),
                    Stop(0.5, Color.color(0.0, 0.0, 0.0, 0.85)),
                    Stop(1.0, Color.TRANSPARENT)
            )
        }

        children.addAll(bg, text)
    }

    fun animateIn() {
        ParallelAnimation(
                fadeIn(this@LevelText, Duration.seconds(1.0)),
                translate(text, Point2D(-20.0, 0.0), Point2D.ZERO, Duration.seconds(1.0))
        ).startInPlayState()
    }

    fun animateOut() {
        fadeOut(this, Duration.seconds(1.0)).startInPlayState()
    }
}