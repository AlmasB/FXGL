/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.dsl.components

import com.almasb.fxgl.entity.component.Component
import com.almasb.fxgl.entity.component.CopyableComponent
import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.value.ChangeListener
import javafx.geometry.Point2D

/**
 * Generic projectile component.
 * Automatically rotates the entity based on velocity direction.
 * The rotation of 0 degrees is assumed to be facing right.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
class ProjectileComponent(direction: Point2D, speed: Double) : Component(), CopyableComponent<ProjectileComponent> {

    /**
     * Constructs the component with direction facing right and 1.0 speed.
     */
    constructor() : this(Point2D(1.0, 0.0), 1.0)

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

    private val speedProp = SimpleDoubleProperty(speed)

    private val speedListener = ChangeListener<Number> { _, _, newSpeed ->
        velocity = velocity.normalize().multiply(newSpeed.toDouble())
        updateRotation()
    }

    fun speedProperty(): DoubleProperty = speedProp

    var speed: Double
        get() = speedProp.value
        set(value) { speedProp.value = value }

    private var isAllowRotation: Boolean = true

    /**
     * Allow to disable / enable projectile rotation towards direction of travel.
     */
    fun allowRotation(allowRotation: Boolean): ProjectileComponent {
        isAllowRotation = allowRotation
        return this
    }

    /**
     * Checks if rotation is enabled, if so then rotate.
     */
    private fun updateRotation(){
        if (isAllowRotation)
            entity.rotateToVector(velocity)
    }

    override fun onAdded() {
        updateRotation()

        speedProp.addListener(speedListener)
    }

    override fun onUpdate(tpf: Double) {
        entity.translate(velocity.multiply(tpf))
    }

    override fun onRemoved() {
        speedProp.removeListener(speedListener)
    }

    override fun copy(): ProjectileComponent {
        return ProjectileComponent(direction, speed)
    }

    override fun isComponentInjectionRequired(): Boolean = false
}
