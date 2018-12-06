/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app

import com.almasb.fxgl.animation.Interpolators
import com.almasb.fxgl.core.collection.ObjectMap
import com.almasb.fxgl.core.math.FXGLMath
import com.almasb.fxgl.core.math.FXGLMath.*
import com.almasb.fxgl.core.math.Vec2
import com.almasb.fxgl.particle.Particle
import com.almasb.fxgl.particle.ParticleEmitters
import com.almasb.fxgl.particle.ParticleSystem
import javafx.animation.*
import javafx.scene.Group
import javafx.scene.effect.*
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

    private val indices = ObjectMap<Particle, Double>()

    init {
        val emitter = ParticleEmitters.newFireEmitter()
        emitter.isAllowParticleRotation = true
        emitter.blendMode = BlendMode.SRC_OVER

        emitter.setVelocityFunction { i -> randomPoint2D().multiply(1.5) }

        emitter.setSourceImage(texture("particles/explosion.png", 32.0, 32.0).brighter().brighter().saturate().getImage())
        emitter.setSize(1.5, 8.5)
        emitter.numParticles = 15
        emitter.emissionRate = 1.0
        emitter.setExpireFunction { Duration.seconds(random(3, 7).toDouble()) }
        emitter.setControl { p ->

            val index = indices.get(p, random(0.001, 3.05))

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



        val emitter2 = ParticleEmitters.newFireEmitter()
        emitter2.isAllowParticleRotation = true
        emitter2.blendMode = BlendMode.ADD

        emitter2.setVelocityFunction { i -> randomPoint2D().multiply(1.5) }

        emitter2.setSourceImage(texture("particles/smoke.png", 32.0, 32.0).brighter().brighter().multiplyColor(Color.AQUA).saturate().getImage())
        emitter2.setSize(1.5, 12.5)
        emitter2.numParticles = 10
        emitter2.emissionRate = 0.25
        emitter2.setExpireFunction { Duration.seconds(random(3, 7).toDouble()) }
        emitter2.setControl { p ->

            val index = indices.get(p, random(0.001, 3.05))

            indices.put(p, index)

            val x = p.position.x.toDouble()
            val y = p.position.y.toDouble()

            val noiseValue = FXGLMath.noise3D(x / 300, y / 300, (1 - p.life) * index)
            var angle = toDegrees((noiseValue + 1) * Math.PI * 1.5)

            angle %= 360.0

            if (randomBoolean(0.35)) {
                angle = map(angle, 0.0, 360.0, -100.0, 50.0)
            }

            val v = Vec2.fromAngle(angle).normalizeLocal().mulLocal(random(-0.35, 0.35))

            p.acceleration.set(v)
        }



        particleSystem.pane.effect = GaussianBlur(3.0)
        particleSystem.addParticleEmitter(emitter, 0.0 - 50.0, h + 50.0)
        //particleSystem.addParticleEmitter(emitter2, w / 2, h / 2)


        val f = makeLetter("F")
        val x = makeLetter("X")
        val g = makeLetter("G")
        val l = makeLetter("L")



        val fxglText = Group(f, x, g, l)

        val light = Light.Distant()
        light.azimuth = 15.0

        val lighting = Lighting()
        lighting.light = light
        lighting.surfaceScale = 2.0

        //fxglText.effect = lighting

        val content = Group(fxglText)



        val originX = w / 2 - f.layoutBounds.width * 4 / 2
        val dx = f.layoutBounds.width

        //f.translateX = originX
        //x.translateX = originX + dx
        //g.translateX = originX + dx * 2
        //l.translateX = originX + dx * 3.3

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


        val line = Rectangle(10.0, 0.0, Color.color(0.9, 0.95, 0.96))
        line.stroke = Color.DARKBLUE
        line.arcWidth = 15.0
        line.arcHeight = 15.0
        line.strokeWidth = 4.0
        line.opacity = 0.0

        line.translateX = w / 2 - 25
        line.translateY = 245.0

        val line2 = Rectangle(10.0, 0.0, Color.color(0.9, 0.95, 0.96))
        line2.stroke = Color.DARKBLUE
        line2.arcWidth = 15.0
        line2.arcHeight = 15.0
        line2.strokeWidth = 4.0
        line2.opacity = 0.0

        line2.translateX = w / 2 + 25
        line2.translateY = 245.0

        val t = Timeline()

        t.keyFrames.addAll(
                KeyFrame(Duration.seconds(1.0), KeyValue(line.opacityProperty(), 1.0)),
                KeyFrame(Duration.seconds(1.0), KeyValue(line2.opacityProperty(), 1.0)),

                KeyFrame(Duration.seconds(1.0), KeyValue(line.heightProperty(), 100.0)),
                KeyFrame(Duration.seconds(1.0), KeyValue(line2.heightProperty(), 100.0))
        )


        contentRoot.children.addAll(Rectangle(w, h), content, line, line2, particleSystem.pane)








        animation = SequentialTransition(
                f1, f2, f3, f4, t
        )
        animation.setOnFinished {

            val t2 = FadeTransition(Duration.seconds(1.2), contentRoot)
            t2.fromValue = 1.0
            t2.toValue = 0.5

            t2.setOnFinished {
                finishIntro()
            }

            t2.play()


            //finishIntro()

//            f.effect = null
//            x.effect = null
//            g.effect = null
//            l.effect = null
//
//            val rotate = Rotate(0.0, originX, h / 2 - 150 - f.layoutBounds.height, 0.0, Point3D(0.0, 0.0, 1.0))
//            fxglText.transforms.add(rotate)
//

//
//            t.setOnFinished {
//                val t2 = TranslateTransition(Duration.seconds(1.25), fxglText)
//                t2.toY = h * 2
//                t2.interpolator = Interpolators.EXPONENTIAL.EASE_IN()
//                t2.setOnFinished {
//                    //finishIntro()
//                }
//                t2.play()
//            }
//
//            t.play()
        }
    }

    private fun makeLetter(letter: String) = Text(letter).apply {
        font = FXGL.getUIFactory().newFont(122.0)
        fill = Color.color(0.9, 0.95, 0.96)
        translateY = h / 2 - 150
        opacity = 0.96
        //effect = DropShadow(5.5, Color.BLUE)

        effect = Glow(0.35)

        stroke = Color.DARKBLUE
        strokeWidth = 3.5
    }

    override fun onUpdate(tpf: Double) {
        super.onUpdate(tpf)

        particleSystem.onUpdate(tpf)
    }

    override fun startIntro() = animation.play()
}