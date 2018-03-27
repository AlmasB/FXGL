/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics.box2d.callbacks;

import com.almasb.fxgl.core.math.Vec2;

public interface ParticleRaycastCallback {

    /**
     * Called for each particle found in the query.
     *
     * @param index particle index
     * @param point the point of initial intersection
     * @param normal the normal vector at the point of intersection
     * @param fraction fraction
     * @return -1 to filter, 0 to terminate, fraction to clip the ray for
     * closest hit, 1 to continue
     */
    float reportParticle(int index, Vec2 point, Vec2 normal, float fraction);
}
