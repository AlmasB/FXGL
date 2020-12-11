/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.dsl.components

import com.almasb.fxgl.entity.component.Component
import javafx.geometry.Point2D

/**
 * Generic projectile component.
 * Automatically rotates the entity based on velocity direction.
 * The rotation of 0 degrees is assumed to be facing right.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 * @author https://github.com/AahzBrut
 */
class ProjectileWithAccelerationComponent(direction: Point2D, speed: Double) : Component() {

    constructor(direction: Point2D, speed: Double, acceleration: Point2D) : this(direction, speed) {
        this.acceleration = acceleration
    }

    var acceleration: Point2D = Point2D(0.0, 0.0)


    var velocity: Point2D = direction.normalize().multiply(speed)
        private set

    /**
     * Set direction in which projectile is moving.
     */
    var direction: Point2D
        get() = velocity.normalize()
        set(direction) {
            velocity = direction.normalize().multiply(speed)
            updateRotation()
        }

    var speed: Double = speed
        set(value) {
            field = value

            velocity = velocity.normalize().multiply(speed)
            updateRotation()
        }

    private var isAllowRotation: Boolean = true

    /**
     * Allow to disable / enable projectile rotation towards direction of travel.
     */
    fun allowRotation(allowRotation: Boolean): ProjectileWithAccelerationComponent {
        isAllowRotation = allowRotation
        return this
    }

    /**
     * Checks if rotation is enabled, if so then rotate.
     */
    private fun updateRotation() {
        if (isAllowRotation)
            entity.rotateToVector(velocity)
    }

    override fun onAdded() {
        updateRotation()
    }

    override fun onUpdate(tpf: Double) {
        val prevVelocity = velocity
        speed = velocity.add(acceleration.multiply(tpf)).magnitude()
        direction = velocity.add(acceleration.multiply(tpf))
        // translating entity by average velocity per frame
        // i.e. ((initial velocity + final velocity) / 2) * delta time
        entity.translate(velocity.add(prevVelocity).multiply(tpf / 2.0))
    }

    override fun isComponentInjectionRequired(): Boolean = false
}