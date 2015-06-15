/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.almasb.fxgl.search;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Modified and adapted for general use version of
 * recursive backtracking algorithm
 * shamelessly borrowed from the ruby at
 * http://weblog.jamisbuck.org/2010/12/27/maze-generation-recursive-backtracking
 *
 * @author Almas Baimagambetov (ab607@uni.brighton.ac.uk)
 * @version 1.0
 */
public class MazeGenerator {
    private final int x;
    private final int y;
    private final int[][] maze;

    private MazeCell[][] theMaze;

    public MazeGenerator(int x, int y) {
        this.x = x;
        this.y = y;
        maze = new int[this.x][this.y];
        generateMaze(0, 0);
        adapt();
        //solve();
    }

    public MazeCell[][] getMaze() {
        return theMaze;
    }

    public void adapt() {
        theMaze = new MazeCell[x][y];

        for (int i = 0; i < y; ++i) {
            for (int j = 0; j < x; ++j) {
                MazeCell cell = new MazeCell(j, i, Math.abs(x-1-j) + Math.abs(y-1-i));
                if ((maze[j][i] & 1) == 0) cell.topWall = true;
                if ((maze[j][i] & 8) == 0) cell.leftWall = true;

                theMaze[j][i] = cell;
            }
        }
    }

    public void solve() {
        List<MazeCell> path = new MazeSolver().getPath(theMaze, theMaze[0][0], theMaze[x-1][y-1]);
        for (MazeCell cell : path) {
            cell.value = 9;
            display();

            try {
                Thread.sleep(1000);
            }
            catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void slowDisplay() {
        for (int i = 0; i < y; i++) {

            // draw the north edge
            for (int j = 0; j < x; j++) {
                System.out.print(theMaze[j][i].topWall ? "+---" : "+   ");
            }
            System.out.println("+");

            // draw the west edge
            for (int j = 0; j < x; j++) {
                System.out.print(theMaze[j][i].leftWall ? "| " : "  ");
                System.out.print(theMaze[j][i].value + " ");
            }
            System.out.println("|");

            try {
                Thread.sleep(1000);
            }
            catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        // draw the bottom line
        for (int j = 0; j < x; j++) {
            System.out.print("+---");
        }
        System.out.println("+");
    }

    public void display() {
        for (int i = 0; i < y; i++) {

            // draw the north edge
            for (int j = 0; j < x; j++) {
                System.out.print(theMaze[j][i].topWall ? "+---" : "+   ");
            }
            System.out.println("+");

            // draw the west edge
            for (int j = 0; j < x; j++) {
                System.out.print(theMaze[j][i].leftWall ? "| " : "  ");
                System.out.print(theMaze[j][i].value + " ");
            }
            System.out.println("|");
        }

        // draw the bottom line
        for (int j = 0; j < x; j++) {
            System.out.print("+---");
        }
        System.out.println("+");




        /*for (int i = 0; i < y; i++) {
            // draw the north edge
            for (int j = 0; j < x; j++) {
                System.out.print((maze[j][i] & 1) == 0 ? "+---" : "+   ");
            }
            System.out.println("+");
            // draw the west edge
            for (int j = 0; j < x; j++) {
                System.out.print((maze[j][i] & 8) == 0 ? "|   " : "    ");
            }
            System.out.println("|");
        }
        // draw the bottom line
        for (int j = 0; j < x; j++) {
            System.out.print("+---");
        }
        System.out.println("+");


        for (int i = 0; i < y; ++i) {
            for (int j = 0; j < x; ++j) {
                System.out.print(maze[j][i] + ",");
            }
            System.out.println();
        }*/
    }

    private void generateMaze(int cx, int cy) {
        DIR[] dirs = DIR.values();
        Collections.shuffle(Arrays.asList(dirs));
        for (DIR dir : dirs) {
            int nx = cx + dir.dx;
            int ny = cy + dir.dy;
            if (between(nx, x) && between(ny, y) && (maze[nx][ny] == 0)) {
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

        // use the static initializer to resolve forward references
        static {
            N.opposite = S;
            S.opposite = N;
            E.opposite = W;
            W.opposite = E;
        }

        private DIR(int bit, int dx, int dy) {
            this.bit = bit;
            this.dx = dx;
            this.dy = dy;
        }
    };

    public static class MazeCell extends AStarNode {
        public MazeCell(int x, int y, int hCost) {
            super(x, y, hCost, 0);
        }
        public boolean topWall = false, leftWall = false;
        public int value = 0;
    }
}
