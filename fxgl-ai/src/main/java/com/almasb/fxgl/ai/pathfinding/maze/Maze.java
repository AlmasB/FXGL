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

import com.almasb.fxgl.ai.pathfinding.AStarGrid;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A 2d maze.
 *
 * Slightly modified and adapted version from
 * <a href="http://rosettacode.org/wiki/Maze_generation#Java">Rosetta Code</a>.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class Maze {
    private final int width;
    private final int height;
    private final int[][] maze;
    private MazeCell[][] mazeCells;

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public MazeCell[][] getMaze() {
        return mazeCells;
    }

    public MazeCell getMazeCell(int x, int y) {
        return mazeCells[x][y];
    }

    /**
     * Constructs a new maze with given width and height.
     *
     * @param width maze width
     * @param height maze height
     */
    public Maze(int width, int height) {
        this.width = width;
        this.height = height;
        maze = new int[width][height];
        mazeCells = new MazeCell[width][height];
        generateMaze(0, 0);

        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                MazeCell cell = new MazeCell(j, i);
                if ((maze[j][i] & 1) == 0) cell.setTopWall(true);
                if ((maze[j][i] & 8) == 0) cell.setLeftWall(true);

                mazeCells[j][i] = cell;
            }
        }
    }

    /**
     * Returns shortest path between two cells in the maze.
     * If no path found, empty list is returned
     *
     * @param startX x of start maze cell
     * @param startY y of start maze cell
     * @param targetX x of target maze cell
     * @param targetY y of target maze cell
     * @return shortest path
     */
    public List<MazeCell> getPath(int startX, int startY, int targetX, int targetY) {
        AStarGrid grid = new AStarGrid(width, height);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                grid.getNode(x, y).setUserData(getMazeCell(x, y));
            }
        }

        return new MazeSolver().getPath(grid.getGrid(), grid.getNode(startX, startY), grid.getNode(targetX, targetY))
                .stream()
                .map(node -> (MazeCell) node.getUserData())
                .collect(Collectors.toList());
    }

    private void generateMaze(int cx, int cy) {
        DIR[] dirs = DIR.values();
        Collections.shuffle(Arrays.asList(dirs));
        for (DIR dir : dirs) {
            int nx = cx + dir.dx;
            int ny = cy + dir.dy;
            if (between(nx, width) && between(ny, height) && (maze[nx][ny] == 0)) {
                maze[cx][cy] |= dir.bit;
                maze[nx][ny] |= dir.opposite.bit;
                generateMaze(nx, ny);
            }
        }
    }

    private static boolean between(int v, int upper) {
        return (v >= 0) && (v < upper);
    }

    private enum DIR {
        N(1, 0, -1), S(2, 0, 1), E(4, 1, 0), W(8, -1, 0);
        private final int bit;
        private final int dx;
        private final int dy;
        private DIR opposite;

        static {
            N.opposite = S;
            S.opposite = N;
            E.opposite = W;
            W.opposite = E;
        }

        DIR(int bit, int dx, int dy) {
            this.bit = bit;
            this.dx = dx;
            this.dy = dy;
        }
    }
}
