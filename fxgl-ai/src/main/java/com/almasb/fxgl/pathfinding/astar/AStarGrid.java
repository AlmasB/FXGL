/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.pathfinding.astar;

import com.almasb.fxgl.pathfinding.CellState;
import com.almasb.fxgl.pathfinding.Grid;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class AStarGrid extends Grid<AStarCell> {

    /**
     * Constructs A* grid with A* cells using given width and height.
     * All cells are initially {@link CellState#WALKABLE}.
     */
    public AStarGrid(int width, int height) {
        super(AStarCell.class, width, height, (x, y) -> new AStarCell(x, y, CellState.WALKABLE));
    }

    public List<AStarCell> getWalkableCells() {
        return getCells()
                .stream()
                .filter(c -> c.getState().isWalkable())
                .collect(Collectors.toList());
    }
}
