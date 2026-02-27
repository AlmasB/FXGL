/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics.box2d.collision.broadphase;

import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.physics.box2d.callbacks.TreeCallback;
import com.almasb.fxgl.physics.box2d.callbacks.TreeRayCastCallback;
import com.almasb.fxgl.physics.box2d.collision.AABB;
import com.almasb.fxgl.physics.box2d.collision.RayCastInput;

public interface BroadPhaseStrategy {

    /**
     * Create a proxy. Provide a tight fitting AABB and a userData pointer.
     */
    int createProxy(AABB aabb, Object userData);

    /**
     * Destroy a proxy
     */
    void destroyProxy(int proxyId);

    /**
     * Move a proxy with a swepted AABB. If the proxy has moved outside of its fattened AABB, then the
     * proxy is removed from the tree and re-inserted. Otherwise the function returns immediately.
     *
     * @return true if the proxy was re-inserted.
     */
    boolean moveProxy(int proxyId, AABB aabb, Vec2 displacement);

    Object getUserData(int proxyId);

    AABB getFatAABB(int proxyId);

    /**
     * Query an AABB for overlapping proxies. The callback class is called for each proxy that
     * overlaps the supplied AABB.
     */
    void query(TreeCallback callback, AABB aabb);

    /**
     * Ray-cast against the proxies in the tree. This relies on the callback to perform a exact
     * ray-cast in the case were the proxy contains a shape. The callback also performs the any
     * collision filtering. This has performance roughly equal to k * log(n), where k is the number of
     * collisions and n is the number of proxies in the tree.
     *
     * @param input    the ray-cast input data. The ray extends from p1 to p1 + maxFraction * (p2 - p1).
     * @param callback a callback class that is called for each proxy that is hit by the ray.
     */
    void raycast(TreeRayCastCallback callback, RayCastInput input);
}