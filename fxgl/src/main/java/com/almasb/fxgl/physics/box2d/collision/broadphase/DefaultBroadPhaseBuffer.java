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

    private int proxyCount = 0;

    private int m_moveCapacity = 16;
    private int[] m_moveBuffer = new int[m_moveCapacity];

    private int m_moveCount = 0;

    private int m_pairCapacity = 16;
    private Pair[] m_pairBuffer = new Pair[m_pairCapacity];

    private int m_pairCount = 0;

    private int m_queryProxyId = NULL_PROXY;

    public DefaultBroadPhaseBuffer(BroadPhaseStrategy strategy) {
        for (int i = 0; i < m_pairCapacity; i++) {
            m_pairBuffer[i] = new Pair();
        }

        tree = strategy;
    }

    @Override
    public final int createProxy(final AABB aabb, Object userData) {
        int proxyId = tree.createProxy(aabb, userData);
        ++proxyCount;
        bufferMove(proxyId);
        return proxyId;
    }

    @Override
    public final void destroyProxy(int proxyId) {
        unbufferMove(proxyId);
        --proxyCount;
        tree.destroyProxy(proxyId);
    }

    @Override
    public final void moveProxy(int proxyId, final AABB aabb, final Vec2 displacement) {
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
    public AABB getFatAABB(int proxyId) {
        return tree.getFatAABB(proxyId);
    }

    @Override
    public boolean testOverlap(int proxyIdA, int proxyIdB) {
        AABB a = tree.getFatAABB(proxyIdA);
        AABB b = tree.getFatAABB(proxyIdB);

        return AABB.testOverlap(a, b);
    }

    @Override
    public final int getProxyCount() {
        return proxyCount;
    }

    @Override
    public final void updatePairs(PairCallback callback) {
        // Reset pair buffer
        m_pairCount = 0;

        // Perform tree queries for all moving proxies.
        for (int i = 0; i < m_moveCount; ++i) {
            m_queryProxyId = m_moveBuffer[i];
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
        m_moveCount = 0;

        // Sort the pair buffer to expose duplicates.
        Arrays.sort(m_pairBuffer, 0, m_pairCount);

        // Send the pairs back to the client.
        int i = 0;
        while (i < m_pairCount) {
            Pair primaryPair = m_pairBuffer[i];
            Object userDataA = tree.getUserData(primaryPair.proxyIdA);
            Object userDataB = tree.getUserData(primaryPair.proxyIdB);

            callback.addPair(userDataA, userDataB);
            ++i;

            // Skip any duplicate pairs.
            while (i < m_pairCount) {
                Pair pair = m_pairBuffer[i];
                if (pair.proxyIdA != primaryPair.proxyIdA || pair.proxyIdB != primaryPair.proxyIdB) {
                    break;
                }
                ++i;
            }
        }
    }

    @Override
    public final void query(final TreeCallback callback, final AABB aabb) {
        tree.query(callback, aabb);
    }

    @Override
    public final void raycast(final TreeRayCastCallback callback, final RayCastInput input) {
        tree.raycast(callback, input);
    }

    @Override
    public final int getTreeHeight() {
        return tree.getHeight();
    }

    @Override
    public int getTreeBalance() {
        return tree.getMaxBalance();
    }

    @Override
    public float getTreeQuality() {
        return tree.getAreaRatio();
    }

    protected final void bufferMove(int proxyId) {
        if (m_moveCount == m_moveCapacity) {
            int[] old = m_moveBuffer;
            m_moveCapacity *= 2;
            m_moveBuffer = new int[m_moveCapacity];
            System.arraycopy(old, 0, m_moveBuffer, 0, old.length);
        }

        m_moveBuffer[m_moveCount] = proxyId;
        ++m_moveCount;
    }

    protected final void unbufferMove(int proxyId) {
        for (int i = 0; i < m_moveCount; i++) {
            if (m_moveBuffer[i] == proxyId) {
                m_moveBuffer[i] = NULL_PROXY;
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
        if (m_pairCount == m_pairCapacity) {
            Pair[] oldBuffer = m_pairBuffer;
            m_pairCapacity *= 2;
            m_pairBuffer = new Pair[m_pairCapacity];
            System.arraycopy(oldBuffer, 0, m_pairBuffer, 0, oldBuffer.length);
            for (int i = oldBuffer.length; i < m_pairCapacity; i++) {
                m_pairBuffer[i] = new Pair();
            }
        }

        if (proxyId < m_queryProxyId) {
            m_pairBuffer[m_pairCount].proxyIdA = proxyId;
            m_pairBuffer[m_pairCount].proxyIdB = m_queryProxyId;
        } else {
            m_pairBuffer[m_pairCount].proxyIdA = m_queryProxyId;
            m_pairBuffer[m_pairCount].proxyIdB = proxyId;
        }

        ++m_pairCount;
        return true;
    }
}
