/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core.collection.grid;

import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines how neighbors of a cell are selected in a grid.
 *
 * @author Almas Baim (https://github.com/AlmasB)
 */
public interface NeighborSelectionStrategy {

    NeighborSelectionStrategy LEFT_UP_RIGHT_DOWN = (x, y) -> {
        List<Point2D> result = new ArrayList<>();
        result.add(new Point2D(x - 1, y));
        result.add(new Point2D(x, y - 1));
        result.add(new Point2D(x + 1, y));
        result.add(new Point2D(x, y + 1));
        return result;
    };

    NeighborSelectionStrategy LEFT_UP_RIGHT_DOWN_UPLEFT_UPRIGHT_DOWNRIGHT_DOWNLEFT = (x, y) -> {
        List<Point2D> result = new ArrayList<>();
        result.add(new Point2D(x - 1, y));
        result.add(new Point2D(x, y - 1));
        result.add(new Point2D(x + 1, y));
        result.add(new Point2D(x, y + 1));
        result.add(new Point2D(x - 1, y - 1));
        result.add(new Point2D(x + 1, y - 1));
        result.add(new Point2D(x + 1, y + 1));
        result.add(new Point2D(x - 1, y + 1));
        return result;
    };

    /**
     * Given a grid coordinate of a cell, [x] and [y],
     * returns a list of neighboring coordinates as defined by the implementing strategy.
     * Each Point2D in the list must have its x and y set as integers.
     * It is the responsibility of the underlying grid to check whether
     * the returned coordinates are valid (i.e. within the grid).
     */
    List<Point2D> selectNeighborCoordinates(int x, int y);
}
