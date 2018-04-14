/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.animation

import com.almasb.fxgl.animation.AnimatedValue
import com.almasb.fxgl.animation.Animation

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class RotationAnimationBuilder(private val animationBuilder: AnimationBuilder) {

    private var startAngle = 0.0
    private var endAngle = 0.0

    fun rotateFrom(startAngle: Double): RotationAnimationBuilder {
        this.startAngle = startAngle
        return this
    }

    fun rotateTo(endAngle: Double): RotationAnimationBuilder {
        this.endAngle = endAngle
        return this
    }

    fun build(): Animation<*> {
        return object : Animation<Double>(animationBuilder, AnimatedValue<Double>(startAngle, endAngle, animationBuilder.interpolator)) {

            override fun onProgress(value: Double) {
                animationBuilder.entities.forEach { it.rotation = value }
            }
        }
    }

    fun buildAndPlay(): Animation<*> {
        val anim = build()
        anim.startInPlayState()
        return anim
    }
}