/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.pathfinding.heuristic;

import com.almasb.fxgl.core.collection.grid.Cell;

/**
 * @author Almas Baim (https://github.com/AlmasB)
 */
public abstract class DiagonalHeuristic<T extends Cell> extends Heuristic<T> {

    private final int diagonalWeight;

    public DiagonalHeuristic(int weight, int diagonalWeight) {
        super(weight);
        this.diagonalWeight = diagonalWeight;
    }

    public int getDiagonalWeight() {
        return diagonalWeight;
    }
}
