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

package com.almasb.fxgl.animation

import com.almasb.fxgl.core.math.FXGLMath
import javafx.animation.Interpolator
import javafx.geometry.Point2D

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
open class AnimatedValue<T>
@JvmOverloads constructor(val from: T, val to: T, val interpolator: Interpolator = Interpolator.LINEAR) {

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