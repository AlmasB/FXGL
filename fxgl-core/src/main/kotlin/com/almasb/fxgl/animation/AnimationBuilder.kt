/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.animation

import com.almasb.fxgl.core.UpdatableRunner
import com.almasb.fxgl.core.math.FXGLMath
import com.almasb.fxgl.core.util.EmptyRunnable
import com.almasb.fxgl.logging.Logger
import javafx.animation.Interpolator
import javafx.beans.property.DoubleProperty
import javafx.beans.value.WritableValue
import javafx.geometry.Point2D
import javafx.geometry.Point3D
import javafx.scene.Node
import javafx.scene.shape.CubicCurve
import javafx.scene.shape.QuadCurve
import javafx.scene.shape.Shape
import javafx.scene.transform.Rotate
import javafx.scene.transform.Scale
import javafx.util.Duration
import java.lang.IllegalArgumentException
import java.util.function.Consumer

/**
 * Animation DSL that provides a fluent API for building and running animations.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
open class AnimationBuilder
@JvmOverloads constructor(protected val scene: UpdatableRunner? = null) {

    var duration: Duration = Duration.seconds(1.0)
    var delay: Duration = Duration.ZERO
    var interpolator: Interpolator = Interpolator.LINEAR
    var times: Int = 1
    var onCycleFinished: Runnable = EmptyRunnable
    var isAutoReverse: Boolean = false

    var onFinished: Runnable = EmptyRunnable

    constructor(copy: AnimationBuilder) : this(copy.scene) {
        duration = copy.duration
        delay = copy.delay
        interpolator = copy.interpolator
        times = copy.times
        onFinished = copy.onFinished
        onCycleFinished = copy.onCycleFinished
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

    fun onCycleFinished(onCycleFinished: Runnable): AnimationBuilder {
        this.onCycleFinished = onCycleFinished
        return this
    }

    fun autoReverse(autoReverse: Boolean): AnimationBuilder {
        this.isAutoReverse = autoReverse
        return this
    }

    fun onFinished(onFinished: Runnable): AnimationBuilder {
        this.onFinished = onFinished
        return this
    }

    internal fun <T> buildAnimation(animatedValue: AnimatedValue<T>, onProgress: Consumer<T>): Animation<T> {
        return object : Animation<T>(this, animatedValue) {
            override fun onProgress(value: T) {
                onProgress.accept(value)
            }
        }
    }

    /* BEGIN BUILT-IN ANIMATIONS */

    fun <T> animate(value: AnimatedValue<T>) = GenericAnimationBuilder(this, value)

    fun <T> animate(property: WritableValue<T>) = PropertyAnimationBuilder(this, property)

    fun translate(vararg entities: Animatable) = TranslationAnimationBuilder(this).apply {
        objects += entities
    }

    fun translate(vararg entities: Node) = TranslationAnimationBuilder(this).apply {
        objects += entities.map { it.toAnimatable() }
    }

    fun translate(entities: Collection<Any>) = TranslationAnimationBuilder(this).apply {
        objects += entities.map { toAnimatable(it) }
    }

    fun fade(vararg entities: Animatable) = FadeAnimationBuilder(this).apply {
        objects += entities
    }

    fun fade(vararg entities: Node) = FadeAnimationBuilder(this).apply {
        objects += entities.map { it.toAnimatable() }
    }

    fun fade(entities: Collection<Any>) = FadeAnimationBuilder(this).apply {
        objects += entities.map { toAnimatable(it) }
    }

    fun scale(vararg entities: Animatable) = ScaleAnimationBuilder(this).apply {
        objects += entities
    }

    fun scale(vararg entities: Node) = ScaleAnimationBuilder(this).apply {
        objects += entities.map { it.toAnimatable() }
    }

    fun scale(entities: Collection<Any>) = ScaleAnimationBuilder(this).apply {
        objects += entities.map { toAnimatable(it) }
    }

    fun rotate(vararg entities: Animatable) = RotationAnimationBuilder(this).apply {
        objects += entities
    }

    fun rotate(vararg entities: Node) = RotationAnimationBuilder(this).apply {
        objects += entities.map { it.toAnimatable() }
    }

    fun rotate(entities: Collection<Any>) = RotationAnimationBuilder(this).apply {
        objects += entities.map { toAnimatable(it) }
    }

    private fun toAnimatable(obj: Any): Animatable = when (obj) {
        is Node -> obj.toAnimatable()
        is Animatable -> obj
        else -> throw IllegalArgumentException("${obj.javaClass} must be Node or Animatable")
    }

    fun fadeIn(vararg entities: Animatable) = FadeAnimationBuilder(this).apply {
        objects += entities
    }.from(0.0).to(1.0)

    fun fadeIn(vararg entities: Node) = FadeAnimationBuilder(this).apply {
        objects += entities.map { it.toAnimatable() }
    }.from(0.0).to(1.0)

    fun fadeOut(vararg entities: Animatable) = FadeAnimationBuilder(this).apply {
        objects += entities
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

    fun buildSequence(vararg anims: Animation<*>): Animation<*> {
        val ranges = linkedMapOf<ClosedFloatingPointRange<Double>, Animation<*>>()

        var count = 0.0

        anims.forEach { anim ->
            ranges[count..count+anim.endTime] = anim

            count += anim.endTime
        }

        val totalDuration = anims.sumOf { it.endTime }

        var isNewCycle = true

        var a: Animation<*>? = null

        a = onCycleFinished {
                    isNewCycle = true
                }
                .duration(Duration.seconds(totalDuration))
                .animate(AnimatedValue(0.0, totalDuration))
                .onProgress { time ->

                    // this is needed to pre-apply all animations when starting or reversing
                    if (isNewCycle) {
                        ranges.values.forEach {
                            it.stop()

                            if (a!!.isReverse) {
                                it.startReverse()
                            } else {
                                it.start()
                            }
                        }

                        isNewCycle = false
                    }

                    ranges.forEach { (range, anim) ->
                        if (time in range) {

                            val mappedTime = FXGLMath.map(time, range.start, range.endInclusive, 0.0, anim.endTime)

                            anim.setTimeTo(mappedTime)
                        }
                    }
                }
                .build()

        return a
    }

    /* END BUILT-IN ANIMATIONS */

    abstract class AM(private val animationBuilder: AnimationBuilder) : AnimationBuilder(animationBuilder) {
        val objects = arrayListOf<Animatable>()

        abstract fun build(): Animation<*>

        /**
         * Builds animation and plays in the game scene.
         */
        fun buildAndPlay() {
            if (animationBuilder.scene != null) {
                buildAndPlay(animationBuilder.scene)
            } else {
                Logger.get(javaClass).warning("No game scene was set to AnimationBuilder")
            }
        }

        fun buildAndPlay(scene: UpdatableRunner) {
            build().also { animation ->

                animation.onFinished = Runnable {
                    scene.removeListener(animation)
                    onFinished.run()
                }

                animation.start()
                scene.addListener(animation)
            }
        }
    }

    class TranslationAnimationBuilder(animationBuilder: AnimationBuilder) : AM(animationBuilder) {

        private var path: Shape? = null
        private var fromPoint = Point3D.ZERO
        private var toPoint = Point3D.ZERO

        fun alongPath(path: Shape) = this.also {
            this.path = path
        }

        fun from(start: Point2D) = this.also {
            fromPoint = Point3D(start.x, start.y, 0.0)
        }

        fun to(end: Point2D) = this.also {
            toPoint = Point3D(end.x, end.y, 0.0)
        }

        fun from(start: Point3D) = this.also {
            fromPoint = start
        }

        fun to(end: Point3D) = this.also {
            toPoint = end
        }

        override fun build(): Animation<*> {

            path?.let { curve ->
                return when (curve) {
                    is QuadCurve -> makeAnim(AnimatedQuadBezierPoint3D(curve))
                    is CubicCurve -> makeAnim(AnimatedCubicBezierPoint3D(curve))
                    else -> makeAnim(AnimatedPath(curve))
                }
            }

            return makeAnim(AnimatedPoint3D(fromPoint, toPoint))
        }

        private fun makeAnim(animValue: AnimatedValue<Point3D>): Animation<Point3D> {
            return buildAnimation(
                    animValue,
                    Consumer { value ->
                        objects.forEach {
                            it.xProperty().value = value.x
                            it.yProperty().value = value.y
                            it.zProperty().value = value.z
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
            return buildAnimation(AnimatedValue(from, to),
                    Consumer { value ->
                        objects.forEach {
                            it.opacityProperty().value = value
                        }
                    }
            )
        }
    }

    class ScaleAnimationBuilder(animationBuilder: AnimationBuilder) : AM(animationBuilder) {

        private var startScale = Point3D(1.0, 1.0, 1.0)
        private var endScale = Point3D(1.0, 1.0, 1.0)
        private var scaleOrigin: Point3D? = null

        fun from(start: Point2D): ScaleAnimationBuilder {
            startScale = Point3D(start.x, start.y, 1.0)
            return this
        }

        fun to(end: Point2D): ScaleAnimationBuilder {
            endScale = Point3D(end.x, end.y, 1.0)
            return this
        }

        fun from(start: Point3D): ScaleAnimationBuilder {
            startScale = start
            return this
        }

        fun to(end: Point3D): ScaleAnimationBuilder {
            endScale = end
            return this
        }

        fun origin(scaleOrigin: Point2D): ScaleAnimationBuilder {
            this.scaleOrigin = Point3D(scaleOrigin.x, scaleOrigin.y, 0.0)
            return this
        }

        fun origin(scaleOrigin: Point3D): ScaleAnimationBuilder {
            this.scaleOrigin = scaleOrigin
            return this
        }

        override fun build(): Animation<*> {
            scaleOrigin?.let { origin ->
                objects.forEach {
                    it.setScaleOrigin(origin)
                }
            }

            return buildAnimation(
                    AnimatedPoint3D(startScale, endScale),
                    Consumer { value ->
                        objects.forEach {
                            it.scaleXProperty().value = value.x
                            it.scaleYProperty().value = value.y
                            it.scaleZProperty().value = value.z
                        }
                    }
            )
        }
    }

    class RotationAnimationBuilder(animationBuilder: AnimationBuilder) : AM(animationBuilder) {

        private var is3DAnimation = false
        private var startRotation = Point3D.ZERO
        private var endRotation = Point3D.ZERO
        private var rotationOrigin: Point3D? = null

        fun from(startAngle: Double): RotationAnimationBuilder {
            is3DAnimation = false
            startRotation = Point3D(0.0, 0.0, startAngle)
            return this
        }

        fun to(endAngle: Double): RotationAnimationBuilder {
            is3DAnimation = false
            endRotation = Point3D(0.0, 0.0, endAngle)
            return this
        }

        fun from(start: Point3D): RotationAnimationBuilder {
            is3DAnimation = true
            startRotation = start
            return this
        }

        fun to(end: Point3D): RotationAnimationBuilder {
            is3DAnimation = true
            endRotation = end
            return this
        }

        fun origin(rotationOrigin: Point2D): RotationAnimationBuilder {
            this.rotationOrigin = Point3D(rotationOrigin.x, rotationOrigin.y, 0.0)
            return this
        }

        fun origin(rotationOrigin: Point3D): RotationAnimationBuilder {
            this.rotationOrigin = rotationOrigin
            return this
        }

        override fun build(): Animation<*> {
            // force 3D rotations to be generated to avoid NPE when accessing rotationX/Y properties
            if (is3DAnimation) {
                objects.forEach {
                    it.setRotationOrigin(Point3D.ZERO)
                }
            }

            rotationOrigin?.let { origin ->
                objects.forEach {
                    it.setRotationOrigin(origin)
                }
            }

            return buildAnimation(AnimatedValue(startRotation, endRotation),
                    Consumer { value ->
                        objects.forEach {
                            if (is3DAnimation) {
                                it.rotationXProperty().value = value.x
                                it.rotationYProperty().value = value.y
                            }
                            it.rotationZProperty().value = value.z
                        }
                    }
            )
        }
    }

    class GenericAnimationBuilder<T>(animationBuilder: AnimationBuilder, val animValue: AnimatedValue<T>) : AM(animationBuilder) {

        private var progressConsumer: Consumer<T> = Consumer { }

        fun onProgress(progressConsumer: Consumer<T>): GenericAnimationBuilder<T> {
            this.progressConsumer = progressConsumer
            return this
        }

        override fun build(): Animation<T> {
            return buildAnimation(animValue, progressConsumer)
        }
    }

    class PropertyAnimationBuilder<T>(animationBuilder: AnimationBuilder, private val property: WritableValue<T>) : AM(animationBuilder) {

        private var startValue: T = property.value
        private var endValue: T = property.value

        private var progressConsumer: Consumer<T> = Consumer {
            property.value = it
        }

        fun from(startValue: T): PropertyAnimationBuilder<T> {
            this.startValue = startValue
            return this
        }

        fun to(endValue: T): PropertyAnimationBuilder<T> {
            this.endValue = endValue
            return this
        }

        override fun build(): Animation<T> {
            return buildAnimation(AnimatedValue(startValue, endValue), progressConsumer)
        }
    }
}

private fun Node.toAnimatable(): Animatable {
    val n = this
    return object : Animatable {
        private var scale: Scale? = null
        private var rotateX: Rotate? = null
        private var rotateY: Rotate? = null
        private var rotateZ: Rotate? = null

        override fun xProperty(): DoubleProperty {
            return n.translateXProperty()
        }

        override fun yProperty(): DoubleProperty {
            return n.translateYProperty()
        }

        override fun zProperty(): DoubleProperty {
            return n.translateZProperty()
        }

        override fun scaleXProperty(): DoubleProperty {
            return scale?.xProperty() ?: n.scaleXProperty()
        }

        override fun scaleYProperty(): DoubleProperty {
            return scale?.yProperty() ?: n.scaleYProperty()
        }

        override fun scaleZProperty(): DoubleProperty {
            return scale?.zProperty() ?: n.scaleZProperty()
        }

        override fun rotationXProperty(): DoubleProperty {
            return rotateX!!.angleProperty()
        }

        override fun rotationYProperty(): DoubleProperty {
            return rotateY!!.angleProperty()
        }

        override fun rotationZProperty(): DoubleProperty {
            return rotateZ?.angleProperty() ?: n.rotateProperty()
        }

        override fun opacityProperty(): DoubleProperty {
            return n.opacityProperty()
        }

        override fun setScaleOrigin(pivotPoint: Point3D) {
            // if a node already has a previous transform, reuse it
            // this means the node was animated previously
            n.properties["anim_scale"]?.let { transform ->
                scale = transform as Scale
                scale!!.pivotX = pivotPoint.x
                scale!!.pivotY = pivotPoint.y
                scale!!.pivotZ = pivotPoint.z
                return
            }

            scale = Scale(0.0, 0.0, 0.0, pivotPoint.x, pivotPoint.y, pivotPoint.z)
                    .also {
                        n.transforms.add(it)
                        n.properties["anim_scale"] = it
                    }
        }

        override fun setRotationOrigin(pivotPoint: Point3D) {
            // if a node already has a previous transform, reuse it
            // this means the node was animated previously
            n.properties["anim_rotate_x"]?.let { transform ->
                rotateX = transform as Rotate
                rotateX!!.pivotX = pivotPoint.x
                rotateX!!.pivotY = pivotPoint.y
                rotateX!!.pivotZ = pivotPoint.z
            }

            n.properties["anim_rotate_y"]?.let { transform ->
                rotateY = transform as Rotate
                rotateY!!.pivotX = pivotPoint.x
                rotateY!!.pivotY = pivotPoint.y
                rotateY!!.pivotZ = pivotPoint.z
            }

            n.properties["anim_rotate_z"]?.let { transform ->
                rotateZ = transform as Rotate
                rotateZ!!.pivotX = pivotPoint.x
                rotateZ!!.pivotY = pivotPoint.y
                rotateZ!!.pivotZ = pivotPoint.z
                return
            }

            rotateZ = Rotate(0.0, pivotPoint.x, pivotPoint.y, pivotPoint.z, Rotate.Z_AXIS)
            rotateY = Rotate(0.0, pivotPoint.x, pivotPoint.y, pivotPoint.z, Rotate.Y_AXIS)
            rotateX = Rotate(0.0, pivotPoint.x, pivotPoint.y, pivotPoint.z, Rotate.X_AXIS)

            n.properties["anim_rotate_x"] = rotateX
            n.properties["anim_rotate_y"] = rotateY
            n.properties["anim_rotate_z"] = rotateZ

            n.transforms.addAll(rotateZ, rotateY, rotateX)
        }
    }
}