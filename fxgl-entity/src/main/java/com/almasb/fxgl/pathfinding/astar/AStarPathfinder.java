/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.pathfinding.astar;

import com.almasb.fxgl.core.collection.grid.Cell;
import com.almasb.fxgl.core.collection.grid.NeighborFilteringOption;
import static com.almasb.fxgl.core.collection.grid.NeighborFilteringOption.*;
import com.almasb.fxgl.pathfinding.CellState;
import com.almasb.fxgl.pathfinding.Pathfinder;
import com.almasb.fxgl.pathfinding.heuristic.Heuristic;
import com.almasb.fxgl.pathfinding.heuristic.ManhattanDistance;
import com.almasb.fxgl.pathfinding.heuristic.OctileDistance;

import java.util.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public final class AStarPathfinder implements Pathfinder<AStarCell> {

    private final AStarGrid grid;

    private final Heuristic<AStarCell> defaultHeuristic;
    private final Heuristic<AStarCell> diagonalHeuristic;

    private boolean isCachingPaths = false;
    private Map<CacheKey, List<AStarCell>> cache = new HashMap<>();

    public AStarPathfinder(AStarGrid grid) {
        this(grid, new ManhattanDistance<>(10), new OctileDistance<>());
    }

    public AStarPathfinder(AStarGrid grid, Heuristic<AStarCell> defaultHeuristic, Heuristic<AStarCell> diagonalHeuristic) {
        this.grid = grid;
        this.defaultHeuristic = defaultHeuristic;
        this.diagonalHeuristic = diagonalHeuristic;
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
    public List<AStarCell> findPath(int sourceX, int sourceY, int targetX, int targetY, NeighborFilteringOption neighborFilteringOption) {
        return findPath(grid.getData(), grid.get(sourceX, sourceY), grid.get(targetX, targetY), neighborFilteringOption);
    }

    @Override
    public List<AStarCell> findPath(int sourceX, int sourceY, int targetX, int targetY, List<AStarCell> busyCells) {
        return findPath(grid.getData(), grid.get(sourceX, sourceY), grid.get(targetX, targetY), busyCells.toArray(new AStarCell[0]));
    }

    @Override
    public List<AStarCell> findPath(int sourceX, int sourceY, int targetX, int targetY, NeighborFilteringOption neighborFilteringOption, List<AStarCell> busyCells) {
        return findPath(grid.getData(), grid.get(sourceX, sourceY), grid.get(targetX, targetY), neighborFilteringOption, busyCells.toArray(new AStarCell[0]));
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
        return findPath(grid, start, target, NeighborFilteringOption.FOUR_DIRECTIONS, busyNodes);
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
    public List<AStarCell> findPath(AStarCell[][] grid, AStarCell start, AStarCell target, NeighborFilteringOption neighborFilteringOption, AStarCell... busyNodes) {
        if (start == target || target.getState() == CellState.NOT_WALKABLE)
            return Collections.emptyList();

        var cacheKey = new CacheKey(start.getX(), start.getY(), target.getX(), target.getY());

        if (isCachingPaths) {
            var path = cache.get(cacheKey);

            if (path != null) {
                return new ArrayList<>(path);
            }
        }

        Heuristic<AStarCell> heuristic = (neighborFilteringOption.is(FOUR_DIRECTIONS)) ? defaultHeuristic : diagonalHeuristic;

        // reset grid cells data
        for (int y = 0; y < grid[0].length; y++) {
            for (int x = 0; x < grid.length; x++) {
                grid[x][y].setHCost(heuristic.getCost(x, y, target));
                grid[x][y].setParent(null);
                grid[x][y].setGCost(0);
            }
        }

        Set<AStarCell> open = new HashSet<>();
        Set<AStarCell> closed = new HashSet<>();

        AStarCell current = start;

        boolean found = false;

        while (!found && !closed.contains(target)) {
            for (AStarCell neighbor : getValidNeighbors(current, neighborFilteringOption, busyNodes)) {
                if (neighbor == target) {
                    target.setParent(current);
                    found = true;
                    closed.add(target);
                    break;
                }

                if (!closed.contains(neighbor)) {
                    int gCost = isDiagonal(current, neighbor) ? diagonalHeuristic.getWeight() : defaultHeuristic.getWeight();
                    int newGCost = current.getGCost() + gCost;

                    if (open.contains(neighbor)) {
                        if (newGCost < neighbor.getGCost()) {

                            neighbor.setParent(current);
                            neighbor.setGCost(newGCost);
                        }
                    } else {
                        neighbor.setParent(current);
                        neighbor.setGCost(newGCost);
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
    private List<AStarCell> getValidNeighbors(AStarCell node, NeighborFilteringOption neighborFilteringOption, AStarCell... busyNodes) {
        var result = grid.getNeighbors(node.getX(), node.getY(), neighborFilteringOption);
        result.removeAll(Arrays.asList(busyNodes));
        result.removeIf(cell -> !cell.isWalkable());
        return result;
    }

    private boolean isDiagonal(Cell current, Cell neighbor) {
        return neighbor.getX() - current.getX() != 0 && neighbor.getY() - current.getY() != 0;
    }

}
