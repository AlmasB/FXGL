/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.pathfinding;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public enum CellState {
    WALKABLE, NOT_WALKABLE;

    public boolean isWalkable() {
        return this == WALKABLE;
    }

    public boolean isNotWalkable() {
        return this == NOT_WALKABLE;
    }
}
