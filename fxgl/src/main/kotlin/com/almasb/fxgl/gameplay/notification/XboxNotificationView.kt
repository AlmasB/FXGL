/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.gameplay.notification

import com.almasb.fxgl.animation.Animation
import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.ui.Position
import javafx.geometry.Point2D
import javafx.scene.Node
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.shape.Rectangle
import javafx.scene.text.Text
import javafx.util.Duration

/**
 * A notification view, inspired by Xbox One achievements.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
internal class XboxNotificationView : NotificationView() {

    override fun showFirst(notification: Notification) {
        children.clear()

        var x = 0.0
        var y = 0.0

        val position = notification.position
        val message = notification.message

        when (position) {
            Position.LEFT -> {
                x = 50.0
                y = FXGL.getAppHeight() / 2 - (heightOf(message, 12.0) + 10) / 2
            }
            Position.RIGHT -> {
                x = FXGL.getAppWidth()  - (widthOf(message, 12.0) + 20) - 50.0
                y = FXGL.getAppHeight()  / 2 - (heightOf(message, 12.0) + 10) / 2
            }
            Position.TOP -> {
                x = FXGL.getAppWidth()  / 2 - 30.0
                y = 50.0
            }
            Position.BOTTOM -> {
                x = FXGL.getAppWidth()  / 2 - (widthOf(message, 12.0) + 20) / 2
                y = FXGL.getAppHeight()  - (heightOf(message, 12.0) + 10) - 50.0
            }
        }

        translateX = x
        translateY = y

        scaleX = 0.0
        scaleY = 0.0


        val circle = Circle(30.0, 30.0, 30.0, notification.bgColor)

        bg = Rectangle(400.0, 57.0, notification.bgColor.darker())
        bg.arcWidth = 55.0
        bg.arcHeight = 55.0
        bg.translateX = -385.0
        bg.translateY = 1.5
        bg.setOnMouseClicked { hide() }

        bgClip = Rectangle(400.0 + 15.0, 57.0)
        bgClip.translateXProperty().bind(bg.translateXProperty().negate().add(30))

        bg.clip = bgClip


        text = FXGL.getUIFactory().newText(message, Color.WHITE, 15.0)
        text.translateY = 35.0
        text.isVisible = false

        FXGL.getUIFactory().centerTextX(text, 65.0, 395.0)

        children.addAll(circle)

        val a = FXGL.getUIFactory().scale(this, Point2D.ZERO, Point2D(1.0, 1.0), Duration.seconds(0.3))
        a.onFinished = Runnable {
            FXGL.getMasterTimer().runOnceAfter({
                children.add(0, bg)
                children.add(text)

                val anim = FXGL.getUIFactory().translate(this, Point2D(translateX, translateY), Point2D(200.0, 50.0), Duration.seconds(0.33))
                anim.startInPlayState()

                anim2 = FXGL.getUIFactory().translate(bg, Point2D(bg.translateX, bg.translateY), Point2D(bg.translateX + 400.0, bg.translateY), Duration.seconds(0.33))

                anim2.onFinished = Runnable {
                    bg.clip = null
                    text.isVisible = true
                }
                anim2.startInPlayState()

            }, Duration.seconds(0.33))
        }

        a.startInPlayState()
    }

    override fun showRepeated(notification: Notification) {
        val text2 = FXGL.getUIFactory().newText(notification.message, Color.WHITE, 15.0)
        text2.translateY = -35.0

        children.add(text2)

        FXGL.getUIFactory().centerTextX(text2, 65.0, 395.0)

        val anim = FXGL.getUIFactory().translate(text2, Point2D(text2.translateX, text2.translateY), Point2D(text2.translateX, 35.0), Duration.seconds(0.33))
        anim.onFinished = Runnable {

        }
        anim.startInPlayState()

        val anim2 = FXGL.getUIFactory().translate(text, Point2D(text.translateX, text.translateY), Point2D(text.translateX, text.translateY + 35.0), Duration.seconds(0.33))
        anim2.onFinished = Runnable {
            children.remove(text)
            text = text2
        }
        anim2.startInPlayState()
    }

    private lateinit var anim2: Animation<*>
    private lateinit var bgClip: Node
    private lateinit var bg: Rectangle
    private lateinit var text: Text

    init {

    }

    override fun inAnimation(): Animation<*> {
        return FXGL.getUIFactory().scale(this, Point2D.ZERO, Point2D(1.0, 1.0), Duration.seconds(0.3))
    }

    override fun outAnimation(): Animation<*> {
        // this can be used for reverse animation, but looks weird if it's not the last notification

//        bg.clip = bgClip
//        text.isVisible = false
//        anim2.isReverse = true
//        return anim2
        return FXGL.getUIFactory().scale(this, Point2D(1.0, 1.0), Point2D.ZERO, Duration.seconds(0.3))
    }

    override fun duration(): Duration {
        return Duration.seconds(3.0)
    }

    fun widthOf(text: String, fontSize: Double): Double {
        return FXGL.getUIFactory().newText(text, fontSize).getLayoutBounds().getWidth()
    }

    fun heightOf(text: String, fontSize: Double): Double {
        return FXGL.getUIFactory().newText(text, fontSize).getLayoutBounds().getHeight()
    }
}