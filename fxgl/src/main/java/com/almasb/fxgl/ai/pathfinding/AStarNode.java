/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ai.pathfinding;

/**
 * Generic A* node.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class AStarNode {

    /**
     * Parent node of this node.
     */
    private AStarNode parent;

    /**
     * State of this node.
     */
    private NodeState state;

    /**
     * Location of the node in the grid.
     */
    private int x, y;

    /**
     * G and H costs.
     */
    private int gCost, hCost;

    private Object userData = null;

    /**
     * Constructs A* node with x, y values and state.
     *
     * @param x x value
     * @param y y value
     * @param state initial state
     */
    public AStarNode(int x, int y, NodeState state) {
        this.x = x;
        this.y = y;
        this.state = state;
    }

    /**
     * Set user specific data.
     *
     * @param userData data
     */
    public final void setUserData(Object userData) {
        this.userData = userData;
    }

    /**
     * @return user specific data
     */
    public final Object getUserData() {
        return userData;
    }

    /**
     * Set node's parent.
     *
     * @param parent parent node
     */
    public final void setParent(AStarNode parent) {
        this.parent = parent;
    }

    /**
     * @return node parent
     */
    public final AStarNode getParent() {
        return parent;
    }

    /**
     * Set H cost.
     *
     * @param hCost H cost
     */
    public final void setHCost(int hCost) {
        this.hCost = hCost;
    }

    /**
     * @return H cost
     */
    public final int getHCost() {
        return hCost;
    }

    /**
     * Set G cost.
     *
     * @param gCost G cost
     */
    final void setGCost(int gCost) {
        this.gCost = gCost;
    }

    /**
     * @return G cost
     */
    public final int getGCost() {
        return gCost;
    }

    /**
     * @return X coordinate in the grid
     */
    public final int getX() {
        return x;
    }

    /**
     * @return y coorinate in the grid
     */
    public final int getY() {
        return y;
    }

    /**
     * Set node's state.
     *
     * @param state the state
     */
    public final void setState(NodeState state) {
        this.state = state;
    }

    /**
     * @return node's state
     */
    public final NodeState getState() {
        return state;
    }

    /**
     * @return F cost (G + H)
     */
    public final int getFCost() {
        return gCost + hCost;
    }

    @Override
    public String toString() {
        return "A* Node[x=" + x + ",y=" + y + "," + state + "]";
    }
}
