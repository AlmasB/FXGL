/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics.box2d.dynamics

import com.almasb.fxgl.physics.box2d.collision.shapes.Shape

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */

/**
 * A fixture definition is used to create a fixture.
 * This class defines an abstract fixture definition.
 * You can reuse fixture definitions safely.
 */
class FixtureDef {

    /**
     * The friction coefficient, usually in the range [0,1].
     */
    var friction: Float = 0.2f

    /**
     * The restitution (elasticity) usually in the range [0,1].
     */
    var restitution: Float = 0.0f

    /**
     * The density, usually in kg/m^2
     */
    var density: Float = 0.0f

    /**
     * A sensor shape collects contact information but never generates a collision response.
     */
    var isSensor: Boolean = false

    /**
     * Contact filtering data
     */
    var filter: Filter = Filter()

    /**
     * The shape, this must be set. The shape will be cloned, so you can create the shape on the
     * stack.
     */
    lateinit var shape: Shape

    var userData: Any? = null

    /* FLUENT API */

    fun friction(friction: Float): FixtureDef {
        this.friction = friction
        return this
    }

    fun restitution(restitution: Float): FixtureDef {
        this.restitution = restitution
        return this
    }

    fun density(density: Float): FixtureDef {
        this.density = density
        return this
    }

    fun filter(filter: Filter): FixtureDef {
        this.filter = filter
        return this
    }

    fun shape(shape: Shape): FixtureDef {
        this.shape = shape
        return this
    }

    fun sensor(isSensor: Boolean): FixtureDef {
        this.isSensor = isSensor
        return this
    }

    fun userData(userData: Any): FixtureDef {
        this.userData = userData
        return this
    }
}