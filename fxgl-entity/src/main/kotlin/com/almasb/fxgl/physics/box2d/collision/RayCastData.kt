/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics.box2d.collision

import com.almasb.fxgl.core.math.Vec2

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */

/**
 * Ray-cast input data. The ray extends from p1 to p1 + maxFraction * (p2 - p1).
 */
class RayCastInput {

    @JvmField
    val p1 = Vec2()

    @JvmField
    val p2 = Vec2()

    @JvmField
    var maxFraction = 0f

    fun set(other: RayCastInput) {
        p1.set(other.p1)
        p2.set(other.p2)
        maxFraction = other.maxFraction
    }
}

/**
 * Ray-cast output data. The ray hits at p1 + fraction * (p2 - p1), where p1 and p2
 * come from [RayCastInput].
 */
class RayCastOutput {
    @JvmField
    val normal = Vec2()

    @JvmField
    var fraction = 0f

    fun set(other: RayCastOutput) {
        normal.set(other.normal)
        fraction = other.fraction
    }
}