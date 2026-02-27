/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.pathfinding.heuristic;

import com.almasb.fxgl.core.collection.grid.Cell;

import static java.lang.Math.*;

/**
 * See https://en.wikipedia.org/wiki/Taxicab_geometry for definition.
 *
 * @author Jean-René Lavoie (jeanrlavoie@gmail.com)
 */
public final class ManhattanDistance<T extends Cell> extends Heuristic<T> {

    public ManhattanDistance() {
        super();
    }

    public ManhattanDistance(int weight) {
        super(weight);
    }

    @Override
    public int getCost(int startX, int startY, int targetX, int targetY) {
        return (abs(targetX - startX) + abs(targetY - startY)) * getWeight();
    }
}
