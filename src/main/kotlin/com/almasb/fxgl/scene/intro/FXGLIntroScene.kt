/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2016 AlmasB (almaslvl@gmail.com)
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

package com.almasb.fxgl.scene.intro

import com.almasb.fxgl.scene.IntroScene
import com.almasb.fxgl.settings.ReadOnlyGameSettings
import com.almasb.fxgl.ui.UIFactory
import com.almasb.fxgl.util.Version
import javafx.animation.FadeTransition
import javafx.animation.ParallelTransition
import javafx.animation.RotateTransition
import javafx.animation.TranslateTransition
import javafx.geometry.Point3D
import javafx.scene.Group
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.text.Text
import javafx.util.Duration

/**
 * This is the default FXGL Intro animation.

 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
class FXGLIntroScene(settings: ReadOnlyGameSettings) : IntroScene(settings) {

    private val w: Double
    private val h: Double

    private val animation: ParallelTransition

    init {
        w = settings.width.toDouble()
        h = settings.height.toDouble()

        val f = makeLetter("F")
        val x = makeLetter("X")
        val g = makeLetter("G")
        val l = makeLetter("L")

        x.translateY = h + 70

        g.translateX = w

        l.translateX = w + 70
        l.translateY = h
        l.rotate = 180.0

        val fxglText = Group(f, x, g, l)

        val poweredText = makePoweredBy()
        val version = makeVersion()

        val fireworks = FireworksPane(w, h)

        val content = Group(fxglText, poweredText, version, fireworks)

        root.children.addAll(Rectangle(w, h), content)

        val originX = w / 2 - f.layoutBounds.width * 4 / 2
        val dx = f.layoutBounds.width

        val tt = TranslateTransition(Duration.seconds(1.0), f)
        tt.toX = originX
        tt.toY = h / 2

        val tt2 = TranslateTransition(Duration.seconds(1.0), x)
        tt2.toX = originX + dx
        tt2.toY = h / 2

        val tt3 = TranslateTransition(Duration.seconds(1.0), g)
        tt3.toX = originX + dx * 2
        tt3.toY = h / 2

        val tt4 = TranslateTransition(Duration.seconds(1.0), l)
        tt4.toX = originX + dx * 3.3
        tt4.toY = h / 2

        fireworks.play()

        animation = ParallelTransition(tt, tt2, tt3, tt4)
        animation.setOnFinished { event ->
            poweredText.isVisible = true
            version.isVisible = true

            val rt = RotateTransition(Duration.seconds(1.0), l)
            rt.delay = Duration.seconds(0.66)
            rt.axis = Point3D(0.0, 0.0, 1.0)
            rt.byAngle = -180.0
            rt.setOnFinished { e ->
                val ft = FadeTransition(Duration.seconds(2.5), root)
                ft.toValue = 0.0
                ft.setOnFinished { e1 ->
                    fireworks.stop()
                    finishIntro()
                }
                ft.play()
            }
            rt.play()
        }
    }

    private fun makeLetter(letter: String): Text {
        with(Text(letter)) {
            font = UIFactory.newFont(72.0)
            fill = Color.WHITESMOKE
            return this
        }
    }

    private fun makeVersion(): Text {
        with(Text("${Version.getAsString()} by AlmasB")) {
            isVisible = false
            font = UIFactory.newFont(18.0)
            fill = Color.ALICEBLUE
            translateY = h - 5
            return this
        }
    }

    private fun makePoweredBy(): Text {
        with(Text("Powered By")) {
            isVisible = false
            font = UIFactory.newFont(18.0)
            fill = Color.WHITE
            translateX = (w - layoutBounds.width) / 2
            translateY = h / 2 - 80
            return this
        }
    }

    override fun startIntro() = animation.play()
}