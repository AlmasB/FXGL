/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.gameplay.notification

import com.almasb.fxgl.animation.ParallelAnimation
import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.app.centerTextX
import com.almasb.fxgl.app.scale
import com.almasb.fxgl.app.translate
import com.almasb.fxgl.ui.Position
import javafx.geometry.Point2D
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.shape.Rectangle
import javafx.util.Duration

/**
 * A notification view, inspired by Xbox One achievements.
 *
 * https://github.com/AlmasB/FXGL/issues/487
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
internal class XboxNotificationView : NotificationView() {

    /**
     * Imitates Xbox One style circle.
     */
    private val circle = Circle(30.0, 30.0, 30.0)

    private val bg = Rectangle(400.0, 57.0)
    private val bgClip = Rectangle(bg.width + 15.0, bg.height)

    /**
     * These two will be replacing one another.
     */
    private val text1 = FXGL.getUIFactory().newText("", Color.WHITE, 15.0)
    private val text2 = FXGL.getUIFactory().newText("", Color.WHITE, 15.0)

    private lateinit var bgAnimation: ParallelAnimation

    init {
        bg.arcWidth = 55.0
        bg.arcHeight = 55.0

        bgClip.translateXProperty().bind(bg.translateXProperty().negate().add(30))
    }

    override fun playInAnimation() {
        // reset the view to default so we can play our nice animation
        bg.translateX = -385.0
        bg.translateY = 1.5
        bg.fill = backgroundColor.darker()
        bg.clip = bgClip

        circle.fill = backgroundColor.brighter()

        text1.translateY = 35.0
        text1.isVisible = false
        text1.fill = textColor
        text1.text = ""

        text2.fill = textColor

        when (position) {
            Position.LEFT -> {
                translateX = 50.0
                translateY = FXGL.getAppHeight() / 2 - (text1.layoutBounds.width + 10) / 2
            }
            Position.RIGHT -> {
                translateX = FXGL.getAppWidth()  - (text1.layoutBounds.width + 20) - 50.0
                translateY = FXGL.getAppHeight()  / 2 - (text1.layoutBounds.height + 10) / 2
            }
            Position.TOP -> {
                translateX = FXGL.getAppWidth() / 2 - 30.0
                translateY = 50.0
            }
            Position.BOTTOM -> {
                translateX = FXGL.getAppWidth()  / 2 - (text1.layoutBounds.width + 20) / 2
                translateY = FXGL.getAppHeight()  - (text1.layoutBounds.height + 10) - 50.0
            }
        }

        centerTextX(text1, 65.0, 395.0)

        scaleX = 0.0
        scaleY = 0.0

        // make sure we only have circle for proper centering during the first animation
        children.setAll(circle)




        // move the whole view to left
        val translateThis = translate(this, Point2D(translateX, translateY), Point2D(FXGL.getAppWidth() / 4.0, translateY), Duration.seconds(0.33))

        // but move the BG to right, creating the "slide out" effect
        val translateBG = translate(bg, Point2D(bg.translateX, bg.translateY), Point2D(bg.translateX + 400.0, bg.translateY), Duration.seconds(0.33))

        bgAnimation = ParallelAnimation(1, translateThis, translateBG)
        bgAnimation.onFinished = Runnable {
            bg.clip = null
            text1.isVisible = true
        }





        val scale = scale(this, Point2D.ZERO, Point2D(1.0, 1.0), Duration.seconds(0.3))
        scale.onFinished = Runnable {
            FXGL.getMasterTimer().runOnceAfter({
                // make background appear before the circle
                children.add(0, bg)
                children.add(text1)

                bgAnimation.startInPlayState()
            }, Duration.seconds(0.33))
        }

        scale.startInPlayState()
    }

    override fun push(notification: Notification) {
        text2.text = notification.message
        text2.translateY = -35.0

        children.add(text2)

        centerTextX(text2, 65.0, 395.0)

        // move text 2 to replace text 1
        val anim = translate(text2, Point2D(text2.translateX, text2.translateY), Point2D(text2.translateX, 35.0), Duration.seconds(0.33))
        anim.startInPlayState()

        // move text 1 down
        val anim2 = translate(text1, Point2D(text1.translateX, text1.translateY), Point2D(text1.translateX, text1.translateY + 35.0), Duration.seconds(0.33))
        anim2.onFinished = Runnable {
            // when done, just swap them and keep text2 for next reuse
            text1.translateX = text2.translateX
            text1.translateY = text2.translateY
            text1.text = text2.text

            children.remove(text2)
        }
        anim2.startInPlayState()
    }

    override fun playOutAnimation() {
        text1.isVisible = false
        bg.clip = bgClip

        bgAnimation.onFinished = Runnable {
            children.setAll(circle)
            scale(this, Point2D(1.0, 1.0), Point2D.ZERO, Duration.seconds(0.3)).startInPlayState()
        }
        bgAnimation.stop()
        bgAnimation.startReverse(FXGL.getApp().stateMachine.playState)
    }
}