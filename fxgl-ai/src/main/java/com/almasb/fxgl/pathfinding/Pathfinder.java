/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.pathfinding;

import java.util.List;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public interface Pathfinder<T extends Cell> {

    /**
     * Empty list is returned if no path exists.
     *
     * @return a list of cells from source (excl.) to target (incl.)
     */
    List<T> findPath(int sourceX, int sourceY, int targetX, int targetY);

    /**
     * Empty list is returned if no path exists.
     *
     * @return a list of cells from source (excl.) to target (incl.) while ignoring busyCells
     */
    List<T> findPath(int sourceX, int sourceY, int targetX, int targetY, List<T> busyCells);
}
