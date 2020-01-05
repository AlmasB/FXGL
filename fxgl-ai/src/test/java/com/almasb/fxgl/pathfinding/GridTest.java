/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.pathfinding;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

public class GridTest {

    private static final int GRID_SIZE = 20;
    private Grid<MockCell> grid;

    @BeforeEach
    public void setUp() {
        grid = new MockGrid(GRID_SIZE, GRID_SIZE);
    }

    @Test
    public void testConstructor() {
        grid = new Grid<>(MockCell.class, 5, 5);
        grid.getCells().forEach(Assertions::assertNull);
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

        assertFalse(grid.isWithin(-1, 0));
        assertFalse(grid.isWithin(0, -1));
    }

    @Test
    public void testGetNeighbors() {
        assertThat(grid.getNeighbors(0, 0), containsInAnyOrder(grid.get(0, 1), grid.get(1, 0)));
        assertThat(grid.getNeighbors(GRID_SIZE - 1, 0), containsInAnyOrder(grid.get(GRID_SIZE - 1, 1), grid.get(GRID_SIZE - 2, 0)));
        assertThat(grid.getNeighbors(GRID_SIZE - 1, GRID_SIZE - 1), containsInAnyOrder(grid.get(GRID_SIZE - 1, GRID_SIZE - 2), grid.get(GRID_SIZE - 2, GRID_SIZE - 1)));
        assertThat(grid.getNeighbors(0, GRID_SIZE - 1), containsInAnyOrder(grid.get(0, GRID_SIZE - 2), grid.get(1, GRID_SIZE - 1)));

        assertThat(grid.getNeighbors(3, 3), containsInAnyOrder(grid.get(2, 3), grid.get(4, 3), grid.get(3, 2), grid.get(3, 4)));

        assertThat(grid.getNeighbors(3, 0), containsInAnyOrder(grid.get(3, 1), grid.get(2, 0), grid.get(4, 0)));
        assertThat(grid.getNeighbors(3, GRID_SIZE - 1), containsInAnyOrder(grid.get(3, GRID_SIZE - 2), grid.get(2, GRID_SIZE - 1), grid.get(4, GRID_SIZE - 1)));
    }

    @Test
    public void testGetRandomCell() {
        for (int i = 0; i < 50; i++) {
            assertNotNull(grid.getRandomCell());
        }
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