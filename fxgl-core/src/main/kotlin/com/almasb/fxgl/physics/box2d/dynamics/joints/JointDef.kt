/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics.box2d.dynamics.joints

import com.almasb.fxgl.physics.box2d.dynamics.Body
import com.almasb.fxgl.physics.box2d.dynamics.World

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
abstract class JointDef<T : Joint>
@JvmOverloads constructor(
        var bodyA: Body? = null,
        var bodyB: Body? = null,
) {

    /**
     * Application-specific user data.
     */
    var userData: Any? = null

    /**
     * Whether collision between the two bodies is allowed.
     */
    var isBodyCollisionAllowed = false

    protected abstract fun createJoint(world: World): T
}