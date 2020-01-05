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
open class AnimationBuilder() {

    protected var duration: Duration = Duration.seconds(1.0)
    protected var delay: Duration = Duration.ZERO
    protected var interpolator: Interpolator = Interpolator.LINEAR
    protected var times: Int = 1
    protected var onFinished: Runnable = EmptyRunnable
    protected var isAutoReverse: Boolean = false

    constructor(copy: AnimationBuilder) : this() {
        duration = copy.duration
        delay = copy.delay
        interpolator = copy.interpolator
        times = copy.times
        onFinished = copy.onFinished
        isAutoReverse = copy.isAutoReverse
    }

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

    protected fun makeBuilder(): com.almasb.fxgl.animation.AnimationBuilder {
        return com.almasb.fxgl.animation.AnimationBuilder(duration, delay, interpolator, times, onFinished, isAutoReverse)
    }

    /* BEGIN BUILT-IN ANIMATIONS */

    fun <T> animate(value: AnimatedValue<T>) = GenericAnimationBuilder<T>(this, value)

    fun translate(vararg entities: Entity) = TranslationAnimationBuilder(this).apply {
        objects += entities.map { it.toAnimatable() }
    }

    fun translate(vararg entities: Node) = TranslationAnimationBuilder(this).apply {
        objects += entities.map { it.toAnimatable() }
    }

    fun translate(entities: Collection<Any>) = TranslationAnimationBuilder(this).apply {
        objects += entities.map {
            when (it) {
                is Node -> it.toAnimatable()
                is Entity -> it.toAnimatable()
                else -> throw RuntimeException("${it.javaClass} must be Node or Entity")
            }
        }
    }

    fun fade(vararg entities: Entity) = FadeAnimationBuilder(this).apply {
        objects += entities.map { it.toAnimatable() }
    }

    fun fade(vararg entities: Node) = FadeAnimationBuilder(this).apply {
        objects += entities.map { it.toAnimatable() }
    }

    fun fade(entities: Collection<Any>) = FadeAnimationBuilder(this).apply {
        objects += entities.map {
            when (it) {
                is Node -> it.toAnimatable()
                is Entity -> it.toAnimatable()
                else -> throw RuntimeException("${it.javaClass} must be Node or Entity")
            }
        }
    }

    fun scale(vararg entities: Entity) = ScaleAnimationBuilder(this).apply {
        objects += entities.map { it.toAnimatable() }
    }

    fun scale(vararg entities: Node) = ScaleAnimationBuilder(this).apply {
        objects += entities.map { it.toAnimatable() }
    }

    fun scale(entities: Collection<Any>) = ScaleAnimationBuilder(this).apply {
        objects += entities.map {
            when (it) {
                is Node -> it.toAnimatable()
                is Entity -> it.toAnimatable()
                else -> throw RuntimeException("${it.javaClass} must be Node or Entity")
            }
        }
    }

    fun rotate(vararg entities: Entity) = RotationAnimationBuilder(this).apply {
        objects += entities.map { it.toAnimatable() }
    }

    fun rotate(vararg entities: Node) = RotationAnimationBuilder(this).apply {
        objects += entities.map { it.toAnimatable() }
    }

    fun rotate(entities: Collection<Any>) = RotationAnimationBuilder(this).apply {
        objects += entities.map {
            when (it) {
                is Node -> it.toAnimatable()
                is Entity -> it.toAnimatable()
                else -> throw RuntimeException("${it.javaClass} must be Node or Entity")
            }
        }
    }

    fun fadeIn(vararg entities: Entity) = FadeAnimationBuilder(this).apply {
        objects += entities.map { it.toAnimatable() }
    }.from(0.0).to(1.0)

    fun fadeIn(vararg entities: Node) = FadeAnimationBuilder(this).apply {
        objects += entities.map { it.toAnimatable() }
    }.from(0.0).to(1.0)

    fun fadeOut(vararg entities: Entity) = FadeAnimationBuilder(this).apply {
        objects += entities.map { it.toAnimatable() }
    }.from(1.0).to(0.0)

    fun fadeOut(vararg entities: Node) = FadeAnimationBuilder(this).apply {
        objects += entities.map { it.toAnimatable() }
    }.from(1.0).to(0.0)

    fun bobbleDown(node: Node) = duration(Duration.seconds(0.15))
            .autoReverse(true)
            .repeat(2)
            .translate(node)
            .from(Point2D(node.translateX, node.translateY))
            .to(Point2D(node.translateX, node.translateY + 5.0))

    /* END BUILT-IN ANIMATIONS */

    abstract class AM(animationBuilder: AnimationBuilder) : AnimationBuilder(animationBuilder) {
        val objects = arrayListOf<Animatable>()

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
                    onFinished.run()
                }

                it.start()
                scene.addListener(l)
            }
        }
    }

    class TranslationAnimationBuilder(animationBuilder: AnimationBuilder) : AM(animationBuilder) {

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
            return makeBuilder().build(
                    animValue,
                    Consumer { value ->
                        objects.forEach {
                            it.xProperty().value = value.x
                            it.yProperty().value = value.y
                        }
                    }
            )
        }
    }

    class FadeAnimationBuilder(animationBuilder: AnimationBuilder) : AM(animationBuilder) {

        private var from = 0.0
        private var to = 0.0

        fun from(start: Double) = this.also {
            from = start
        }

        fun to(end: Double) = this.also {
            to = end
        }

        override fun build(): Animation<*> {
            return makeBuilder().build(AnimatedValue(from, to),
                    Consumer { value ->
                        objects.forEach {
                            it.opacityProperty().value = value
                        }
                    }
            )
        }
    }

    class ScaleAnimationBuilder(animationBuilder: AnimationBuilder) : AM(animationBuilder) {

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
            return makeBuilder().build(
                    AnimatedPoint2D(startScale, endScale),
                    Consumer { value ->
                        objects.forEach {
                            it.scaleXProperty().value = value.x
                            it.scaleYProperty().value = value.y
                        }
                    }
            )
        }
    }

    class RotationAnimationBuilder(animationBuilder: AnimationBuilder) : AM(animationBuilder) {

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
            return makeBuilder().build(AnimatedValue(startAngle, endAngle),
                    Consumer { value ->
                        objects.forEach {
                            it.rotationProperty().value = value
                        }
                    }
            )
        }
    }

    class GenericAnimationBuilder<T>(animationBuilder: AnimationBuilder, val animValue: AnimatedValue<T>) : AM(animationBuilder) {

        private var progressConsumer: Consumer<T> = Consumer {  }

        fun onProgress(progressConsumer: Consumer<T>): GenericAnimationBuilder<T> {
            this.progressConsumer = progressConsumer
            return this
        }

        override fun build(): Animation<T> {
            return makeBuilder().build(animValue, progressConsumer)
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