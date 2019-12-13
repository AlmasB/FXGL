/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.pathfinding.astar;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class AStarGridTest {

    private static final int GRID_SIZE = 20;
    private AStarGrid grid;

    @BeforeEach
    public void setUp() {
        grid = new AStarGrid(GRID_SIZE, GRID_SIZE);
    }

    @Test
    public void testValidity() {
        assertThrows(IllegalArgumentException.class, () -> {
            new AStarGrid(-1, 5);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new AStarGrid(5, -1);
        });

        for (int y = 0; y < GRID_SIZE; y++) {
            for (int x = 0; x < GRID_SIZE; x++) {
                assertTrue(grid.get(x, y).getState().isWalkable());
            }
        }
    }

    @Test
    public void testGetWalkableCells() {
        var cells = grid.getWalkableCells();
        assertThat(cells.size(), is(grid.getWidth() * grid.getHeight()));

        for (AStarCell cell : cells) {
            assertTrue(cell.getState().isWalkable());
        }
    }
}