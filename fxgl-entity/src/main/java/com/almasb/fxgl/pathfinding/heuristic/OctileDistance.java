/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.pathfinding.heuristic;

import com.almasb.fxgl.core.collection.grid.Cell;

import static java.lang.Math.*;

/**
 * See https://theory.stanford.edu/~amitp/GameProgramming/Heuristics.html#diagonal-distance
 * for definition.
 *
 * @author Jean-René Lavoie (jeanrlavoie@gmail.com)
 */
public final class OctileDistance<T extends Cell> extends DiagonalHeuristic<T> {

    private final int diagonalFactor;

    public OctileDistance() {
        this(DEFAULT_WEIGHT);
    }

    public OctileDistance(int weight) {
        super(weight, (int)(sqrt(2) * weight));
        diagonalFactor = getDiagonalWeight() - weight;
    }

    @Override
    public int getCost(int startX, int startY, int targetX, int targetY) {
        int dx = abs(startX - targetX);
        int dy = abs(startY - targetY);

        // D * max(dx, dy) + (D2-D) * min(dx, dy), where
        // D - 4-directional weight
        // D2 - diagonal weight
        return getWeight() * max(dx, dy) + diagonalFactor * min(dx, dy);
    }
}
