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
public class OctileDistance<T extends Cell> extends Heuristic<T> {

    private static final int DIAGONAL_WEIGHT = (int)(Math.sqrt(2) * 10.0);
    private static final int DIAGONAL_FACTOR = DIAGONAL_WEIGHT - 10;

    public OctileDistance() {
        super(DIAGONAL_WEIGHT);
    }

    public OctileDistance(int weight) {
        super(weight);
    }

    @Override
    public int getCost(int x, int y, T target) {
        int dx = Math.abs(x - target.getX());
        int dy = Math.abs(y - target.getY());

        if(dx == dy) {
            return (dx + dy) * 10;
        }
        if(dx < dy) {
            return DIAGONAL_FACTOR * dx + 10 * dy;
        }
        return DIAGONAL_FACTOR * dy + 10 * dx;
    }

}
