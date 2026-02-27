/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.pathfinding;

import com.almasb.fxgl.core.collection.grid.CellGenerator;
import com.almasb.fxgl.core.collection.grid.Grid;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The supertype for any grid that can be traversed using some pathfinding algorithm.
 * It can be extended by domain-specific grids, whose cells extend TraversableCell.
 *
 * @author Almas Baim (https://github.com/AlmasB)
 */
public abstract class TraversableGrid<T extends TraversableCell> extends Grid<T> {

    public TraversableGrid(Class<T> type, int width, int height) {
        super(type, width, height);
    }

    public TraversableGrid(Class<T> type, int width, int height, CellGenerator<T> populateFunction) {
        super(type, width, height, populateFunction);
    }

    public TraversableGrid(Class<T> type, int width, int height, int cellWidth, int cellHeight, CellGenerator<T> populateFunction) {
        super(type, width, height, cellWidth, cellHeight, populateFunction);
    }

    /**
     * @return all cells whose state is CellState.WALKABLE
     */
    public List<T> getWalkableCells() {
        return getCells()
                .stream()
                .filter(c -> c.getState().isWalkable())
                .collect(Collectors.toList());
    }

    /**
     * @return given neighbors [source] and [target], true if we can move from [source] to [target] in a single action,
     * i.e. there exists a path of size 1
     */
    public boolean isTraversableInSingleMove(T source, T target) {
        return target.isWalkable();
    }
}
