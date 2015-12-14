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
 * API INCOMPLETE
 * <p>
 * Modified and adapted for general use version of
 * recursive backtracking algorithm
 * shamelessly borrowed from the ruby at
 * http://weblog.jamisbuck.org/2010/12/27/maze-generation-recursive-backtracking
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public final class MazeGenerator {

//    public void solve() {
//        List<OldMazeCell> path = new MazeSolver().getPath(theMaze, theMaze[0][0], theMaze[x - 1][y - 1]);
//        for (OldMazeCell cell : path) {
//            cell.value = 9;
//            display();
//
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    public void slowDisplay() {
//        for (int i = 0; i < y; i++) {
//
//            // draw the north edge
//            for (int j = 0; j < x; j++) {
//                System.out.print(theMaze[j][i].topWall ? "+---" : "+   ");
//            }
//            System.out.println("+");
//
//            // draw the west edge
//            for (int j = 0; j < x; j++) {
//                System.out.print(theMaze[j][i].leftWall ? "| " : "  ");
//                System.out.print(theMaze[j][i].value + " ");
//            }
//            System.out.println("|");
//
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//
//        // draw the bottom line
//        for (int j = 0; j < x; j++) {
//            System.out.print("+---");
//        }
//        System.out.println("+");
//    }
//
//    public void display() {
//        for (int i = 0; i < y; i++) {
//
//            // draw the north edge
//            for (int j = 0; j < x; j++) {
//                System.out.print(theMaze[j][i].topWall ? "+---" : "+   ");
//            }
//            System.out.println("+");
//
//            // draw the west edge
//            for (int j = 0; j < x; j++) {
//                System.out.print(theMaze[j][i].leftWall ? "| " : "  ");
//                System.out.print(theMaze[j][i].value + " ");
//            }
//            System.out.println("|");
//        }
//
//        // draw the bottom line
//        for (int j = 0; j < x; j++) {
//            System.out.print("+---");
//        }
//        System.out.println("+");
//
//
//
//
//        /*for (int i = 0; i < y; i++) {
//            // draw the north edge
//            for (int j = 0; j < x; j++) {
//                System.out.print((maze[j][i] & 1) == 0 ? "+---" : "+   ");
//            }
//            System.out.println("+");
//            // draw the west edge
//            for (int j = 0; j < x; j++) {
//                System.out.print((maze[j][i] & 8) == 0 ? "|   " : "    ");
//            }
//            System.out.println("|");
//        }
//        // draw the bottom line
//        for (int j = 0; j < x; j++) {
//            System.out.print("+---");
//        }
//        System.out.println("+");
//
//
//        for (int i = 0; i < y; ++i) {
//            for (int j = 0; j < x; ++j) {
//                System.out.print(maze[j][i] + ",");
//            }
//            System.out.println();
//        }*/
//    }
//
//
//
//    public static class OldMazeCell extends AStarNode {
//        public OldMazeCell(int x, int y) {
//            super(x, y, NodeState.WALKABLE);
//        }
//
//        public boolean topWall = false, leftWall = false;
//        public int value = 0;
//    }
}
