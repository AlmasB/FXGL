/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics.box2d.dynamics

import com.almasb.fxgl.core.math.Vec2
import com.almasb.fxgl.physics.box2d.collision.shapes.Shape

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */

enum class BodyType {

    /**
     * Zero mass, zero velocity, may be manually moved.
     */
    STATIC,

    /**
     * Zero mass, non-zero velocity set by user, moved by solver.
     */
    KINEMATIC,

    /**
     * Positive mass, non-zero velocity determined by forces, moved by solver.
     */
    DYNAMIC
}

/**
 * A body definition holds all the data needed to construct a rigid body.
 * You can safely re-use body definitions.
 * Shapes are added to a body after construction.
 */
class BodyDef {

    /**
     * The body type: static, kinematic, or dynamic.
     * Note: if a dynamic body would have zero mass, the mass is set to one.
     */
    var type = BodyType.STATIC

    /**
     * The world position of the body.
     * Avoid creating bodies at the origin since this can lead to many overlapping shapes.
     */
    var position = Vec2()

    /**
     * The world angle of the body in radians.
     */
    var angle = 0f

    /**
     * The linear velocity of the body in world co-ordinates.
     */
    var linearVelocity = Vec2()

    /**
     * The angular velocity of the body.
     */
    var angularVelocity = 0f

    /**
     * Linear damping is used to reduce the linear velocity. The damping parameter can be larger than
     * 1.0f but the damping effect becomes sensitive to the time step when the damping parameter is
     * large.
     */
    var linearDamping = 0f

    /**
     * Angular damping is use to reduce the angular velocity. The damping parameter can be larger than
     * 1.0f but the damping effect becomes sensitive to the time step when the damping parameter is
     * large.
     */
    var angularDamping = 0f

    /**
     * Set this flag to false if this body should never fall asleep. Note that this increases CPU
     * usage.
     */
    var isAllowSleep = true

    /**
     * Is this body initially sleeping?
     */
    var isAwake = true

    /**
     * Should this body be prevented from rotating? Useful for characters.
     */
    var isFixedRotation = false

    /**
     * Is this a fast moving body that should be prevented from tunneling through other moving bodies?
     * Note that all bodies are prevented from tunneling through kinematic and static bodies. This
     * setting is only considered on dynamic bodies.
     * You should use this flag sparingly since it increases processing time.
     */
    var isBullet = false

    /**
     * Does this body start out active?
     */
    var isActive = true

    /**
     * Experimental: scales the inertia tensor.
     */
    var gravityScale = 1f

    var userData: Any? = null
}

/**
 * A fixture definition is used to create a fixture.
 * This class defines an abstract fixture definition.
 * You can reuse fixture definitions safely.
 */
class FixtureDef {

    /**
     * The friction coefficient, usually in the range [0,1].
     */
    var friction = 0.2f

    /**
     * The restitution (elasticity) usually in the range [0,1].
     */
    var restitution = 0.0f

    /**
     * The density, usually in kg/m^2
     */
    var density = 0.0f

    /**
     * A sensor shape collects contact information but never generates a collision response.
     */
    var isSensor = false

    /**
     * Contact filtering data
     */
    var filter = Filter()

    /**
     * The shape, this must be set. The shape will be cloned, so you can create the shape on the
     * stack.
     */
    var shape: Shape? = null

    var userData: Any? = null

    fun copy(): FixtureDef {
        val def = FixtureDef()
        def.friction = friction
        def.restitution = restitution
        def.density = density
        def.isSensor = isSensor
        def.filter.set(filter)
        def.shape = shape!!.clone()
        def.userData = userData

        return def
    }

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