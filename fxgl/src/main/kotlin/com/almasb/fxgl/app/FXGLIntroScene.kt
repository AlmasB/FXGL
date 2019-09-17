/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app

import com.almasb.fxgl.animation.Interpolators
import com.almasb.fxgl.core.math.FXGLMath
import com.almasb.fxgl.core.math.FXGLMath.*
import com.almasb.fxgl.core.math.Vec2
import com.almasb.fxgl.dsl.FXGL
import com.almasb.fxgl.particle.Particle
import com.almasb.fxgl.particle.ParticleEmitters
import com.almasb.fxgl.particle.ParticleSystem
import javafx.animation.*
import javafx.scene.Group
import javafx.scene.effect.BlendMode
import javafx.scene.effect.GaussianBlur
import javafx.scene.effect.Glow
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.text.Text
import javafx.util.Duration

/**
 * This is the default FXGL Intro animation.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
class FXGLIntroScene : IntroScene() {

    private val w = FXGL.getAppWidth().toDouble()
    private val h = FXGL.getAppHeight().toDouble()

    private val animation: Transition

    private val particleSystem = ParticleSystem()

    private val indices = hashMapOf<Particle, Double>()

    init {
        // set up particle system
        val emitter = initEmitter()

        particleSystem.pane.effect = GaussianBlur(3.0)
        particleSystem.addParticleEmitter(emitter, 0.0 - 50.0, h + 50.0)

        // set up the letters and the two lines that make "11"
        val f = makeLetter("F")
        val x = makeLetter("X")
        val g = makeLetter("G")
        val l = makeLetter("L")

        val line = makeLine(w / 2 - 25, 245.0)
        val line2 = makeLine(w / 2 + 25, 245.0)

        // set up translate animation for letters

        val originX = w / 2 - f.layoutBounds.width * 4 / 2
        val dx = f.layoutBounds.width

        val f1 = TranslateTransition(Duration.seconds(0.66), f)
        f1.fromX = -100.0
        f1.toX = originX
        f1.interpolator = Interpolators.EXPONENTIAL.EASE_OUT()

        val f2 = TranslateTransition(Duration.seconds(1.0), x)
        f2.fromX = -100.0
        f2.toX = originX + dx
        f2.interpolator = Interpolators.ELASTIC.EASE_OUT()

        val f3 = TranslateTransition(Duration.seconds(1.13), g)
        f3.fromX = -100.0
        f3.toX = originX + dx*2
        f3.interpolator = Interpolators.BOUNCE.EASE_OUT()

        val f4 = TranslateTransition(Duration.seconds(0.66), l)
        f4.fromX = -100.0
        f4.toX = originX + dx * 3.2
        f4.interpolator = Interpolators.BACK.EASE_OUT()

        // set up fade and grow animation for lines
        val t = Timeline()

        t.keyFrames.addAll(
                KeyFrame(Duration.seconds(1.0), KeyValue(line.opacityProperty(), 1.0)),
                KeyFrame(Duration.seconds(1.0), KeyValue(line2.opacityProperty(), 1.0)),

                KeyFrame(Duration.seconds(1.0), KeyValue(line.heightProperty(), 100.0)),
                KeyFrame(Duration.seconds(1.0), KeyValue(line2.heightProperty(), 100.0))
        )

        // combine animations

        animation = SequentialTransition(f1, f2, f3, f4, t)

        animation.setOnFinished {

            val t2 = FadeTransition(Duration.seconds(1.2), contentRoot)
            t2.fromValue = 1.0
            t2.toValue = 0.5

            t2.setOnFinished {
                finishIntro()
            }

            t2.play()
        }

        // add nodes to scene graph
        contentRoot.children.addAll(Rectangle(w, h), Group(f, x, g, l), line, line2, particleSystem.pane)
    }

    private fun initEmitter() = ParticleEmitters.newFireEmitter().apply {
        isAllowParticleRotation = true
        blendMode = BlendMode.SRC_OVER

        setVelocityFunction { randomPoint2D().multiply(1.5) }

        setSourceImage(FXGL.texture("particles/explosion.png", 32.0, 32.0).brighter().brighter().saturate().image)
        setSize(1.5, 8.5)
        numParticles = 15
        emissionRate = 1.0
        setExpireFunction { Duration.seconds(random(3, 7).toDouble()) }
        setControl { p ->

            val index = indices.getOrDefault(p, random(0.001, 3.05))

            indices.put(p, index)

            val x = p.position.x.toDouble()
            val y = p.position.y.toDouble()

            val noiseValue = FXGLMath.noise3D(x / 300, y / 300, (1 - p.life) * index)
            var angle = toDegrees((noiseValue + 1) * Math.PI * 1.5)

            angle %= 360.0

            if (randomBoolean(0.35)) {
                angle = map(angle, 0.0, 360.0, -100.0, 50.0)
            }

            val v = Vec2.fromAngle(angle).normalizeLocal().mulLocal(random(0.05, 0.35))

            p.acceleration.set(v)
        }
    }

    private fun makeLetter(letter: String) = Text(letter).apply {
        font = FXGL.getUIFactory().newFont(122.0)
        fill = Color.color(0.9, 0.95, 0.96)
        translateY = h / 2 - 150
        opacity = 0.96

        effect = Glow(0.35)

        stroke = Color.DARKBLUE
        strokeWidth = 3.5
    }

    private fun makeLine(width: Double, height: Double) = Rectangle(10.0, 0.0, Color.color(0.9, 0.95, 0.96)).apply {
        stroke = Color.DARKBLUE
        arcWidth = 15.0
        arcHeight = 15.0
        strokeWidth = 4.0
        opacity = 0.0

        translateX = width
        translateY = height
    }

    override fun onUpdate(tpf: Double) {
        super.onUpdate(tpf)

        particleSystem.onUpdate(tpf)
    }

    override fun startIntro() = animation.play()
}