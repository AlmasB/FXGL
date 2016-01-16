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


/**
 * API INCOMPLETE
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public final class MazeSolver {
//
//    public List<OldMazeCell> getPath(OldMazeCell[][] grid, OldMazeCell start, OldMazeCell target) {
//        if (target.getState() == NodeState.NOT_WALKABLE) // the target is an unwalkable node
//            return new ArrayList<>();  // return empty path
//
//        List<OldMazeCell> open = new ArrayList<>();
//        List<OldMazeCell> closed = new ArrayList<>();
//        List<OldMazeCell> path = new ArrayList<>();
//
//        OldMazeCell current = start;
//
//        boolean found = false;
//
//        while (!found && !closed.contains(target)) {
//            OldMazeCell[] temp = getNeighbors(current, grid);
//
//            for (int i = 0; i < temp.length; i++) {
//                if (temp[i] == target) {
//                    target.setParent(current);
//                    found = true;
//                    closed.add(target);
//                    break;
//                }
//
//                if (!closed.contains(temp[i])) {
//                    if (open.contains(temp[i])) {
//                        int newG = current.getGCost() + 10;
//
//                        if (newG < temp[i].getGCost()) {
//                            temp[i].setParent(current);
//                            temp[i].setGCost(newG);
//                        }
//                    } else {
//                        temp[i].setParent(current);
//                        temp[i].setGCost(current.getGCost() + 10);
//                        open.add(temp[i]);
//                    }
//                }
//            }
//
//            if (!found) {
//                closed.add(current);
//                open.remove(current);
//                current = getSmallest(open);
//                if (current == null)
//                    return new ArrayList<>();
//            }
//        }
//
//        OldMazeCell tmp = target;
//        path.add(tmp);
//        while (!path.contains(start)) {
//            tmp = (OldMazeCell) tmp.getParent();
//            if (tmp == start)
//                break;
//            path.add(tmp);
//        }
//
//        closed.clear();
//
//        for (int i = path.size() - 1; i >= 0; i--)
//            closed.add(path.get(i));
//
//        return closed;
//    }
//
//    public OldMazeCell getSmallest(List<OldMazeCell> open) {
//        if (open.size() == 0) {
//            System.out.println("No path found. Returning null");
//            return null;
//        }
//
//        OldMazeCell min = open.get(0);
//
//        for (OldMazeCell n : open)
//            if (n.getFCost() < min.getFCost())
//                min = n;
//
//        return min;
//    }
//
//    /**
//     * Adaptation from vanilla A* to cater for walls
//     *
//     * @param n
//     * @param grid
//     * @return
//     */
//    public OldMazeCell[] getNeighbors(OldMazeCell n, OldMazeCell[][] grid) {
//        int x = n.getX();
//        int y = n.getY();
//        int x1 = x - 1;
//        int x2 = x + 1;
//        int y1 = y - 1;
//        int y2 = y + 1;
//
//        boolean b1 = x1 >= 0 && !grid[x][y].leftWall;
//        boolean b2 = x2 < grid.length && !grid[x2][y].leftWall;
//        boolean b3 = y1 >= 0 && !grid[x][y].topWall;
//        boolean b4 = y2 < grid[0].length && !grid[x][y2].topWall;
//
//        int count = 0;
//        if (b1)
//            count++;
//        if (b2)
//            count++;
//        if (b3)
//            count++;
//        if (b4)
//            count++;
//
//        OldMazeCell[] res = new OldMazeCell[count];
//        int i = 0;
//
//        if (b1)
//            res[i++] = grid[x1][y];
//
//        if (b2)
//            res[i++] = grid[x2][y];
//
//        if (b3)
//            res[i++] = grid[x][y1];
//
//        if (b4)
//            res[i++] = grid[x][y2];
//
//        return res;
//    }
}
