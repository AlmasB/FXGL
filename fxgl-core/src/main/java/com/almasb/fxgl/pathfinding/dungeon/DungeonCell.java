/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.pathfinding.dungeon;

import com.almasb.fxgl.pathfinding.CellState;
import com.almasb.fxgl.pathfinding.astar.AStarCell;

public class DungeonCell extends AStarCell {
    public DungeonCell(int x, int y) {
        super(x, y, CellState.NOT_WALKABLE);
    }
}