/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.notification.view

import com.almasb.fxgl.animation.Animation
import com.almasb.fxgl.animation.AnimationDSL
import com.almasb.fxgl.notification.Notification
import javafx.geometry.Point2D
import javafx.scene.shape.Circle
import javafx.scene.shape.Rectangle
import javafx.scene.text.Font
import javafx.scene.text.Text
import javafx.util.Duration

/**
 * A notification view, inspired by Xbox One achievements.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
class XboxNotificationView : NotificationView() {

    /**
     * Imitates Xbox One style circle.
     */
    private val circle = Circle(30.0, 30.0, 30.0)

    private val bg = Rectangle(400.0, 57.0)
    private val bgClip = Rectangle(bg.width + 15.0, bg.height)

    /**
     * These two will be replacing one another.
     */
    private val text1 = Text().also {
        it.fill = textColor
        it.font = Font.font(18.0)
    }

    private val text2 = Text().also {
        it.fill = textColor
        it.font = Font.font(18.0)
    }

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

        translateX = appWidth / 2 - bg.width / 2 + 200
        translateY = 50.0

        centerTextX(text1, 65.0, 395.0)

        scaleX = 0.0
        scaleY = 0.0

        // make sure we only have circle for proper centering during the first animation
        children.setAll(circle)

        // move the whole view to left
        translateThis = AnimationDSL()
                .duration(Duration.seconds(0.33))
                .translate(this)
                .from(Point2D(translateX, translateY))
                .to(Point2D(translateX - 200, translateY))
                .build()

        // but move the BG to right, creating the "slide out" effect
        translateBG = AnimationDSL()
                .onFinished(Runnable {
                    bg.clip = null
                    text1.isVisible = true
                })
                .duration(Duration.seconds(0.33))
                .translate(bg)
                .from(Point2D(bg.translateX, bg.translateY))
                .to(Point2D(bg.translateX + 400.0, bg.translateY))
                .build()

        scale = AnimationDSL()
                .duration(Duration.seconds(0.3))
                .onFinished(Runnable {

                    // make background appear before the circle
                    children.add(0, bg)
                    children.add(text1)

                    translateThis!!.start()
                    translateBG!!.start()
                })
                .scale(this)
                .from(Point2D.ZERO)
                .to(Point2D(1.0, 1.0))
                .build()

        scale!!.start()
    }

    private var translateThis: Animation<*>? = null
    private var translateBG: Animation<*>? = null

    private var scale: Animation<*>? = null

    private var animText2: Animation<*>? = null
    private var animText1: Animation<*>? = null

    override fun push(notification: Notification) {
        text2.text = notification.message
        text2.translateY = -35.0

        children.add(text2)

        centerTextX(text2, 65.0, 395.0)

        // move text 2 to replace text 1
        animText2 = AnimationDSL()
                .duration(Duration.seconds(0.33))
                .translate(text2)
                .from(Point2D(text2.translateX, text2.translateY))
                .to(Point2D(text2.translateX, 35.0))
                .build()

        // move text 1 down
        animText1 = AnimationDSL()
                .onFinished(Runnable {
                    // when done, just swap them and keep text2 for next reuse
                    text1.translateX = text2.translateX
                    text1.translateY = text2.translateY
                    text1.text = text2.text

                    children.remove(text2)
                })
                .duration(Duration.seconds(0.33))
                .translate(text1)
                .from(Point2D(text1.translateX, text1.translateY))
                .to(Point2D(text1.translateX, text1.translateY + 35.0))
                .build()

        animText2!!.start()
        animText1!!.start()
    }

    override fun playOutAnimation() {
        text1.isVisible = false
        bg.clip = bgClip

        translateBG?.stop()

        translateBG?.onFinished = Runnable {

            children.setAll(circle)

            scale = AnimationDSL()
                    .duration(Duration.seconds(0.3))
                    .scale(this)
                    .from(Point2D(1.0, 1.0))
                    .to(Point2D.ZERO)
                    .build()

            scale!!.start()
        }

        translateBG?.startReverse()
    }

    override fun onUpdate(tpf: Double) {
        translateThis?.onUpdate(tpf)
        translateBG?.onUpdate(tpf)

        scale?.onUpdate(tpf)

        animText2?.onUpdate(tpf)
        animText1?.onUpdate(tpf)
    }

    private fun centerTextX(text: Text, minX: Double, maxX: Double) {
        text.translateX = (minX + maxX) / 2 - text.layoutBounds.width / 2
    }
}