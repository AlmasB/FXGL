/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.pathfinding.astar;

import com.almasb.fxgl.pathfinding.Cell;
import com.almasb.fxgl.pathfinding.CellState;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class AStarCell extends Cell {

    private AStarCell parent;

    private CellState state;

    private int gCost;
    private int hCost;

    public AStarCell(int x, int y, CellState state) {
        super(x, y);
        this.state = state;
    }

    public final void setParent(AStarCell parent) {
        this.parent = parent;
    }

    public final AStarCell getParent() {
        return parent;
    }

    public final void setHCost(int hCost) {
        this.hCost = hCost;
    }

    public final int getHCost() {
        return hCost;
    }

    public final void setGCost(int gCost) {
        this.gCost = gCost;
    }

    public final int getGCost() {
        return gCost;
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

    /**
     * @return F cost (G + H)
     */
    public final int getFCost() {
        return gCost + hCost;
    }

    @Override
    public String toString() {
        return "A* Cell[x=" + getX() + ",y=" + getY() + "," + state + "]";
    }
}
