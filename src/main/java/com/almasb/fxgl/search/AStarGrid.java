/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015 AlmasB (almaslvl@gmail.com)
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

package com.almasb.fxgl.search;

import java.util.List;

/**
 * A grid containing A* nodes.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class AStarGrid {

    private AStarLogic logic = new AStarLogic();
    private AStarNode[][] grid;

    /**
     * Constructs A* grid with A* nodes with given width and height.
     * All nodes are initially {@link NodeState#WALKABLE}
     *
     * @param width grid width
     * @param height grid height
     */
    public AStarGrid(int width, int height) {
        grid = new AStarNode[width][height];
        for (int y = 0; y < grid[0].length; y++) {
            for (int x = 0; x < grid.length; x++) {
                grid[x][y] = new AStarNode(x, y, NodeState.WALKABLE);
            }

        }
    }

    /**
     * Set state of the node at x, y.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @param state the state
     */
    public void setNodeState(int x, int y, NodeState state) {
        getNode(x, y).setState(state);
    }

    /**
     * Returns state of the node at a, y.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return  the state
     */
    public NodeState getNodeState(int x, int y) {
        return getNode(x, y).getState();
    }

    /**
     * Returns a list of A* nodes from start to target.
     * Return an empty list if the path doesn't exist.
     *
     * @param startX start node x
     * @param startY start node y
     * @param targetX target node x
     * @param targetY target node y
     * @return the path
     */
    public List<AStarNode> getPath(int startX, int startY, int targetX, int targetY) {
        return logic.getPath(grid, getNode(startX, startY), getNode(targetX, targetY));
    }

    /**
     * Returns a node at x, y. There is no bounds checking.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return A* node at x, y
     */
    public AStarNode getNode(int x, int y) {
        return grid[x][y];
    }
}
