/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.pathfinding.maze;

import com.almasb.fxgl.pathfinding.Grid;

import java.util.Arrays;
import java.util.Collections;

/**
 * A 2d maze.
 *
 * Slightly modified and adapted version from
 * <a href="http://rosettacode.org/wiki/Maze_generation#Java">Rosetta Code</a>.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class Maze extends Grid<MazeCell> {

    /**
     * Constructs a new maze with given width and height.
     *
     * @param width maze width
     * @param height maze height
     */
    public Maze(int width, int height) {
        super(MazeCell.class, width, height);

        int[][] maze = new int[width][height];
        generateMaze(maze, 0, 0);

        populate((x, y) -> {
            MazeCell cell = new MazeCell(x, y);
            if ((maze[x][y] & 1) == 0)
                cell.setTopWall(true);

            if ((maze[x][y] & 8) == 0)
                cell.setLeftWall(true);

            return cell;
        });
    }

    @SuppressWarnings("PMD.UselessParentheses")
    private void generateMaze(int[][] maze, int cx, int cy) {
        DIR[] dirs = DIR.values();
        Collections.shuffle(Arrays.asList(dirs));
        for (DIR dir : dirs) {
            int nx = cx + dir.dx;
            int ny = cy + dir.dy;
            if (between(nx, getWidth()) && between(ny, getHeight()) && (maze[nx][ny] == 0)) {
                maze[cx][cy] |= dir.bit;
                maze[nx][ny] |= dir.opposite.bit;
                generateMaze(maze, nx, ny);
            }
        }
    }

    @SuppressWarnings("PMD.UselessParentheses")
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
