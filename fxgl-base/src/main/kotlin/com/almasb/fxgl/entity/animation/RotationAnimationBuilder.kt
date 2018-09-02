/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.animation

import com.almasb.fxgl.animation.AnimatedValue
import com.almasb.fxgl.animation.Animation
import com.almasb.fxgl.core.util.Consumer

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class RotationAnimationBuilder(private val animationBuilder: EntityAnimationBuilder) {

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
        return animationBuilder.animationBuilder.build(
                AnimatedValue<Double>(startAngle, endAngle),
                Consumer { value -> animationBuilder.entities.forEach { it.rotation = value } }
                )
    }

    fun buildAndPlay(): Animation<*> {
        val anim = build()
        anim.startInPlayState()
        return anim
    }
}