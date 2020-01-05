/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.pathfinding.astar;

import com.almasb.fxgl.pathfinding.CellState;
import com.almasb.fxgl.pathfinding.Pathfinder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public final class AStarPathfinder implements Pathfinder<AStarCell> {

    private AStarGrid grid;

    public AStarPathfinder(AStarGrid grid) {
        this.grid = grid;
    }

    public AStarGrid getGrid() {
        return grid;
    }

    @Override
    public List<AStarCell> findPath(int sourceX, int sourceY, int targetX, int targetY) {
        return findPath(grid.getData(), grid.get(sourceX, sourceY), grid.get(targetX, targetY));
    }

    @Override
    public List<AStarCell> findPath(int sourceX, int sourceY, int targetX, int targetY, List<AStarCell> busyCells) {
        return findPath(grid.getData(), grid.get(sourceX, sourceY), grid.get(targetX, targetY), busyCells.toArray(new AStarCell[0]));
    }

    /**
     * Since the equality check is based on references,
     * start and target must be elements of the array.
     *
     * @param grid      the grid of nodes
     * @param start     starting node
     * @param target    target node
     * @param busyNodes busy "unwalkable" nodes
     * @return          path as list of nodes from start (excl) to target (incl) or empty list if no path found
     */
    public List<AStarCell> findPath(AStarCell[][] grid, AStarCell start, AStarCell target, AStarCell... busyNodes) {
        if (start == target || target.getState() == CellState.NOT_WALKABLE)
            return Collections.emptyList();

        // reset grid cells data
        for (int y = 0; y < grid[0].length; y++) {
            for (int x = 0; x < grid.length; x++) {
                grid[x][y].setHCost(Math.abs(target.getX() - x) + Math.abs(target.getY() - y));
                grid[x][y].setParent(null);
                grid[x][y].setGCost(0);
            }
        }

        List<AStarCell> open = new ArrayList<>();
        List<AStarCell> closed = new ArrayList<>();

        AStarCell current = start;

        boolean found = false;

        while (!found && !closed.contains(target)) {
            for (AStarCell neighbor : getValidNeighbors(current, grid, busyNodes)) {
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

                AStarCell acc = open.get(0);

                for (AStarCell a : open) {
                    acc = a.getFCost() < acc.getFCost() ? a : acc;
                }

                current = acc;
            }
        }

        return buildPath(start, target);
    }

    private List<AStarCell> buildPath(AStarCell start, AStarCell target) {
        List<AStarCell> path = new ArrayList<>();

        AStarCell tmp = target;
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
    protected List<AStarCell> getValidNeighbors(AStarCell node, AStarCell[][] grid, AStarCell... busyNodes) {
        // TODO: reuse grid.getNeighbors()
        int x = node.getX();
        int y = node.getY();
        int[] points = {
                x - 1, y,
                x + 1, y,
                x, y - 1,
                x, y + 1
        };

        List<AStarCell> result = new ArrayList<>();

        for (int i = 0; i < points.length; i++) {
            int x1 = points[i];
            int y1 = points[++i];

            if (x1 >= 0 && x1 < grid.length
                    && y1 >= 0 && y1 < grid[0].length
                    && grid[x1][y1].getState() == CellState.WALKABLE
                    && !contains(x1, y1, busyNodes)) {
                result.add(grid[x1][y1]);
            }
        }

        return result;
    }

    private boolean contains(int x, int y, AStarCell... cells) {
        for (AStarCell n : cells)
            if (n.getX() == x && n.getY() == y)
                return true;

        return false;
    }
}
