/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
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
        return object : Animation<Point2D>(animationBuilder, AnimatedPoint2D(startScale, endScale, animationBuilder.interpolator)) {

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