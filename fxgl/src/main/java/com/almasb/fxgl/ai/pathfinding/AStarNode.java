/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
