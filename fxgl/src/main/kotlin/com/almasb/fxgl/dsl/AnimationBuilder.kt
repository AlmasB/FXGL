/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.dsl

import com.almasb.fxgl.animation.*
import com.almasb.fxgl.core.util.Consumer
import com.almasb.fxgl.core.util.EmptyRunnable
import com.almasb.fxgl.entity.Entity
import com.almasb.fxgl.scene.Scene
import com.almasb.fxgl.scene.SceneListener
import javafx.animation.Interpolator
import javafx.beans.property.DoubleProperty
import javafx.geometry.Point2D
import javafx.scene.Node
import javafx.scene.shape.CubicCurve
import javafx.scene.shape.QuadCurve
import javafx.scene.shape.Shape
import javafx.util.Duration

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class AnimationBuilder {

    private var duration: Duration = Duration.seconds(1.0)
    private var delay: Duration = Duration.ZERO
    private var interpolator: Interpolator = Interpolator.LINEAR
    private var times: Int = 1
    private var onFinished: Runnable = EmptyRunnable
    private var isAutoReverse: Boolean = false

    private val objects = arrayListOf<Animatable>()

    fun duration(duration: Duration): AnimationBuilder {
        this.duration = duration
        return this
    }

    fun delay(delay: Duration): AnimationBuilder {
        this.delay = delay
        return this
    }

    fun interpolator(interpolator: Interpolator): AnimationBuilder {
        this.interpolator = interpolator
        return this
    }

    fun repeat(times: Int): AnimationBuilder {
        this.times = times
        return this
    }

    fun repeatInfinitely(): AnimationBuilder {
        return repeat(Integer.MAX_VALUE)
    }

    fun onFinished(onFinished: Runnable): AnimationBuilder {
        this.onFinished = onFinished
        return this
    }

    fun autoReverse(autoReverse: Boolean): AnimationBuilder {
        this.isAutoReverse = autoReverse
        return this
    }

    private fun makeBuilder(): com.almasb.fxgl.animation.AnimationBuilder {
        return com.almasb.fxgl.animation.AnimationBuilder(duration, delay, interpolator, times, onFinished, isAutoReverse)
    }

    fun translate(vararg entities: Entity) = TranslationAnimationBuilder(this).also {
        objects += entities.map { it.toAnimatable() }
    }

    fun translate(vararg entities: Node) = TranslationAnimationBuilder(this).also {
        objects += entities.map { it.toAnimatable() }
    }

    fun translate(entities: Collection<Any>) = TranslationAnimationBuilder(this).also {
        objects += entities.map {
            when (it) {
                is Node -> it.toAnimatable()
                is Entity -> it.toAnimatable()
                else -> throw RuntimeException("${it.javaClass} must be Node or Entity")
            }
        }
    }

    fun fade(vararg entities: Entity) = FadeAnimationBuilder(this).also {
        objects += entities.map { it.toAnimatable() }
    }

    fun fade(vararg entities: Node) = FadeAnimationBuilder(this).also {
        objects += entities.map { it.toAnimatable() }
    }

    fun fade(entities: Collection<Any>) = FadeAnimationBuilder(this).also {
        objects += entities.map {
            when (it) {
                is Node -> it.toAnimatable()
                is Entity -> it.toAnimatable()
                else -> throw RuntimeException("${it.javaClass} must be Node or Entity")
            }
        }
    }

    fun scale(vararg entities: Entity) = ScaleAnimationBuilder(this).also {
        objects += entities.map { it.toAnimatable() }
    }

    fun scale(vararg entities: Node) = ScaleAnimationBuilder(this).also {
        objects += entities.map { it.toAnimatable() }
    }

    fun scale(entities: Collection<Any>) = ScaleAnimationBuilder(this).also {
        objects += entities.map {
            when (it) {
                is Node -> it.toAnimatable()
                is Entity -> it.toAnimatable()
                else -> throw RuntimeException("${it.javaClass} must be Node or Entity")
            }
        }
    }

    fun rotate(vararg entities: Entity) = RotationAnimationBuilder(this).also {
        objects += entities.map { it.toAnimatable() }
    }

    fun rotate(vararg entities: Node) = RotationAnimationBuilder(this).also {
        objects += entities.map { it.toAnimatable() }
    }

    fun rotate(entities: Collection<Any>) = RotationAnimationBuilder(this).also {
        objects += entities.map {
            when (it) {
                is Node -> it.toAnimatable()
                is Entity -> it.toAnimatable()
                else -> throw RuntimeException("${it.javaClass} must be Node or Entity")
            }
        }
    }

    fun fadeIn(vararg entities: Entity) = FadeAnimationBuilder(this).also {
        objects += entities.map { it.toAnimatable() }
    }.from(0.0).to(1.0)

    fun fadeIn(vararg entities: Node) = FadeAnimationBuilder(this).also {
        objects += entities.map { it.toAnimatable() }
    }.from(0.0).to(1.0)

    fun fadeOut(vararg entities: Entity) = FadeAnimationBuilder(this).also {
        objects += entities.map { it.toAnimatable() }
    }.from(1.0).to(0.0)

    fun fadeOut(vararg entities: Node) = FadeAnimationBuilder(this).also {
        objects += entities.map { it.toAnimatable() }
    }.from(1.0).to(0.0)

    abstract class AM(private val animationBuilder: AnimationBuilder) {
        abstract fun build(): Animation<*>

        /**
         * Builds animation and plays in the game scene.
         */
        fun buildAndPlay() {
            buildAndPlay(FXGL.getGameScene())
        }

        fun buildAndPlay(scene: Scene) {
            build().also {
                val l = it.toListener()

                it.onFinished = Runnable {
                    scene.removeListener(l)
                    animationBuilder.onFinished.run()
                }

                it.start()
                scene.addListener(l)
            }
        }
    }

    class TranslationAnimationBuilder(private val animationBuilder: AnimationBuilder) : AM(animationBuilder) {

        private var path: Shape? = null
        private var fromPoint = Point2D.ZERO
        private var toPoint = Point2D.ZERO

        fun alongPath(path: Shape) = this.also {
            this.path = path
        }

        fun from(start: Point2D) = this.also {
            fromPoint = start
        }

        fun to(end: Point2D) = this.also {
            toPoint = end
        }

        override fun build(): Animation<*> {

            path?.let { curve ->
                when (curve) {
                    is QuadCurve -> return makeAnim(AnimatedQuadBezierPoint2D(curve))
                    is CubicCurve -> return makeAnim(AnimatedCubicBezierPoint2D(curve))
                    else -> throw IllegalArgumentException("Unsupported path: $curve")
                }
            }

            return makeAnim(AnimatedPoint2D(fromPoint, toPoint))
        }

        private fun makeAnim(animValue: AnimatedValue<Point2D>): Animation<Point2D> {
            return animationBuilder.makeBuilder().build(
                    animValue,
                    Consumer { value ->
                        animationBuilder.objects.forEach {
                            it.xProperty().value = value.x
                            it.yProperty().value = value.y
                        }
                    }
            )
        }
    }

    class FadeAnimationBuilder(private val animationBuilder: AnimationBuilder) : AM(animationBuilder) {

        private var from = 0.0
        private var to = 0.0

        fun from(start: Double) = this.also {
            from = start
        }

        fun to(end: Double) = this.also {
            to = end
        }

        override fun build(): Animation<*> {
            return animationBuilder.makeBuilder().build(AnimatedValue(from, to),
                    Consumer { value ->
                        animationBuilder.objects.forEach {
                            it.opacityProperty().value = value
                        }
                    }
            )
        }
    }

    class ScaleAnimationBuilder(private val animationBuilder: AnimationBuilder) : AM(animationBuilder) {

        private var startScale = Point2D(1.0, 1.0)
        private var endScale = Point2D(1.0, 1.0)

        fun from(start: Point2D): ScaleAnimationBuilder {
            startScale = start
            return this
        }

        fun to(end: Point2D): ScaleAnimationBuilder {
            endScale = end
            return this
        }

        override fun build(): Animation<*> {
            return animationBuilder.makeBuilder().build(
                    AnimatedPoint2D(startScale, endScale),
                    Consumer { value ->
                        animationBuilder.objects.forEach {
                            it.scaleXProperty().value = value.x
                            it.scaleYProperty().value = value.y
                        }
                    }
            )
        }
    }

    class RotationAnimationBuilder(private val animationBuilder: AnimationBuilder) : AM(animationBuilder) {

        private var startAngle = 0.0
        private var endAngle = 0.0

        fun from(startAngle: Double): RotationAnimationBuilder {
            this.startAngle = startAngle
            return this
        }

        fun to(endAngle: Double): RotationAnimationBuilder {
            this.endAngle = endAngle
            return this
        }

        override fun build(): Animation<*> {
            return animationBuilder.makeBuilder().build(AnimatedValue(startAngle, endAngle),
                    Consumer { value ->
                        animationBuilder.objects.forEach {
                            it.rotationProperty().value = value
                        }
                    }
            )
        }
    }
}

private fun Node.toAnimatable(): Animatable {
    val n = this
    return object : Animatable {
        override fun xProperty(): DoubleProperty {
            return n.translateXProperty()
        }

        override fun yProperty(): DoubleProperty {
            return n.translateYProperty()
        }

        override fun scaleXProperty(): DoubleProperty {
            return n.scaleXProperty()
        }

        override fun scaleYProperty(): DoubleProperty {
            return n.scaleYProperty()
        }

        override fun rotationProperty(): DoubleProperty {
            return n.rotateProperty()
        }

        override fun opacityProperty(): DoubleProperty {
            return n.opacityProperty()
        }
    }
}

private fun Entity.toAnimatable(): Animatable {
    val e = this
    return object : Animatable {
        override fun xProperty(): DoubleProperty {
            return e.xProperty()
        }

        override fun yProperty(): DoubleProperty {
            return e.yProperty()
        }

        override fun scaleXProperty(): DoubleProperty {
            return e.transformComponent.scaleXProperty()
        }

        override fun scaleYProperty(): DoubleProperty {
            return e.transformComponent.scaleYProperty()
        }

        override fun rotationProperty(): DoubleProperty {
            return e.transformComponent.angleProperty()
        }

        override fun opacityProperty(): DoubleProperty {
            return e.viewComponent.opacityProp
        }
    }
}

private fun Animation<*>.toListener(): SceneListener {
    val a = this
    return object : SceneListener {
        override fun onUpdate(tpf: Double) {
            a.onUpdate(tpf)
        }
    }
}