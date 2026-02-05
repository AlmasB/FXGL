/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.pathfinding.astar;

import com.almasb.fxgl.core.collection.grid.Cell;
import com.almasb.fxgl.core.collection.grid.NeighborDirection;
import static com.almasb.fxgl.core.collection.grid.NeighborDirection.*;
import com.almasb.fxgl.pathfinding.CellState;
import com.almasb.fxgl.pathfinding.Pathfinder;
import com.almasb.fxgl.pathfinding.TraversableGrid;
import com.almasb.fxgl.pathfinding.heuristic.DiagonalHeuristic;
import com.almasb.fxgl.pathfinding.heuristic.Heuristic;
import com.almasb.fxgl.pathfinding.heuristic.ManhattanDistance;
import com.almasb.fxgl.pathfinding.heuristic.OctileDistance;

import java.util.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public final class AStarPathfinder<T extends AStarCell> extends Pathfinder<T> {

    private final Heuristic<T> defaultHeuristic;
    private final DiagonalHeuristic<T> diagonalHeuristic;

    private boolean isCachingPaths = false;
    private Map<CacheKey, List<T>> cache = new HashMap<>();

    public AStarPathfinder(TraversableGrid<T> grid) {
        this(grid, new ManhattanDistance<>(), new OctileDistance<>());
    }

    public AStarPathfinder(TraversableGrid<T> grid, Heuristic<T> defaultHeuristic, DiagonalHeuristic<T> diagonalHeuristic) {
        super(grid);
        this.defaultHeuristic = defaultHeuristic;
        this.diagonalHeuristic = diagonalHeuristic;
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
    public List<T> findPath(int sourceX, int sourceY, int targetX, int targetY, NeighborDirection neighborDirection, List<T> busyCells) {
        return findPath(getGrid().getData(), getGrid().get(sourceX, sourceY), getGrid().get(targetX, targetY), neighborDirection, busyCells.toArray(new AStarCell[0]));
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
    public List<T> findPath(T[][] grid, T start, T target, T... busyNodes) {
        return findPath(grid, start, target, FOUR_DIRECTIONS, busyNodes);
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
    public List<T> findPath(T[][] grid, T start, T target, NeighborDirection neighborDirection, AStarCell... busyNodes) {
        if (start == target || target.getState() == CellState.NOT_WALKABLE)
            return Collections.emptyList();

        var cacheKey = new CacheKey(start.getX(), start.getY(), target.getX(), target.getY());

        if (isCachingPaths) {
            var path = cache.get(cacheKey);

            if (path != null) {
                return new ArrayList<>(path);
            }
        }

        Heuristic<T> heuristic = (neighborDirection == FOUR_DIRECTIONS) ? defaultHeuristic : diagonalHeuristic;

        // reset grid cells data
        for (int y = 0; y < grid[0].length; y++) {
            for (int x = 0; x < grid.length; x++) {
                grid[x][y].setHCost(heuristic.getCost(x, y, target.getX(), target.getY()));
                grid[x][y].setParent(null);
                grid[x][y].setGCost(0);
            }
        }

        Set<T> open = new HashSet<>();
        Set<T> closed = new HashSet<>();

        T current = start;

        boolean found = false;

        while (!found && !closed.contains(target)) {
            for (T neighbor : getValidNeighbors(current, neighborDirection, busyNodes)) {
                getCellVisitListener().onVisit(neighbor);

                if (neighbor == target) {
                    target.setParent(current);
                    found = true;
                    closed.add(target);
                    break;
                }

                if (!closed.contains(neighbor)) {
                    int gCost = isDiagonal(current, neighbor)
                            ? diagonalHeuristic.getDiagonalWeight()
                            : defaultHeuristic.getWeight();

                    gCost *= neighbor.getMovementCost();

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

                T acc = null;

                for (T a : open) {
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

        var result = new ArrayList<>(path);

        getPathFoundListener().onPathFound(result);

        return result;
    }

    private List<T> buildPath(T start, T target) {
        List<T> path = new ArrayList<>();

        T tmp = target;
        do {
            path.add(tmp);
            tmp = (T) tmp.getParent();
        } while (tmp != start);

        Collections.reverse(path);
        return path;
    }

    /**
     * @param node the A* node
     * @param busyNodes nodes which are busy, i.e. walkable but have a temporary obstacle
     * @return neighbors of the node
     */
    private List<T> getValidNeighbors(T node, NeighborDirection neighborDirection, AStarCell... busyNodes) {
        var result = new ArrayList<>(getGrid().getNeighbors(node.getX(), node.getY(), neighborDirection));
        result.removeAll(Arrays.asList(busyNodes));
        result.removeIf(cell -> !getGrid().isTraversableInSingleMove(node, cell));
        return result;
    }

    private boolean isDiagonal(Cell current, Cell neighbor) {
        return neighbor.getX() - current.getX() != 0 && neighbor.getY() - current.getY() != 0;
    }
}
