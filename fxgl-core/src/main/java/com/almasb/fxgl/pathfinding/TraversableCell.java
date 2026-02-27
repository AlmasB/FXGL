/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.pathfinding;

import com.almasb.fxgl.core.collection.grid.Cell;

/**
 * Describes a cell that has a "walkable" or "not walkable" state.
 * It also keeps information about its parent, which is the last cell that was used
 * to visit this cell.
 * Search algorithms can use it to rebuild some path, such as a path between two cells.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class TraversableCell extends Cell {

    private TraversableCell parent;

    private CellState state;

    public TraversableCell(int x, int y) {
        this(x, y, CellState.WALKABLE);
    }

    public TraversableCell(int x, int y, CellState state) {
        super(x, y);
        this.state = state;
    }

    public final void setParent(TraversableCell parent) {
        this.parent = parent;
    }

    public final TraversableCell getParent() {
        return parent;
    }

    public final void setState(CellState state) {
        this.state = state;
    }

    public final CellState getState() {
        return state;
    }

    public final boolean isWalkable() {
        return state.isWalkable();
    }

    @Override
    public String toString() {
        return "TraversableCell[x=" + getX() + ",y=" + getY() + "," + state + "]";
    }
}
