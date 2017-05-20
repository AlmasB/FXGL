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

package com.almasb.fxgl.ai.pathfinding.maze;

import com.almasb.fxgl.ai.pathfinding.AStarLogic;
import com.almasb.fxgl.ai.pathfinding.AStarNode;

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
