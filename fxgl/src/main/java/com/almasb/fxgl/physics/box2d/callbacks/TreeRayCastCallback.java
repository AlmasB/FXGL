/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics.box2d.callbacks;

import com.almasb.fxgl.physics.box2d.collision.RayCastInput;
import com.almasb.fxgl.physics.box2d.collision.broadphase.DynamicTree;

/**
 * Callback for {@link DynamicTree}
 *
 * @author Daniel Murphy
 */
public interface TreeRayCastCallback {

    /**
     * @param input raycast input
     * @param nodeId id of the node
     * @return the fraction to the node
     */
    float raycastCallback(RayCastInput input, int nodeId);
}
