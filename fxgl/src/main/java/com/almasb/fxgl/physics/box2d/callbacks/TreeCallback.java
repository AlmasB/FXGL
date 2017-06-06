/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics.box2d.callbacks;

import com.almasb.fxgl.physics.box2d.collision.broadphase.DynamicTree;

/**
 * Callback for {@link DynamicTree}.
 *
 * @author Daniel Murphy
 */
public interface TreeCallback {

    /**
     * Callback from a query request.
     *
     * @param proxyId the id of the proxy
     * @return if the query should be continued
     */
    boolean treeCallback(int proxyId);
}
