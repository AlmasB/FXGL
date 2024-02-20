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
public abstract class Heuristic<T extends Cell> {

    public static final int DEFAULT_WEIGHT = 10;

    private final int weight;

    public Heuristic() {
        this(DEFAULT_WEIGHT);
    }

    public Heuristic(int weight) {
        this.weight = weight;
    }

   public abstract int getCost(int x, int y, T target);

    public int getWeight() {
        return weight;
    }

}
