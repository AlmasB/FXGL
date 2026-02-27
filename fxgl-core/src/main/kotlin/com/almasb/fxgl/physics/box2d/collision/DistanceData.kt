/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics.box2d.collision

import com.almasb.fxgl.core.math.Vec2
import com.almasb.fxgl.physics.box2d.common.Transform

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */

/**
 * Input for Distance with the option to use the shape radii in the computation.
 */
internal class DistanceInput {

    @JvmField
    var proxyA = DistanceProxy()

    @JvmField
    var proxyB = DistanceProxy()

    @JvmField
    var transformA = Transform()

    @JvmField
    var transformB = Transform()

    @JvmField
    var useRadii = false
}

/**
 * Output for Distance.
 */
internal class DistanceOutput {

    /**
     * Closest point on shapeA.
     */
    @JvmField
    val pointA = Vec2()

    /**
     * Closest point on shapeB.
     */
    @JvmField
    val pointB = Vec2()

    @JvmField
    var distance = 0f

    /**
     * Number of gjk iterations used.
     */
    @JvmField
    var iterations = 0
}