/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.animation

import com.almasb.fxgl.core.math.FXGLMath
import javafx.animation.Interpolator
import javafx.animation.PathTransition
import javafx.geometry.Point2D
import javafx.geometry.Point3D
import javafx.scene.paint.Color
import javafx.scene.shape.CubicCurve
import javafx.scene.shape.QuadCurve
import javafx.scene.shape.Rectangle
import javafx.scene.shape.Shape
import javafx.util.Duration
import kotlin.math.max
import kotlin.math.min

/**
 * A value that can be animated (progressed) from value1 to value 2.
 * An interpolator can be used to control the rate of animation (progression).
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
open class AnimatedValue<T>(val from: T, val to: T) {

    fun getValue(progress: Double): T {
        return animate(from, to, progress, Interpolator.LINEAR)
    }

    fun getValue(progress: Double, interpolator: Interpolator): T {
        return animate(from, to, progress, interpolator)
    }

    @Suppress("UNCHECKED_CAST")
    open fun animate(val1: T, val2: T, progress: Double, interpolator: Interpolator): T {
        return interpolator.interpolate(val1, val2, progress) as T
    }
}

class AnimatedPoint2D(from: Point2D, to: Point2D)
    : AnimatedValue<Point2D>(from, to) {

    override fun animate(val1: Point2D, val2: Point2D, progress: Double, interpolator: Interpolator): Point2D {
        return interpolate(val1, val2, progress, interpolator)
    }

    private fun interpolate(fromValue: Point2D, toValue: Point2D, progress: Double, interpolator: Interpolator): Point2D {
        val x = interpolator.interpolate(fromValue.x, toValue.x, progress)
        val y = interpolator.interpolate(fromValue.y, toValue.y, progress)

        return Point2D(x, y)
    }
}

class AnimatedPoint3D(from: Point3D, to: Point3D)
    : AnimatedValue<Point3D>(from, to) {

    override fun animate(val1: Point3D, val2: Point3D, progress: Double, interpolator: Interpolator): Point3D {
        return interpolate(val1, val2, progress, interpolator)
    }

    private fun interpolate(fromValue: Point3D, toValue: Point3D, progress: Double, interpolator: Interpolator): Point3D {
        val x = interpolator.interpolate(fromValue.x, toValue.x, progress)
        val y = interpolator.interpolate(fromValue.y, toValue.y, progress)
        val z = interpolator.interpolate(fromValue.z, toValue.z, progress)

        return Point3D(x, y, z)
    }
}

class AnimatedQuadBezierPoint2D
(val path: QuadCurve) : AnimatedValue<Point2D>(Point2D.ZERO, Point2D.ZERO) {

    override fun animate(val1: Point2D, val2: Point2D, progress: Double, interpolator: Interpolator): Point2D {
        return FXGLMath.bezier(
                Point2D(path.startX, path.startY),
                Point2D(path.controlX, path.controlY),
                Point2D(path.endX, path.endY),
                interpolator.interpolate(0.0, 1.0, progress)
        )
    }
}

class AnimatedCubicBezierPoint2D
(val path: CubicCurve) : AnimatedValue<Point2D>(Point2D.ZERO, Point2D.ZERO) {

    override fun animate(val1: Point2D, val2: Point2D, progress: Double, interpolator: Interpolator): Point2D {
        return FXGLMath.bezier(
                Point2D(path.startX, path.startY),
                Point2D(path.controlX1, path.controlY1),
                Point2D(path.controlX2, path.controlY2),
                Point2D(path.endX, path.endY),
                interpolator.interpolate(0.0, 1.0, progress)
        )
    }
}

internal class AnimatedQuadBezierPoint3D
(val path: QuadCurve) : AnimatedValue<Point3D>(Point3D.ZERO, Point3D.ZERO) {

    private val animated2D = AnimatedQuadBezierPoint2D(path)

    override fun animate(val1: Point3D, val2: Point3D, progress: Double, interpolator: Interpolator): Point3D {
        val p = animated2D.animate(Point2D(val1.x, val1.y), Point2D(val2.x, val2.y), progress, interpolator)

        return Point3D(p.x, p.y, 0.0)
    }
}

internal class AnimatedCubicBezierPoint3D
(val path: CubicCurve) : AnimatedValue<Point3D>(Point3D.ZERO, Point3D.ZERO) {

    private val animated2D = AnimatedCubicBezierPoint2D(path)

    override fun animate(val1: Point3D, val2: Point3D, progress: Double, interpolator: Interpolator): Point3D {
        val p = animated2D.animate(Point2D(val1.x, val1.y), Point2D(val2.x, val2.y), progress, interpolator)

        return Point3D(p.x, p.y, 0.0)
    }
}

class AnimatedColor(from: Color, to: Color)
    : AnimatedValue<Color>(from, to) {

    override fun animate(val1: Color, val2: Color, progress: Double, interpolator: Interpolator): Color {
        return Color.color(
                clamp(interpolator.interpolate(val1.red, val2.red, progress)),
                clamp(interpolator.interpolate(val1.green, val2.green, progress)),
                clamp(interpolator.interpolate(val1.blue, val2.blue, progress)),
                clamp(interpolator.interpolate(val1.opacity, val2.opacity, progress))
        )
    }

    // we need to clamp because interpolators can produce values outside [0..1], which crashes Color.color()
    private fun clamp(value: Double) = FXGLMath.clamp(value.toFloat(), 0f, 1f).toDouble()
}

class AnimatedPath
(val path: Shape) : AnimatedValue<Point3D>(Point3D.ZERO, Point3D.ZERO) {

    /**
     * Maps reference time values [0..1] to points on path at that time.
     */
    private val points = hashMapOf<Int, Point3D>()

    init {
        val dummy = Rectangle()
        val pt = PathTransition(Duration.seconds(1.0), path, dummy)
        pt.play()

        var t = 0.0
        var percent = 0

        while (t < 1.0) {
            points[percent++] = Point3D(dummy.translateX, dummy.translateY, 0.0)

            t += 0.01

            pt.jumpTo(Duration.seconds(t))
        }

        pt.jumpTo(Duration.seconds(1.0))

        // hack to ensure that points[0] is not (0, 0)
        points[0] = points[1]!!
        points[100] = Point3D(dummy.translateX, dummy.translateY, 0.0)
    }

    override fun animate(val1: Point3D, val2: Point3D, progress: Double, interpolator: Interpolator): Point3D {
        val t = interpolator.interpolate(0.0, 1.0, progress)

        val key = (t * 100).toInt()

        return points[key]!!
    }
}

class AnimatedStringIncreasing(value: String) : AnimatedValue<String>("", value) {

    override fun animate(val1: String, val2: String, progress: Double, interpolator: Interpolator): String {
        var index = (val2.length * interpolator.interpolate(0.0, 1.0, progress)).toInt()

        index = max(index, 0)
        index = min(index, val2.length)

        return val2.substring(0, index)
    }
}

class AnimatedStringDecreasing(value: String) : AnimatedValue<String>("", value) {

    private val anim = AnimatedStringIncreasing(value)

    override fun animate(val1: String, val2: String, progress: Double, interpolator: Interpolator): String {
        return anim.animate(val1, val2, 1 - progress, interpolator)
    }
}
