/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.scene.intro

import com.almasb.fxgl.animation.Interpolators
import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.scene.IntroScene
import javafx.animation.*
import javafx.geometry.Point3D
import javafx.scene.Group
import javafx.scene.effect.DropShadow
import javafx.scene.effect.Light
import javafx.scene.effect.Lighting
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.text.Text
import javafx.scene.transform.Rotate
import javafx.util.Duration


/**
 * This is the default FXGL Intro animation.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
class FXGLIntroScene : IntroScene() {

    private val w = FXGL.getAppWidth().toDouble()
    private val h = FXGL.getAppHeight().toDouble()

    private val animation: ParallelTransition

    init {
        val f = makeLetter("F")
        val x = makeLetter("X")
        val g = makeLetter("G")
        val l = makeLetter("L")

        val fxglText = Group(f, x, g, l)

        val light = Light.Distant()
        light.azimuth = -135.0

        val lighting = Lighting()
        lighting.light = light
        lighting.surfaceScale = 15.0

        fxglText.effect = lighting

        val content = Group(fxglText)

        contentRoot.children.addAll(Rectangle(w, h), content)

        val originX = w / 2 - f.layoutBounds.width * 4 / 2
        val dx = f.layoutBounds.width

        f.translateX = originX
        x.translateX = originX + dx
        g.translateX = originX + dx * 2
        l.translateX = originX + dx * 3.3

        val f1 = FadeTransition(Duration.seconds(0.75), f)
        f1.toValue = 1.0

        val f2 = FadeTransition(Duration.seconds(1.5), x)
        f2.toValue = 1.0

        val f3 = FadeTransition(Duration.seconds(2.25), g)
        f3.toValue = 1.0

        val f4 = FadeTransition(Duration.seconds(2.25), l)
        f4.toValue = 1.0

        animation = ParallelTransition(
                f1, f2, f3, f4
        )
        animation.setOnFinished {

            f.effect = null
            x.effect = null
            g.effect = null
            l.effect = null

            val rotate = Rotate(0.0, originX, h / 2 - 150 - f.layoutBounds.height, 0.0, Point3D(0.0, 0.0, 1.0))
            fxglText.transforms.add(rotate)

            val t = Timeline(
                    KeyFrame(Duration.seconds(0.35), KeyValue(rotate.angleProperty(), 70.0)),
                    KeyFrame(Duration.seconds(0.75), KeyValue(rotate.angleProperty(), 30.0)),
                    KeyFrame(Duration.seconds(1.00), KeyValue(rotate.angleProperty(), 100.0))
            )

            t.setOnFinished {
                val t2 = TranslateTransition(Duration.seconds(1.25), fxglText)
                t2.toY = h * 2
                t2.interpolator = Interpolators.EXPONENTIAL.EASE_IN()
                t2.setOnFinished {
                    finishIntro()
                }
                t2.play()
            }

            t.play()
        }
    }

    private fun makeLetter(letter: String) = Text(letter).apply {
        font = FXGL.getUIFactory().newFont(122.0)
        fill = Color.WHITESMOKE
        translateY = h / 2 - 150
        opacity = 0.0
        effect = DropShadow(5.5, Color.BLUE)
    }

    override fun startIntro() = animation.play()
}