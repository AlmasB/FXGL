/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package com.almasb.fxgl.physics.box2d.collision.broadphase;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.physics.box2d.callbacks.TreeCallback;
import com.almasb.fxgl.physics.box2d.callbacks.TreeRayCastCallback;
import com.almasb.fxgl.physics.box2d.collision.AABB;
import com.almasb.fxgl.physics.box2d.collision.RayCastInput;
import com.almasb.fxgl.physics.box2d.common.JBoxSettings;

/**
 * A dynamic tree arranges data in a binary tree to accelerate queries such as volume queries and
 * ray casts. Leafs are proxies with an AABB. In the tree we expand the proxy AABB by _fatAABBFactor
 * so that the proxy AABB is bigger than the client object. This allows the client object to move by
 * small amounts without triggering a tree update.
 *
 * @author daniel
 */
public class DynamicTree implements BroadPhaseStrategy {
    private static final int NULL_NODE = -1;

    private DynamicTreeNode root = null;
    private DynamicTreeNode[] m_nodes = new DynamicTreeNode[16];
    private int m_nodeCount = 0;
    private int m_nodeCapacity = 16;

    private int m_freeList = 0;

    private DynamicTreeNode[] nodeStack = new DynamicTreeNode[20];
    private int nodeStackIndex = 0;

    public DynamicTree() {
        // Build a linked list for the free list.
        for (int i = m_nodeCapacity - 1; i >= 0; i--) {
            m_nodes[i] = new DynamicTreeNode(i);
            m_nodes[i].parent = (i == m_nodeCapacity - 1) ? null : m_nodes[i + 1];
            m_nodes[i].height = -1;
        }
    }

    @Override
    public final int createProxy(final AABB aabb, Object userData) {
        final DynamicTreeNode node = allocateNode();
        int proxyId = node.id;
        // Fatten the aabb
        final AABB nodeAABB = node.aabb;
        nodeAABB.lowerBound.x = aabb.lowerBound.x - JBoxSettings.aabbExtension;
        nodeAABB.lowerBound.y = aabb.lowerBound.y - JBoxSettings.aabbExtension;
        nodeAABB.upperBound.x = aabb.upperBound.x + JBoxSettings.aabbExtension;
        nodeAABB.upperBound.y = aabb.upperBound.y + JBoxSettings.aabbExtension;
        node.userData = userData;

        insertLeaf(proxyId);

        return proxyId;
    }

    @Override
    public final void destroyProxy(int proxyId) {
        DynamicTreeNode node = m_nodes[proxyId];

        removeLeaf(node);
        freeNode(node);
    }

    @Override
    public final boolean moveProxy(int proxyId, final AABB aabb, Vec2 displacement) {
        final DynamicTreeNode node = m_nodes[proxyId];

        final AABB nodeAABB = node.aabb;
        // if (nodeAABB.contains(aabb)) {
        if (nodeAABB.lowerBound.x <= aabb.lowerBound.x && nodeAABB.lowerBound.y <= aabb.lowerBound.y
                && aabb.upperBound.x <= nodeAABB.upperBound.x && aabb.upperBound.y <= nodeAABB.upperBound.y) {
            return false;
        }

        removeLeaf(node);

        // Extend AABB
        final Vec2 lowerBound = nodeAABB.lowerBound;
        final Vec2 upperBound = nodeAABB.upperBound;
        lowerBound.x = aabb.lowerBound.x - JBoxSettings.aabbExtension;
        lowerBound.y = aabb.lowerBound.y - JBoxSettings.aabbExtension;
        upperBound.x = aabb.upperBound.x + JBoxSettings.aabbExtension;
        upperBound.y = aabb.upperBound.y + JBoxSettings.aabbExtension;

        // Predict AABB displacement.
        final float dx = displacement.x * JBoxSettings.aabbMultiplier;
        final float dy = displacement.y * JBoxSettings.aabbMultiplier;
        if (dx < 0.0f) {
            lowerBound.x += dx;
        } else {
            upperBound.x += dx;
        }

        if (dy < 0.0f) {
            lowerBound.y += dy;
        } else {
            upperBound.y += dy;
        }

        insertLeaf(proxyId);
        return true;
    }

    @Override
    public final Object getUserData(int proxyId) {
        return m_nodes[proxyId].userData;
    }

    @Override
    public final AABB getFatAABB(int proxyId) {
        return m_nodes[proxyId].aabb;
    }

    @Override
    public final void query(TreeCallback callback, AABB aabb) {
        nodeStackIndex = 0;
        nodeStack[nodeStackIndex++] = root;

        while (nodeStackIndex > 0) {
            DynamicTreeNode node = nodeStack[--nodeStackIndex];
            if (node == null) {
                continue;
            }

            if (AABB.testOverlap(node.aabb, aabb)) {
                if (node.child1 == null) {
                    boolean proceed = callback.treeCallback(node.id);
                    if (!proceed) {
                        return;
                    }
                } else {
                    if (nodeStack.length - nodeStackIndex - 2 <= 0) {
                        DynamicTreeNode[] newBuffer = new DynamicTreeNode[nodeStack.length * 2];
                        System.arraycopy(nodeStack, 0, newBuffer, 0, nodeStack.length);
                        nodeStack = newBuffer;
                    }
                    nodeStack[nodeStackIndex++] = node.child1;
                    nodeStack[nodeStackIndex++] = node.child2;
                }
            }
        }
    }

    private final Vec2 r = new Vec2();
    private final AABB aabb = new AABB();
    private final RayCastInput subInput = new RayCastInput();

    @Override
    public void raycast(TreeRayCastCallback callback, RayCastInput input) {
        final Vec2 p1 = input.p1;
        final Vec2 p2 = input.p2;
        float p1x = p1.x, p2x = p2.x, p1y = p1.y, p2y = p2.y;
        float vx, vy;
        float rx, ry;
        float absVx, absVy;
        float cx, cy;
        float hx, hy;
        float tempx, tempy;
        r.x = p2x - p1x;
        r.y = p2y - p1y;
        assert (r.x * r.x + r.y * r.y) > 0f;
        r.getLengthAndNormalize();
        rx = r.x;
        ry = r.y;

        // v is perpendicular to the segment.
        vx = -1f * ry;
        vy = 1f * rx;
        absVx = FXGLMath.abs(vx);
        absVy = FXGLMath.abs(vy);

        // Separating axis for segment (Gino, p80).
        // |dot(v, p1 - c)| > dot(|v|, h)

        float maxFraction = input.maxFraction;

        // Build a bounding box for the segment.
        final AABB segAABB = aabb;
        // Vec2 t = p1 + maxFraction * (p2 - p1);
        // before inline
        // temp.set(p2).subLocal(p1).mulLocal(maxFraction).addLocal(p1);
        // Vec2.minToOut(p1, temp, segAABB.lowerBound);
        // Vec2.maxToOut(p1, temp, segAABB.upperBound);
        tempx = (p2x - p1x) * maxFraction + p1x;
        tempy = (p2y - p1y) * maxFraction + p1y;
        segAABB.lowerBound.x = p1x < tempx ? p1x : tempx;
        segAABB.lowerBound.y = p1y < tempy ? p1y : tempy;
        segAABB.upperBound.x = p1x > tempx ? p1x : tempx;
        segAABB.upperBound.y = p1y > tempy ? p1y : tempy;
        // end inline

        nodeStackIndex = 0;
        nodeStack[nodeStackIndex++] = root;
        while (nodeStackIndex > 0) {
            final DynamicTreeNode node = nodeStack[--nodeStackIndex];
            if (node == null) {
                continue;
            }

            final AABB nodeAABB = node.aabb;
            if (!AABB.testOverlap(nodeAABB, segAABB)) {
                continue;
            }

            // Separating axis for segment (Gino, p80).
            // |dot(v, p1 - c)| > dot(|v|, h)
            // node.aabb.getCenterToOut(c);
            // node.aabb.getExtentsToOut(h);
            cx = (nodeAABB.lowerBound.x + nodeAABB.upperBound.x) * .5f;
            cy = (nodeAABB.lowerBound.y + nodeAABB.upperBound.y) * .5f;
            hx = (nodeAABB.upperBound.x - nodeAABB.lowerBound.x) * .5f;
            hy = (nodeAABB.upperBound.y - nodeAABB.lowerBound.y) * .5f;
            tempx = p1x - cx;
            tempy = p1y - cy;
            float separation = FXGLMath.abs(vx * tempx + vy * tempy) - (absVx * hx + absVy * hy);
            if (separation > 0.0f) {
                continue;
            }

            if (node.child1 == null) {
                subInput.p1.x = p1x;
                subInput.p1.y = p1y;
                subInput.p2.x = p2x;
                subInput.p2.y = p2y;
                subInput.maxFraction = maxFraction;

                float value = callback.raycastCallback(subInput, node.id);

                if (value == 0.0f) {
                    // The client has terminated the ray cast.
                    return;
                }

                if (value > 0.0f) {
                    // Update segment bounding box.
                    maxFraction = value;
                    // temp.set(p2).subLocal(p1).mulLocal(maxFraction).addLocal(p1);
                    // Vec2.minToOut(p1, temp, segAABB.lowerBound);
                    // Vec2.maxToOut(p1, temp, segAABB.upperBound);
                    tempx = (p2x - p1x) * maxFraction + p1x;
                    tempy = (p2y - p1y) * maxFraction + p1y;
                    segAABB.lowerBound.x = p1x < tempx ? p1x : tempx;
                    segAABB.lowerBound.y = p1y < tempy ? p1y : tempy;
                    segAABB.upperBound.x = p1x > tempx ? p1x : tempx;
                    segAABB.upperBound.y = p1y > tempy ? p1y : tempy;
                }
            } else {
                if (nodeStack.length - nodeStackIndex - 2 <= 0) {
                    DynamicTreeNode[] newBuffer = new DynamicTreeNode[nodeStack.length * 2];
                    System.arraycopy(nodeStack, 0, newBuffer, 0, nodeStack.length);
                    nodeStack = newBuffer;
                }
                nodeStack[nodeStackIndex++] = node.child1;
                nodeStack[nodeStackIndex++] = node.child2;
            }
        }
    }

    private DynamicTreeNode allocateNode() {
        if (m_freeList == NULL_NODE) {
            DynamicTreeNode[] old = m_nodes;
            m_nodeCapacity *= 2;
            m_nodes = new DynamicTreeNode[m_nodeCapacity];
            System.arraycopy(old, 0, m_nodes, 0, old.length);

            // Build a linked list for the free list.
            for (int i = m_nodeCapacity - 1; i >= m_nodeCount; i--) {
                m_nodes[i] = new DynamicTreeNode(i);
                m_nodes[i].parent = (i == m_nodeCapacity - 1) ? null : m_nodes[i + 1];
                m_nodes[i].height = -1;
            }
            m_freeList = m_nodeCount;
        }
        int nodeId = m_freeList;
        final DynamicTreeNode treeNode = m_nodes[nodeId];
        m_freeList = treeNode.parent != null ? treeNode.parent.id : NULL_NODE;

        treeNode.parent = null;
        treeNode.child1 = null;
        treeNode.child2 = null;
        treeNode.height = 0;
        treeNode.userData = null;
        ++m_nodeCount;
        return treeNode;
    }

    /**
     * returns a node to the pool
     */
    private void freeNode(DynamicTreeNode node) {
        node.parent = m_freeList != NULL_NODE ? m_nodes[m_freeList] : null;
        node.height = -1;
        m_freeList = node.id;
        m_nodeCount--;
    }

    private final AABB combinedAABB = new AABB();

    private void insertLeaf(int leaf_index) {
        DynamicTreeNode leaf = m_nodes[leaf_index];
        if (root == null) {
            root = leaf;
            root.parent = null;
            return;
        }

        // find the best sibling
        AABB leafAABB = leaf.aabb;
        DynamicTreeNode index = root;
        while (index.child1 != null) {
            final DynamicTreeNode node = index;
            DynamicTreeNode child1 = node.child1;
            DynamicTreeNode child2 = node.child2;

            float area = node.aabb.getPerimeter();

            combinedAABB.combine(node.aabb, leafAABB);
            float combinedArea = combinedAABB.getPerimeter();

            // Cost of creating a new parent for this node and the new leaf
            float cost = 2.0f * combinedArea;

            // Minimum cost of pushing the leaf further down the tree
            float inheritanceCost = 2.0f * (combinedArea - area);

            // Cost of descending into child1
            float cost1;
            if (child1.child1 == null) {
                combinedAABB.combine(leafAABB, child1.aabb);
                cost1 = combinedAABB.getPerimeter() + inheritanceCost;
            } else {
                combinedAABB.combine(leafAABB, child1.aabb);
                float oldArea = child1.aabb.getPerimeter();
                float newArea = combinedAABB.getPerimeter();
                cost1 = (newArea - oldArea) + inheritanceCost;
            }

            // Cost of descending into child2
            float cost2;
            if (child2.child1 == null) {
                combinedAABB.combine(leafAABB, child2.aabb);
                cost2 = combinedAABB.getPerimeter() + inheritanceCost;
            } else {
                combinedAABB.combine(leafAABB, child2.aabb);
                float oldArea = child2.aabb.getPerimeter();
                float newArea = combinedAABB.getPerimeter();
                cost2 = newArea - oldArea + inheritanceCost;
            }

            // Descend according to the minimum cost.
            if (cost < cost1 && cost < cost2) {
                break;
            }

            // Descend
            if (cost1 < cost2) {
                index = child1;
            } else {
                index = child2;
            }
        }

        DynamicTreeNode sibling = index;
        DynamicTreeNode oldParent = m_nodes[sibling.id].parent;
        final DynamicTreeNode newParent = allocateNode();
        newParent.parent = oldParent;
        newParent.userData = null;
        newParent.aabb.combine(leafAABB, sibling.aabb);
        newParent.height = sibling.height + 1;

        if (oldParent != null) {
            // The sibling was not the root.
            if (oldParent.child1 == sibling) {
                oldParent.child1 = newParent;
            } else {
                oldParent.child2 = newParent;
            }

            newParent.child1 = sibling;
            newParent.child2 = leaf;
            sibling.parent = newParent;
            leaf.parent = newParent;
        } else {
            // The sibling was the root.
            newParent.child1 = sibling;
            newParent.child2 = leaf;
            sibling.parent = newParent;
            leaf.parent = newParent;
            root = newParent;
        }

        // Walk back up the tree fixing heights and AABBs
        index = leaf.parent;
        while (index != null) {
            index = balance(index);

            DynamicTreeNode child1 = index.child1;
            DynamicTreeNode child2 = index.child2;

            assert child1 != null;
            assert child2 != null;

            index.height = 1 + Math.max(child1.height, child2.height);
            index.aabb.combine(child1.aabb, child2.aabb);

            index = index.parent;
        }
    }

    private void removeLeaf(DynamicTreeNode leaf) {
        if (leaf == root) {
            root = null;
            return;
        }

        DynamicTreeNode parent = leaf.parent;
        DynamicTreeNode grandParent = parent.parent;
        DynamicTreeNode sibling;
        if (parent.child1 == leaf) {
            sibling = parent.child2;
        } else {
            sibling = parent.child1;
        }

        if (grandParent != null) {
            // Destroy parent and connect sibling to grandParent.
            if (grandParent.child1 == parent) {
                grandParent.child1 = sibling;
            } else {
                grandParent.child2 = sibling;
            }
            sibling.parent = grandParent;
            freeNode(parent);

            // Adjust ancestor bounds.
            DynamicTreeNode index = grandParent;
            while (index != null) {
                index = balance(index);

                DynamicTreeNode child1 = index.child1;
                DynamicTreeNode child2 = index.child2;

                index.aabb.combine(child1.aabb, child2.aabb);
                index.height = 1 + Math.max(child1.height, child2.height);

                index = index.parent;
            }
        } else {
            root = sibling;
            sibling.parent = null;
            freeNode(parent);
        }
    }

    // Perform a left or right rotation if node A is imbalanced.
    // Returns the new root index.
    private DynamicTreeNode balance(DynamicTreeNode iA) {
        DynamicTreeNode A = iA;
        if (A.child1 == null || A.height < 2) {
            return iA;
        }

        DynamicTreeNode iB = A.child1;
        DynamicTreeNode iC = A.child2;
        assert 0 <= iB.id && iB.id < m_nodeCapacity;
        assert 0 <= iC.id && iC.id < m_nodeCapacity;

        DynamicTreeNode B = iB;
        DynamicTreeNode C = iC;

        int balance = C.height - B.height;

        // Rotate C up
        if (balance > 1) {
            DynamicTreeNode iF = C.child1;
            DynamicTreeNode iG = C.child2;
            DynamicTreeNode F = iF;
            DynamicTreeNode G = iG;
            assert F != null;
            assert G != null;
            assert 0 <= iF.id && iF.id < m_nodeCapacity;
            assert 0 <= iG.id && iG.id < m_nodeCapacity;

            // Swap A and C
            C.child1 = iA;
            C.parent = A.parent;
            A.parent = iC;

            // A's old parent should point to C
            if (C.parent != null) {
                if (C.parent.child1 == iA) {
                    C.parent.child1 = iC;
                } else {
                    assert C.parent.child2 == iA;
                    C.parent.child2 = iC;
                }
            } else {
                root = iC;
            }

            // Rotate
            if (F.height > G.height) {
                C.child2 = iF;
                A.child2 = iG;
                G.parent = iA;
                A.aabb.combine(B.aabb, G.aabb);
                C.aabb.combine(A.aabb, F.aabb);

                A.height = 1 + Math.max(B.height, G.height);
                C.height = 1 + Math.max(A.height, F.height);
            } else {
                C.child2 = iG;
                A.child2 = iF;
                F.parent = iA;
                A.aabb.combine(B.aabb, F.aabb);
                C.aabb.combine(A.aabb, G.aabb);

                A.height = 1 + Math.max(B.height, F.height);
                C.height = 1 + Math.max(A.height, G.height);
            }

            return iC;
        }

        // Rotate B up
        if (balance < -1) {
            DynamicTreeNode iD = B.child1;
            DynamicTreeNode iE = B.child2;
            DynamicTreeNode D = iD;
            DynamicTreeNode E = iE;
            assert 0 <= iD.id && iD.id < m_nodeCapacity;
            assert 0 <= iE.id && iE.id < m_nodeCapacity;

            // Swap A and B
            B.child1 = iA;
            B.parent = A.parent;
            A.parent = iB;

            // A's old parent should point to B
            if (B.parent != null) {
                if (B.parent.child1 == iA) {
                    B.parent.child1 = iB;
                } else {
                    assert B.parent.child2 == iA;
                    B.parent.child2 = iB;
                }
            } else {
                root = iB;
            }

            // Rotate
            if (D.height > E.height) {
                B.child2 = iD;
                A.child1 = iE;
                E.parent = iA;
                A.aabb.combine(C.aabb, E.aabb);
                B.aabb.combine(A.aabb, D.aabb);

                A.height = 1 + Math.max(C.height, E.height);
                B.height = 1 + Math.max(A.height, D.height);
            } else {
                B.child2 = iE;
                A.child1 = iD;
                D.parent = iA;
                A.aabb.combine(C.aabb, D.aabb);
                B.aabb.combine(A.aabb, E.aabb);

                A.height = 1 + Math.max(C.height, D.height);
                B.height = 1 + Math.max(A.height, E.height);
            }

            return iB;
        }

        return iA;
    }
}
