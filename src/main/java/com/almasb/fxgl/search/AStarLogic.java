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
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
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

import com.almasb.fxgl.util.FXGLLogger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * A* search logic.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public final class AStarLogic {

    private static final Logger log = FXGLLogger.getLogger("FXGL.AStarLogic");

    /**
     * Since the equality check is based on references
     * start and target must be elements of the array.
     *
     * @param grid      the grid of nodes
     * @param start     starting node
     * @param target    target node
     * @param busyNodes busy "unwalkable" nodes
     * @return          path as list of nodes from start to target or empty list if no path found
     */
    public List<AStarNode> getPath(AStarNode[][] grid, AStarNode start, AStarNode target, AStarNode... busyNodes) {
        if (target.getState() == NodeState.NOT_WALKABLE)
            return new ArrayList<>();

        for (int y = 0; y < grid[0].length; y++) {
            for (int x = 0; x < grid.length; x++) {
                grid[x][y].setHCost(Math.abs(target.getX() - x) + Math.abs(target.getY() - y));
            }
        }

        List<AStarNode> open = new ArrayList<>();
        List<AStarNode> closed = new ArrayList<>();
        List<AStarNode> path = new ArrayList<>();

        AStarNode current = start;

        boolean found = false;

        while (!found && !closed.contains(target)) {
            AStarNode[] neighbors = getNeighbors(current, grid, busyNodes);

            for (AStarNode neighbor : neighbors) {
                if (neighbor == target) {
                    target.setParent(current);
                    found = true;
                    closed.add(target);
                    break;
                }

                if (!closed.contains(neighbor)) {
                    if (open.contains(neighbor)) {
                        int newG = current.getGCost() + 10;

                        if (newG < neighbor.getGCost()) {
                            neighbor.setParent(current);
                            neighbor.setGCost(newG);
                        }
                    } else {
                        neighbor.setParent(current);
                        neighbor.setGCost(current.getGCost() + 10);
                        open.add(neighbor);
                    }
                }
            }

            if (!found) {
                closed.add(current);
                open.remove(current);
                current = getSmallest(open);
                if (current == null)
                    return new ArrayList<>();
            }
        }

        AStarNode tmp = target;
        path.add(tmp);
        while (!path.contains(start)) {
            tmp = tmp.getParent();
            if (tmp == start)
                break;
            path.add(tmp);
        }

        Collections.reverse(path);
        return path;
    }

    private AStarNode getSmallest(List<AStarNode> open) {
        if (open.isEmpty()) {
            log.warning("No path found");
            return null;
        }

        AStarNode min = open.get(0);

        for (AStarNode n : open)
            if (n.getFCost() < min.getFCost())
                min = n;

        return min;
    }

    private AStarNode[] getNeighbors(AStarNode n, AStarNode[][] grid, AStarNode... busyNodes) {
        int x = n.getX();
        int y = n.getY();
        int x1 = x - 1;
        int x2 = x + 1;
        int y1 = y - 1;
        int y2 = y + 1;

        boolean b1 = x1 >= 0 && grid[x1][y].getState() == NodeState.WALKABLE && !contains(x1, y, busyNodes);
        boolean b2 = x2 < grid.length && grid[x2][y].getState() == NodeState.WALKABLE && !contains(x2, y, busyNodes);
        boolean b3 = y1 >= 0 && grid[x][y1].getState() == NodeState.WALKABLE && !contains(x, y1, busyNodes);
        boolean b4 = y2 < grid[0].length && grid[x][y2].getState() == NodeState.WALKABLE && !contains(x, y2, busyNodes);

        int count = 0;
        if (b1)
            count++;
        if (b2)
            count++;
        if (b3)
            count++;
        if (b4)
            count++;

        AStarNode[] res = new AStarNode[count];
        int i = 0;

        if (b1)
            res[i++] = grid[x1][y];

        if (b2)
            res[i++] = grid[x2][y];

        if (b3)
            res[i++] = grid[x][y1];

        if (b4)
            res[i++] = grid[x][y2];

        return res;
    }

    private boolean contains(int x, int y, AStarNode... nodes) {
        for (AStarNode n : nodes)
            if (n.getX() == x && n.getY() == y)
                return true;

        return false;
    }
}
