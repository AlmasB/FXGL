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

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.scene.IntroScene
import com.almasb.fxgl.ui.UIFactory
import com.almasb.fxgl.util.Version
import javafx.animation.*
import javafx.geometry.Point2D
import javafx.geometry.Point3D
import javafx.scene.Group
import javafx.scene.canvas.Canvas
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.text.Text
import javafx.util.Duration
import java.util.*

/**
 * This is the default FXGL Intro animation.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
class FXGLIntroScene() : IntroScene() {

    private val w: Double
    private val h: Double

    private val animation: ParallelTransition

    init {
        w = FXGL.getSettings().width.toDouble()
        h = FXGL.getSettings().height.toDouble()

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

        val content = Group(fxglText, poweredText, version)

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

        animation = ParallelTransition(tt, tt2, tt3, tt4)
        animation.setOnFinished { event ->
            poweredText.isVisible = true
            version.isVisible = true

            val rt = RotateTransition(Duration.seconds(1.0), l)
            rt.delay = Duration.seconds(0.66)
            rt.axis = Point3D(0.0, 0.0, 1.0)
            rt.byAngle = -180.0
            rt.setOnFinished { e ->
                animateParticles()
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

    private fun animateParticles() {
        val particles = ArrayList<Particle>()

        val image = root.snapshot(null, null)

        val reader = image.pixelReader
        for (y in 0..h.toInt() - 1) {
            for (x in 0..w.toInt() - 1) {
                val color = reader.getColor(x, y)

                if (!(color.blue == 0.0 && color.red == 0.0 && color.green == 0.0)) {
                    particles.add(Particle(x.toDouble(), y.toDouble()))
                }
            }
        }

        val canvas = Canvas(w, h)
        val g = canvas.graphicsContext2D

        root.children.setAll(canvas)

        val timer = object : AnimationTimer() {
            override fun handle(now: Long) {

                if (particles.isEmpty()) {
                    stop()

                    val ft = FadeTransition(Duration.seconds(0.5), root)
                    ft.toValue = 0.0
                    ft.setOnFinished { e1 ->
                        finishIntro()
                    }
                    ft.play()
                }

                val it = particles.iterator()
                while (it.hasNext()) {
                    if (it.next().x < 0)
                        it.remove()
                }

                particles.filter({ p -> p.vel === Point2D.ZERO })
                        .sortedWith(Comparator { p1, p2 -> (p1.y - p2.y).toInt() })
                        .take(25)
                        .forEach { p -> p.vel = Point2D(-4 - Math.random() * 10, 0.0) }

                g.setGlobalAlpha(1.0)
                g.clearRect(0.0, 0.0, w, h)
                g.setFill(Color.BLACK)
                g.fillRect(0.0, 0.0, w, h)
                g.setFill(Color.WHITE)

                for (p in particles) {
                    p.update()

                    g.setGlobalAlpha(p.x / 400 + 0.3)
                    g.fillOval(p.x, p.y, 1.0, 1.0)
                }
            }
        }
        timer.start()
    }

    private class Particle(var x: Double, var y: Double) {
        var vel = Point2D.ZERO

        fun update() {
            x += vel.x
            y += vel.y
        }
    }
}