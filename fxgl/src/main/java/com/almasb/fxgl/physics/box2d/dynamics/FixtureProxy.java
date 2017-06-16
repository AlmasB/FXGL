/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics.box2d.dynamics;

import com.almasb.fxgl.physics.box2d.collision.AABB;

/**
 * This proxy is used internally to connect fixtures to the broad-phase.
 *
 * @author Daniel
 */
public class FixtureProxy {
    final AABB aabb = new AABB();
    Fixture fixture;
    int childIndex;
    int proxyId;
}
