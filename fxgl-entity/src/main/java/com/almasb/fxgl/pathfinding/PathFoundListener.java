/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.pathfinding;

import com.almasb.fxgl.core.collection.grid.Cell;

import java.util.List;

/**
 * @author Almas Baim (https://github.com/AlmasB)
 */
public interface PathFoundListener<T extends Cell> {

    /**
     * This is called after a valid path has been found.
     */
    void onPathFound(List<T> path);
}
