/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.pathfinding;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GridTest {

    private static final int GRID_SIZE = 20;
    private Grid<MockCell> grid;

    @BeforeEach
    public void setUp() {
        grid = new MockGrid(GRID_SIZE, GRID_SIZE);
    }

    @Test
    public void testIsWithin() {
        for (int y = 0; y < GRID_SIZE; y++) {
            for (int x = 0; x < GRID_SIZE; x++) {
                assertTrue(grid.isWithin(x, y));
            }
        }

        assertFalse(grid.isWithin(GRID_SIZE, 0));
        assertFalse(grid.isWithin(0, GRID_SIZE));
    }

    @Test
    public void testRandomCell() {
        for (int i = 0; i < 50; i++) {
            Optional<MockCell> c = grid.getRandomCell(cell -> cell.getX() < 3 && cell.getY() > 15);
            assertTrue(c.isPresent());

            MockCell cell = c.get();

            assertThat(cell.getX(), allOf(greaterThanOrEqualTo(0), lessThan(3)));
            assertThat(cell.getY(), allOf(greaterThanOrEqualTo(16), lessThan(20)));
        }
    }

    public static class MockCell extends Cell {

        public MockCell(int x, int y) {
            super(x, y);
        }
    }

    public static class MockGrid extends Grid<MockCell> {

        public MockGrid(int width, int height) {
            super(MockCell.class, width, height, (x, y) -> new MockCell(x, y));
        }
    }
}