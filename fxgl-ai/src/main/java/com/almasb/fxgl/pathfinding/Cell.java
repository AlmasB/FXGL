/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.pathfinding;

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
}
