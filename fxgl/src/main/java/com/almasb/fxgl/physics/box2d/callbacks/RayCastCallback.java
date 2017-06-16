/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics.box2d.callbacks;

import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.physics.box2d.dynamics.Fixture;
import com.almasb.fxgl.physics.box2d.dynamics.World;

/**
 * Callback class for ray casts.
 * See {@link World#raycast(RayCastCallback, Vec2, Vec2)}
 *
 * @author Daniel Murphy
 */
public interface RayCastCallback {

    /**
     * Called for each fixture found in the query. You control how the ray cast
     * proceeds by returning a float:
     * <ul>
     *     <li>return -1: ignore this fixture and continue</li>
     *     <li>return 0: terminate the ray cast</li>
     *     <li>return fraction: clip the ray to this point</li>
     *     <li>return 1: don't clip the ray and continue</li>
     * </ul>
     *
     * @param fixture the fixture hit by the ray
     * @param point the point of initial intersection
     * @param normal the normal vector at the point of intersection
     * @param fraction fraction
     * @return -1 to filter, 0 to terminate, fraction to clip the ray for
     * closest hit, 1 to continue
     */
    float reportFixture(Fixture fixture, Vec2 point, Vec2 normal, float fraction);
}
