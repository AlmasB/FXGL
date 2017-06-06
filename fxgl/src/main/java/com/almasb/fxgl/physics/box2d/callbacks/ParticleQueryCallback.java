/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics.box2d.callbacks;

import com.almasb.fxgl.physics.box2d.dynamics.World;

/**
 * Callback class for AABB queries. See
 * {@link World#queryAABB(QueryCallback, com.almasb.fxgl.physics.box2d.collision.AABB)}.
 *
 * @author dmurph
 */
public interface ParticleQueryCallback {

    /**
     * Called for each particle found in the query AABB.
     *
     * @return false to terminate the query.
     */
    boolean reportParticle(int index);
}
