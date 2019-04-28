/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.dsl.components

import com.almasb.fxgl.core.math.FXGLMath.*
import com.almasb.fxgl.core.math.Vec2
import com.almasb.fxgl.dsl.random
import com.almasb.fxgl.entity.component.Component
import javafx.geometry.Point2D
import javafx.geometry.Rectangle2D

/**
 * Randomly moves an entity within given bounds.
 * Uses entity's bbox to determine if the entity is within bounds.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class RandomMoveComponent
@JvmOverloads constructor(
        var bounds: Rectangle2D,
        var moveSpeed: Double,
        var tx: Double = random(100, 10000).toDouble()) : Component() {

    private val angleAdjustRate = random(0.0, 0.5)

    private val velocity = Vec2()
    private var directionAngle = toDegrees(random(-1, 1) * PI2)

    private val rotationSpeed = random(-100, 100)

    override fun onUpdate(tpf: Double) {
        adjustAngle(tpf)
        move(tpf)
        rotate(tpf)

        tx += tpf

        checkBounds()
    }

    private fun adjustAngle(tpf: Double) {
        if (randomBoolean(angleAdjustRate)) {
            directionAngle += toDegrees(noise1D(tx) - 0.5)
        }
    }

    private fun move(tpf: Double) {
        val directionVector = Vec2.fromAngle(directionAngle).mulLocal(moveSpeed)

        velocity.addLocal(directionVector).mulLocal(tpf)

        entity.translate(velocity)
    }

    private fun checkBounds() {
        if (entity.x < bounds.minX
                || entity.y < bounds.minY
                || entity.rightX >= bounds.maxX
                || entity.bottomY >= bounds.maxY) {

            val newDirectionVector = Point2D((bounds.minX + bounds.maxX) / 2, (bounds.minY + bounds.maxY) / 2)
                    .subtract(entity.center)

            val angle = toDegrees(Math.atan(newDirectionVector.y / newDirectionVector.x))
            directionAngle = if (newDirectionVector.x > 0) angle else 180 + angle
        }
    }

    private fun rotate(tpf: Double) {
        entity.rotateBy(rotationSpeed * tpf)
    }
}