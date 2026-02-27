/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.pathfinding;

import com.almasb.fxgl.core.collection.grid.NeighborDirection;

import java.util.Collections;
import java.util.List;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public abstract class Pathfinder<T extends TraversableCell> {

    private TraversableGrid<T> grid;
    private CellVisitListener<T> visitListener = _ -> {};
    private PathFoundListener<T> pathListener = _ -> {};

    public Pathfinder(TraversableGrid<T> grid) {
        this.grid = grid;
    }

    public final TraversableGrid<T> getGrid() {
        return grid;
    }

    public final CellVisitListener<T> getCellVisitListener() {
        return visitListener;
    }

    public final void setCellVisitListener(CellVisitListener<T> visitListener) {
        this.visitListener = visitListener;
    }

    public final PathFoundListener<T> getPathFoundListener() {
        return pathListener;
    }

    public final void setPathFoundListener(PathFoundListener<T> pathListener) {
        this.pathListener = pathListener;
    }

    /**
     * Empty list is returned if no path exists.
     *
     * @return a list of cells from source (excl.) to target (incl.)
     */
    public List<T> findPath(int sourceX, int sourceY, int targetX, int targetY) {
        return findPath(sourceX, sourceY, targetX, targetY, Collections.emptyList());
    }

    /**
     * Empty list is returned if no path exists.
     *
     * @return a list of cells from source (excl.) to target (incl.)
     */
    public List<T> findPath(int sourceX, int sourceY, int targetX, int targetY, NeighborDirection neighborDirection) {
        return findPath(sourceX, sourceY, targetX, targetY, neighborDirection, Collections.emptyList());
    }

    /**
     * Empty list is returned if no path exists.
     *
     * @return a list of cells from source (excl.) to target (incl.) while ignoring busyCells
     */
    public List<T> findPath(int sourceX, int sourceY, int targetX, int targetY, List<T> busyCells) {
        return findPath(sourceX, sourceY, targetX, targetY, NeighborDirection.FOUR_DIRECTIONS, busyCells);
    }

    /**
     * Empty list is returned if no path exists.
     *
     * @return a list of cells from source (excl.) to target (incl.) while ignoring busyCells
     */
    public abstract List<T> findPath(int sourceX, int sourceY, int targetX, int targetY, NeighborDirection neighborDirection, List<T> busyCells);
}
