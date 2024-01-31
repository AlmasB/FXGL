/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.pathfinding.heuristic;

import com.almasb.fxgl.core.collection.grid.Cell;

/**
 * @author Jean-René Lavoie (jeanrlavoie@gmail.com)
 */
public class ManhattanDistance<T extends Cell> extends Heuristic<T> {

    public ManhattanDistance() {
        super();
    }

    public ManhattanDistance(int weight) {
        super(weight);
    }

    @Override
    public int getCost(int x, int y, T target) {
        return (Math.abs(target.getX() - x) + Math.abs(target.getY() - y)) * getWeight();
    }

}
