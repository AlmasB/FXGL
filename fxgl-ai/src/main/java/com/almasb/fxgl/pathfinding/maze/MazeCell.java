/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.pathfinding.maze;

import com.almasb.fxgl.pathfinding.Cell;

/**
 * Represents a single cell in a maze.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class MazeCell extends Cell {

    private boolean topWall = false;
    private boolean leftWall = false;

    public MazeCell(int x, int y) {
        super(x, y);
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
