/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics

import com.almasb.fxgl.core.math.Vec2
import com.almasb.fxgl.physics.box2d.callbacks.RayCastCallback
import com.almasb.fxgl.physics.box2d.dynamics.Fixture

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class EdgeCallback : RayCastCallback {

    var fixture: Fixture? = null
        private set

    var point: Vec2? = null
        private set

    var bestFraction = 1.0f
        private set

    override fun reportFixture(fixture: Fixture, point: Vec2, normal: Vec2?, fraction: Float): Float {
        val e = fixture.body.entity
        if (e.getComponent(PhysicsComponent::class.java).isRaycastIgnored)
            return 1.0f

        if (fraction < bestFraction) {
            this.fixture = fixture
            this.point = point.clone()
            bestFraction = fraction
        }

        return bestFraction
    }

    fun reset() {
        fixture = null
        point = null
        bestFraction = 1.0f
    }
}