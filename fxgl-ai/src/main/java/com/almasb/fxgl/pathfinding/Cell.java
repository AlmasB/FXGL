/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.pathfinding;

import static java.lang.Math.abs;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public abstract class Cell {

    private int x;
    private int y;

    private Object userData = null;

    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public final int getX() {
        return x;
    }

    public final int getY() {
        return y;
    }

    /**
     * Set user specific data.
     */
    public final void setUserData(Object userData) {
        this.userData = userData;
    }

    /**
     * @return user specific data
     */
    public final Object getUserData() {
        return userData;
    }

    /**
     * Note: only horizontal and vertical movements are used to compute,
     * i.e. Manhattan distance (no diagonal movement).
     * Distance from 0,0 to 2,2 is therefore 4.
     *
     * @return distance in number of cells from this cell to other
     */
    public int distance(Cell other) {
        return abs(getX() - other.getX()) + abs(getY() - other.getY());
    }

    @Override
    public String toString() {
        return "Cell(" + x + "," + y + ")";
    }
}
