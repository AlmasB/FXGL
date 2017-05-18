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

import com.almasb.fxgl.animation.AnimatedPoint2D
import com.almasb.fxgl.animation.Animation
import javafx.geometry.Point2D

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class ScaleAnimationBuilder(private val animationBuilder: AnimationBuilder) {

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

    fun build(): Animation<*> {
        return object : Animation<Point2D>(animationBuilder.delay, animationBuilder.duration, animationBuilder.times,
                AnimatedPoint2D(startScale, endScale, animationBuilder.interpolator)) {

            override fun onProgress(value: Point2D) {
                animationBuilder.entities.forEach {
                    it.setScaleX(value.x)
                    it.setScaleY(value.y)
                }
            }
        }
    }

    fun buildAndPlay(): Animation<*> {
        val anim = build()
        anim.startInPlayState()
        return anim
    }
}