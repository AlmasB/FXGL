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

/**
 * Represents a single cell in a maze.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class MazeCell {
    private int x, y;
    private boolean topWall = false, leftWall = false;

    public MazeCell(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * @return x coordinate of this cell in the grid
     */
    public int getX() {
        return x;
    }

    /**
     * @return y coordinate of this cell in the grid
     */
    public int getY() {
        return y;
    }

    /**
     * @param leftWall left wall for this cell
     */
    public void setLeftWall(boolean leftWall) {
        this.leftWall = leftWall;
    }

    /**
     * @param topWall top wall for this cell
     */
    public void setTopWall(boolean topWall) {
        this.topWall = topWall;
    }

    /**
     * @return if left wall is present
     */
    public boolean hasLeftWall() {
        return leftWall;
    }

    /**
     * @return if top wall is present
     */
    public boolean hasTopWall() {
        return topWall;
    }
}
