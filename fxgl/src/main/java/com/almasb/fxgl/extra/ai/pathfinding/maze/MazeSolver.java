/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.extra.ai.pathfinding.maze;

import com.almasb.fxgl.extra.ai.pathfinding.AStarLogic;
import com.almasb.fxgl.extra.ai.pathfinding.AStarNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Solves a Maze using A*.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public final class MazeSolver extends AStarLogic {

    /**
     * Adaptation from vanilla A* to cater for walls.
     */
    @Override
    protected List<AStarNode> getValidNeighbors(AStarNode node, AStarNode[][] grid, AStarNode... busyNodes) {
        int x = node.getX();
        int y = node.getY();
        int x1 = x - 1;
        int x2 = x + 1;
        int y1 = y - 1;
        int y2 = y + 1;

        boolean b1 = x1 >= 0 && !((MazeCell)grid[x][y].getUserData()).hasLeftWall();
        boolean b2 = x2 < grid.length && !((MazeCell)grid[x2][y].getUserData()).hasLeftWall();
        boolean b3 = y1 >= 0 && !((MazeCell)grid[x][y].getUserData()).hasTopWall();
        boolean b4 = y2 < grid[0].length && !((MazeCell)grid[x][y2].getUserData()).hasTopWall();

        List<AStarNode> result = new ArrayList<>();

        if (b1)
            result.add(grid[x1][y]);
        if (b2)
            result.add(grid[x2][y]);
        if (b3)
            result.add(grid[x][y1]);
        if (b4)
            result.add(grid[x][y2]);

        return result;
    }
}
