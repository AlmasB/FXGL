/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.dsl.components

import com.almasb.fxgl.core.math.FXGLMath
import com.almasb.fxgl.entity.component.Component
import kotlin.math.abs

/**
 * The component automatically rotates the entity to face the direction of movement.
 * The movement direction is calculated based on previous and current entity positions.
 * The rotation angle range is in [-180..180].
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class AutoRotationComponent : Component() {

    private var isSmoothing = false
    private var prevX = 0.0
    private var prevY = 0.0

    override fun onAdded() {
        prevX = entity.x
        prevY = entity.y
    }

    override fun onUpdate(tpf: Double) {
        val angle = FXGLMath.atan2Deg(entity.y - prevY, entity.x - prevX)

        if (!isSmoothing) {
            entity.rotation = angle
        } else {
            entity.rotation = smooth(angle)
        }

        prevX = entity.x
        prevY = entity.y
    }

    private fun smooth(angle: Double): Double {
        val angles = doubleArrayOf(
                angle,
                angle + 360,
                angle - 360
        )

        val closestAngle = angles.minByOrNull { abs(it - entity.rotation) }!!

        var result = entity.rotation * 0.9 + closestAngle * 0.1

        // bring into -180..180 range
        while (result < -180.0) {
            result += 360
        }

        while (result > 180.0) {
            result += -360
        }

        return result
    }

    fun withSmoothing(): AutoRotationComponent {
        isSmoothing = true
        return this
    }

    override fun isComponentInjectionRequired(): Boolean = false
}