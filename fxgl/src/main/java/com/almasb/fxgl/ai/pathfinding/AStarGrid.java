/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ai.pathfinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * A* grid containing A* nodes.
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
        if (width < 1 || height < 1)
            throw new IllegalArgumentException("width and height cannot < 1");

        grid = new AStarNode[width][height];
        for (int y = 0; y < grid[0].length; y++) {
            for (int x = 0; x < grid.length; x++) {
                grid[x][y] = new AStarNode(x, y, NodeState.WALKABLE);
            }
        }
    }

    /**
     * @return grid width
     */
    public final int getWidth() {
        return grid.length;
    }

    /**
     * @return grid height
     */
    public final int getHeight() {
        return grid[0].length;
    }

    /**
     *
     * @param x x coord
     * @param y y coord
     * @return true IFF the point is within the grid
     */
    public final boolean isWithin(int x, int y) {
        return x >= 0 && x < getWidth()
                && y >= 0 && y < getHeight();
    }

    /**
     * Convenience method to set state of all nodes to given state.
     *
     * @param state node state
     */
    public final void setStateForAllNodes(NodeState state) {
        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                getNode(x, y).setState(state);
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
    public final void setNodeState(int x, int y, NodeState state) {
        getNode(x, y).setState(state);
    }

    /**
     * Returns state of the node at a, y.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return  the state
     */
    public final NodeState getNodeState(int x, int y) {
        return getNode(x, y).getState();
    }

    /**
     * Returns a list of A* nodes from start to target.
     * The list will include target.
     * Return an empty list if the path doesn't exist.
     *
     * @param startX start node x
     * @param startY start node y
     * @param targetX target node x
     * @param targetY target node y
     * @return the path
     */
    public final List<AStarNode> getPath(int startX, int startY, int targetX, int targetY) {
        return logic.getPath(grid, getNode(startX, startY), getNode(targetX, targetY));
    }

    /**
     * Returns a node at x, y. There is no bounds checking.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return A* node at x, y
     */
    public final AStarNode getNode(int x, int y) {
        return grid[x][y];
    }

    /**
     * @return a random node from the grid
     */
    public final AStarNode getRandomNode() {
        int x = (int) (Math.random() * getWidth());
        int y = (int) (Math.random() * getHeight());

        return getNode(x, y);
    }

    /**
     * @param predicate filter condition
     * @return a random node that passes the filter or {@link Optional#empty()}
     * if no such node exists
     */
    public final Optional<AStarNode> getRandomNode(Predicate<AStarNode> predicate) {
        List<AStarNode> filtered = getNodes().stream()
                .filter(predicate)
                .collect(Collectors.toList());

        if (filtered.isEmpty())
            return Optional.empty();

        int index = (int) (Math.random() * filtered.size());

        return Optional.of(filtered.get(index));
    }

    /**
     * @return underlying grid of nodes
     */
    public final AStarNode[][] getGrid() {
        return grid;
    }

    /**
     * @return all grid nodes
     */
    public final List<AStarNode> getNodes() {
        List<AStarNode> nodes = new ArrayList<>();

        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                nodes.add(getNode(x, y));
            }
        }

        return nodes;
    }
}
