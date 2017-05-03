/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
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

package com.almasb.fxgl.entity.animation.control

import com.almasb.fxgl.core.math.FXGLMath
import com.almasb.fxgl.entity.component.PositionComponent
import javafx.animation.Interpolator
import javafx.geometry.Point2D
import javafx.scene.shape.CubicCurve
import javafx.scene.shape.QuadCurve
import javafx.scene.shape.Shape
import javafx.util.Duration

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class TranslationControl(delay: Duration, duration: Duration,
                         cycleCount: Int, val interpolator: Interpolator,
                         val path: Shape?,
                         val startPosition: Point2D, val endPosition: Point2D) : AnimationControl(delay, duration, cycleCount) {

    private lateinit var position: PositionComponent

    override fun onProgress(progress: Double) {
        position.value = interpolate(progress)
    }

    private fun interpolate(progress: Double): Point2D {
        if (path != null) {
            when (path) {
                is QuadCurve -> {
                    return FXGLMath.bezier(Point2D(path.startX, path.startY), Point2D(path.controlX, path.controlY),
                            Point2D(path.endX, path.endY), progress)
                }

                is CubicCurve -> {
                    return FXGLMath.bezier(Point2D(path.startX, path.startY), Point2D(path.controlX1, path.controlY1),
                            Point2D(path.controlX2, path.controlY2), Point2D(path.endX, path.endY), progress)
                }

                else -> {
                    throw IllegalArgumentException("Unsupported path: $path")
                }
            }
        } else {
            return FXGLMath.interpolate(startPosition, endPosition, progress, interpolator)
        }
    }
}