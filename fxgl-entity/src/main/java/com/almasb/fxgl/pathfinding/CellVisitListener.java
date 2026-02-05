/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.pathfinding;

import com.almasb.fxgl.core.collection.grid.Cell;

/**
 * @author Almas Baim (https://github.com/AlmasB)
 */
public interface CellVisitListener<T extends Cell> {

    /**
     * This is called when a [cell] is being visited (evaluated) by
     * a pathfinding algorithm.
     */
    void onVisit(T cell);
}
