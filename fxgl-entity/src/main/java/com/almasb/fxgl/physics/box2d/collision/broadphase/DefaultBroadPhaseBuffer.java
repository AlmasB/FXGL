/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package com.almasb.fxgl.physics.box2d.collision.broadphase;

import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.physics.box2d.callbacks.PairCallback;
import com.almasb.fxgl.physics.box2d.callbacks.TreeCallback;
import com.almasb.fxgl.physics.box2d.callbacks.TreeRayCastCallback;
import com.almasb.fxgl.physics.box2d.collision.AABB;
import com.almasb.fxgl.physics.box2d.collision.RayCastInput;

import java.util.Arrays;

/**
 * The broad-phase is used for computing pairs and performing volume queries and ray casts.
 * This broad-phase does not persist pairs.
 * Instead, this reports potentially new pairs.
 * It is up to the client to consume the new pairs and to track subsequent overlap.
 *
 * @author Daniel Murphy
 */
public class DefaultBroadPhaseBuffer implements TreeCallback, BroadPhase {

    private final BroadPhaseStrategy tree;

    private int moveCapacity = 16;
    private int[] moveBuffer = new int[moveCapacity];
    private int moveCount = 0;

    private int pairCapacity = 16;
    private Pair[] pairBuffer = new Pair[pairCapacity];
    private int pairCount = 0;

    private int m_queryProxyId = NULL_PROXY;

    public DefaultBroadPhaseBuffer(BroadPhaseStrategy strategy) {
        for (int i = 0; i < pairCapacity; i++) {
            pairBuffer[i] = new Pair();
        }

        tree = strategy;
    }

    @Override
    public final int createProxy(AABB aabb, Object userData) {
        int proxyId = tree.createProxy(aabb, userData);
        bufferMove(proxyId);
        return proxyId;
    }

    @Override
    public final void destroyProxy(int proxyId) {
        unbufferMove(proxyId);
        tree.destroyProxy(proxyId);
    }

    @Override
    public final void moveProxy(int proxyId, AABB aabb, Vec2 displacement) {
        boolean buffer = tree.moveProxy(proxyId, aabb, displacement);
        if (buffer) {
            bufferMove(proxyId);
        }
    }

    @Override
    public void touchProxy(int proxyId) {
        bufferMove(proxyId);
    }

    @Override
    public Object getUserData(int proxyId) {
        return tree.getUserData(proxyId);
    }

    @Override
    public boolean testOverlap(int proxyIdA, int proxyIdB) {
        AABB a = tree.getFatAABB(proxyIdA);
        AABB b = tree.getFatAABB(proxyIdB);

        return AABB.testOverlap(a, b);
    }

    @Override
    public final void updatePairs(PairCallback callback) {
        // Reset pair buffer
        pairCount = 0;

        // Perform tree queries for all moving proxies.
        for (int i = 0; i < moveCount; ++i) {
            m_queryProxyId = moveBuffer[i];
            if (m_queryProxyId == NULL_PROXY) {
                continue;
            }

            // We have to query the tree with the fat AABB so that
            // we don't fail to create a pair that may touch later.
            final AABB fatAABB = tree.getFatAABB(m_queryProxyId);

            // Query tree, create pairs and add them pair buffer.
            tree.query(this, fatAABB);
        }

        // Reset move buffer
        moveCount = 0;

        // Sort the pair buffer to expose duplicates.
        Arrays.sort(pairBuffer, 0, pairCount);

        // Send the pairs back to the client.
        int i = 0;
        while (i < pairCount) {
            Pair primaryPair = pairBuffer[i];
            Object userDataA = tree.getUserData(primaryPair.proxyIdA);
            Object userDataB = tree.getUserData(primaryPair.proxyIdB);

            callback.addPair(userDataA, userDataB);
            ++i;

            // Skip any duplicate pairs.
            while (i < pairCount) {
                Pair pair = pairBuffer[i];
                if (pair.proxyIdA != primaryPair.proxyIdA || pair.proxyIdB != primaryPair.proxyIdB) {
                    break;
                }
                ++i;
            }
        }
    }

    @Override
    public final void query(TreeCallback callback, AABB aabb) {
        tree.query(callback, aabb);
    }

    @Override
    public final void raycast(TreeRayCastCallback callback, RayCastInput input) {
        tree.raycast(callback, input);
    }

    private void bufferMove(int proxyId) {
        if (moveCount == moveCapacity) {
            int[] old = moveBuffer;
            moveCapacity *= 2;
            moveBuffer = new int[moveCapacity];
            System.arraycopy(old, 0, moveBuffer, 0, old.length);
        }

        moveBuffer[moveCount] = proxyId;
        ++moveCount;
    }

    private void unbufferMove(int proxyId) {
        for (int i = 0; i < moveCount; i++) {
            if (moveBuffer[i] == proxyId) {
                moveBuffer[i] = NULL_PROXY;
            }
        }
    }

    /**
     * This is called from DynamicTree::query when we are gathering pairs.
     */
    @Override
    public final boolean treeCallback(int proxyId) {
        // A proxy cannot form a pair with itself.
        if (proxyId == m_queryProxyId) {
            return true;
        }

        // Grow the pair buffer as needed.
        if (pairCount == pairCapacity) {
            Pair[] oldBuffer = pairBuffer;
            pairCapacity *= 2;
            pairBuffer = new Pair[pairCapacity];
            System.arraycopy(oldBuffer, 0, pairBuffer, 0, oldBuffer.length);
            for (int i = oldBuffer.length; i < pairCapacity; i++) {
                pairBuffer[i] = new Pair();
            }
        }

        if (proxyId < m_queryProxyId) {
            pairBuffer[pairCount].proxyIdA = proxyId;
            pairBuffer[pairCount].proxyIdB = m_queryProxyId;
        } else {
            pairBuffer[pairCount].proxyIdA = m_queryProxyId;
            pairBuffer[pairCount].proxyIdB = proxyId;
        }

        ++pairCount;
        return true;
    }
}
