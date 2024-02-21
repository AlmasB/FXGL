/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.pathfinding.heuristic;

import com.almasb.fxgl.core.collection.grid.Cell;

/**
 * Describes a heuristic function h(n), where n is the next cell.
 *
 * @author Jean-René Lavoie (jeanrlavoie@gmail.com)
 */
public abstract class Heuristic<T extends Cell> {

    protected static final int DEFAULT_WEIGHT = 10;

    private final int weight;

    public Heuristic() {
        this(DEFAULT_WEIGHT);
    }

    public Heuristic(int weight) {
        this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }

    /**
     * @return estimated weighted cost from start to target
     */
    public int getCost(T start, T target) {
        return getCost(start.getX(), start.getY(), target.getX(), target.getY());
    }

    /**
     * @return estimated weighted cost from start to target
     */
    public abstract int getCost(int startX, int startY, int targetX, int targetY);
}
