/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.pathfinding;

import java.util.function.BiFunction;

/**
 * Given (x, y) generates a cell.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@FunctionalInterface
public interface CellGenerator<T extends Cell> extends BiFunction<Integer, Integer, T> {
}
