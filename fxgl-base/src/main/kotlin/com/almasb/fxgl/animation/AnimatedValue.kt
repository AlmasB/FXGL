/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.animation

import com.almasb.fxgl.core.math.FXGLMath
import javafx.animation.Interpolator
import javafx.geometry.Point2D
import javafx.scene.paint.Color
import javafx.scene.shape.CubicCurve
import javafx.scene.shape.QuadCurve

/**
 * Built-in supported types: Point2D, Double, Int, Long, Float.
 * Any other types must implement animate().
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
open class AnimatedValue<T>
@JvmOverloads constructor(val from: T, val to: T, var interpolator: Interpolator = Interpolator.LINEAR) {

    fun getValue(progress: Double): T {
        return animate(from, to, progress, interpolator)
    }

    @Suppress("UNCHECKED_CAST")
    open fun animate(val1: T, val2: T, progress: Double, interpolator: Interpolator): T {
        return interpolator.interpolate(val1, val2, progress) as T
    }
}

class AnimatedPoint2D
@JvmOverloads constructor(from: Point2D, to: Point2D, interpolator: Interpolator = Interpolator.LINEAR)
    : AnimatedValue<Point2D>(from, to, interpolator) {

    override fun animate(val1: Point2D, val2: Point2D, progress: Double, interpolator: Interpolator): Point2D {
        return FXGLMath.interpolate(val1, val2, progress, interpolator)
    }
}

class AnimatedQuadBezierPoint2D
(val path: QuadCurve) : AnimatedValue<Point2D>(Point2D.ZERO, Point2D.ZERO) {

    override fun animate(val1: Point2D, val2: Point2D, progress: Double, interpolator: Interpolator): Point2D {
        return FXGLMath.bezier(
                Point2D(path.startX, path.startY),
                Point2D(path.controlX, path.controlY),
                Point2D(path.endX, path.endY),
                progress
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
                progress
        )
    }
}

class AnimatedColor
@JvmOverloads constructor(from: Color, to: Color, interpolator: Interpolator = Interpolator.LINEAR)
    : AnimatedValue<Color>(from, to, interpolator) {

    override fun animate(val1: Color, val2: Color, progress: Double, interpolator: Interpolator): Color {
        return Color.color(
                FXGLMath.interpolate(val1.red, val2.red, progress, interpolator),
                FXGLMath.interpolate(val1.green, val2.green, progress, interpolator),
                FXGLMath.interpolate(val1.blue, val2.blue, progress, interpolator),
                FXGLMath.interpolate(val1.opacity, val2.opacity, progress, interpolator)
        )
    }
}