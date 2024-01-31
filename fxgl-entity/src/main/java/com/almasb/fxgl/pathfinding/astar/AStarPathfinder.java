/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.pathfinding.astar;

import com.almasb.fxgl.pathfinding.CellState;
import com.almasb.fxgl.pathfinding.Pathfinder;

import java.util.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public final class AStarPathfinder implements Pathfinder<AStarCell> {

    private final AStarGrid grid;

    private boolean isCachingPaths = false;
    private Map<CacheKey, List<AStarCell>> cache = new HashMap<>();

    public AStarPathfinder(AStarGrid grid) {
        this.grid = grid;
    }

    public AStarGrid getGrid() {
        return grid;
    }

    /**
     * If set to true, computed paths for same start and end cells are cached.
     * Default is false.
     */
    public void setCachingPaths(boolean isCachingPaths) {
        this.isCachingPaths = isCachingPaths;
    }

    public boolean isCachingPaths() {
        return isCachingPaths;
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

        var cacheKey = new CacheKey(start.getX(), start.getY(), target.getX(), target.getY());

        if (isCachingPaths) {
            var path = cache.get(cacheKey);

            if (path != null) {
                return new ArrayList<>(path);
            }
        }

        // reset grid cells data
        for (int y = 0; y < grid[0].length; y++) {
            for (int x = 0; x < grid.length; x++) {
                grid[x][y].setHCost(Math.abs(target.getX() - x) + Math.abs(target.getY() - y));
                grid[x][y].setParent(null);
                grid[x][y].setGCost(0);
            }
        }

        Set<AStarCell> open = new HashSet<>();
        Set<AStarCell> closed = new HashSet<>();

        AStarCell current = start;

        boolean found = false;

        while (!found && !closed.contains(target)) {
            for (AStarCell neighbor : getValidNeighbors(current, busyNodes)) {
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

                AStarCell acc = null;

                for (AStarCell a : open) {
                    if (acc == null) {
                        acc = a;
                        continue;
                    }

                    acc = a.getFCost() < acc.getFCost() ? a : acc;
                }

                current = acc;
            }
        }

        var path = buildPath(start, target);

        if (isCachingPaths) {
            cache.put(cacheKey, path);
        }

        return new ArrayList<>(path);
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
     * @param busyNodes nodes which are busy, i.e. walkable but have a temporary obstacle
     * @return neighbors of the node
     */
    private List<AStarCell> getValidNeighbors(AStarCell node, AStarCell... busyNodes) {
        var result = grid.getNeighbors(node.getX(), node.getY());
        result.removeAll(Arrays.asList(busyNodes));
        result.removeIf(cell -> !cell.isWalkable());
        return result;
    }

}
