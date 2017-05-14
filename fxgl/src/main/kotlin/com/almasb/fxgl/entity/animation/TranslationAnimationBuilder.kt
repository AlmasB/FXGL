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

package com.almasb.fxgl.entity.animation

import com.almasb.fxgl.animation.*
import javafx.geometry.Point2D
import javafx.scene.shape.CubicCurve
import javafx.scene.shape.QuadCurve
import javafx.scene.shape.Shape

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class TranslationAnimationBuilder(private val animationBuilder: AnimationBuilder) {

    private var path: Shape? = null
    private var fromPoint = Point2D.ZERO
    private var toPoint = Point2D.ZERO

    fun alongPath(path: Shape): TranslationAnimationBuilder {
        this.path = path
        return this
    }

    fun from(start: Point2D): TranslationAnimationBuilder {
        fromPoint = start
        return this
    }

    fun to(end: Point2D): TranslationAnimationBuilder {
        toPoint = end
        return this
    }

    fun build(): Animation<*> {

        path?.let { curve ->
            when (curve) {
                is QuadCurve -> return makeAnim(AnimatedQuadBezierPoint2D(curve))
                is CubicCurve -> return makeAnim(AnimatedCubicBezierPoint2D(curve))
                else -> throw IllegalArgumentException("Unsupported path: $curve")
            }
        }

        return makeAnim(AnimatedPoint2D(fromPoint, toPoint, animationBuilder.interpolator))
    }

    private fun makeAnim(animValue: AnimatedValue<Point2D>): Animation<Point2D> {
        return object : Animation<Point2D>(animationBuilder.delay, animationBuilder.duration, animationBuilder.times,
                animValue) {

            override fun onProgress(value: Point2D) {
                animationBuilder.entities.forEach { it.position = value }
            }
        }
    }

    fun buildAndPlay(): Animation<*> {
        val anim = build()
        anim.startInPlayState()
        return anim
    }
}