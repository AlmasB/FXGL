/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.extra.ai.pathfinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A* search logic.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class AStarLogic {

    /**
     * Since the equality check is based on references,
     * start and target must be elements of the array.
     *
     * @param grid      the grid of nodes
     * @param start     starting node
     * @param target    target node
     * @param busyNodes busy "unwalkable" nodes
     * @return          path as list of nodes from start to target or empty list if no path found
     */
    public final List<AStarNode> getPath(AStarNode[][] grid, AStarNode start, AStarNode target, AStarNode... busyNodes) {
        if (target.getState() == NodeState.NOT_WALKABLE)
            return Collections.emptyList();

        for (int y = 0; y < grid[0].length; y++) {
            for (int x = 0; x < grid.length; x++) {
                grid[x][y].setHCost(Math.abs(target.getX() - x) + Math.abs(target.getY() - y));
            }
        }

        List<AStarNode> open = new ArrayList<>();
        List<AStarNode> closed = new ArrayList<>();

        AStarNode current = start;

        boolean found = false;

        while (!found && !closed.contains(target)) {
            for (AStarNode neighbor : getValidNeighbors(current, grid, busyNodes)) {
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

                if (open.isEmpty())
                    return Collections.emptyList();

                current = open.stream()
                        .reduce((n1, n2) -> n2.getFCost() < n1.getFCost() ? n2 : n1)
                        .get();
            }
        }

        return buildPath(start, target);
    }

    private List<AStarNode> buildPath(AStarNode start, AStarNode target) {
        List<AStarNode> path = new ArrayList<>();

        AStarNode tmp = target;
        do {
            path.add(tmp);
            tmp = tmp.getParent();
        } while (tmp != start);

        Collections.reverse(path);
        return path;
    }

    /**
     * @param node the A* node
     * @param grid the A* grid
     * @param busyNodes nodes which are busy, i.e. walkable but have a temporary obstacle
     * @return neighbors of the node
     */
    protected List<AStarNode> getValidNeighbors(AStarNode node, AStarNode[][] grid, AStarNode... busyNodes) {
        int x = node.getX();
        int y = node.getY();
        int[] points = {
            x - 1, y,
            x + 1, y,
            x, y - 1,
            x, y + 1
        };

        List<AStarNode> result = new ArrayList<>();

        for (int i = 0; i < points.length; i++) {
            int x1 = points[i];
            int y1 = points[++i];

            if (x1 >= 0 && x1 < grid.length
                    && y1 >= 0 && y1 < grid[0].length
                    && grid[x1][y1].getState() == NodeState.WALKABLE
                    && !contains(x1, y1, busyNodes)) {
                result.add(grid[x1][y1]);
            }
        }

        return result;
    }

    private boolean contains(int x, int y, AStarNode... nodes) {
        for (AStarNode n : nodes)
            if (n.getX() == x && n.getY() == y)
                return true;

        return false;
    }
}
