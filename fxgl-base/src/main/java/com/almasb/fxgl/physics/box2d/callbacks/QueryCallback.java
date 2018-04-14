/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics.box2d.callbacks;

import com.almasb.fxgl.physics.box2d.dynamics.Fixture;
import com.almasb.fxgl.physics.box2d.dynamics.World;

/**
 * Callback class for AABB queries.
 * See {@link World#queryAABB(QueryCallback, com.almasb.fxgl.physics.box2d.collision.AABB)}.
 *
 * @author Daniel Murphy
 */
public interface QueryCallback {

    /**
     * Called for each fixture found in the query AABB.
     *
     * @param fixture the fixture
     * @return false to terminate the query.
     */
    boolean reportFixture(Fixture fixture);
}
