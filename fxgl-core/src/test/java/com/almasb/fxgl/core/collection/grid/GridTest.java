/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core.collection.grid;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Random;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

public class GridTest {

    private static final int GRID_SIZE = 20;
    private static final int CELL_WIDTH = 10;
    private static final int CELL_HEIGHT = 15;

    private Grid<MockCell> grid;

    @BeforeEach
    public void setUp() {
        grid = new MockGrid(GRID_SIZE, GRID_SIZE);
    }

    @Test
    public void testConstructor() {
        grid = new Grid<>(MockCell.class, 5, 5);
        grid.getCells().forEach(Assertions::assertNull);
        assertEquals(0, grid.getCellWidth());
        assertEquals(0, grid.getCellHeight());
    }

    @Test
    public void testConstructorWithCellSize() {
        grid = new Grid<>(MockCell.class, 5, 5, CELL_WIDTH, CELL_HEIGHT, new MockCellGenerator());
        assertEquals(CELL_WIDTH, grid.getCellWidth());
        assertEquals(CELL_HEIGHT, grid.getCellHeight());
    }

    @Test
    public void testConstructorWithNegativeCellSize() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Grid<>(MockCell.class, 5, 5, -1, CELL_HEIGHT, new MockCellGenerator());
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new Grid<>(MockCell.class, 5, 5, CELL_WIDTH, -1, new MockCellGenerator());
        });
    }

    @Test
    public void testConstructorWithNegativeSize() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Grid<>(MockCell.class, -1, 5, CELL_WIDTH, CELL_HEIGHT, new MockCellGenerator());
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new Grid<>(MockCell.class, 5, -1, CELL_WIDTH, CELL_HEIGHT, new MockCellGenerator());
        });
    }

    @Test
    public void testGetData() {
        var data = grid.getData();

        assertThat(data.length, is(GRID_SIZE));
    }

    @Test
    public void testGetOptionalByPixels() {
        // div by 0
        grid = new MockGridWithCellSize(GRID_SIZE, GRID_SIZE, 0, CELL_HEIGHT);
        assertThat(grid.getOptionalByPixels(0, 0).isPresent(), is(false));

        // div by 0
        grid = new MockGridWithCellSize(GRID_SIZE, GRID_SIZE, CELL_WIDTH, 0);
        assertThat(grid.getOptionalByPixels(0, 0).isPresent(), is(false));

        // normal use case
        grid = new MockGridWithCellSize(GRID_SIZE, GRID_SIZE, CELL_WIDTH, CELL_HEIGHT);

        var cell = grid.getOptionalByPixels(0, 0).get();

        assertThat(cell.getX(), is(0));
        assertThat(cell.getY(), is(0));

        // x = 9, y = 14 is still cell 0,0 because of width and height above
        cell = grid.getOptionalByPixels(9, 14).get();

        assertThat(cell.getX(), is(0));
        assertThat(cell.getY(), is(0));

        cell = grid.getOptionalByPixels(25, 14).get();

        assertThat(cell.getX(), is(2));
        assertThat(cell.getY(), is(0));

        // 30 is 15 + 15 (between cells, but bias towards rounding up)
        cell = grid.getOptionalByPixels(25, 30).get();

        assertThat(cell.getX(), is(2));
        assertThat(cell.getY(), is(2));

        // outside
        assertThat(grid.getOptionalByPixels(201, 14).isPresent(), is(false));
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

        assertThat(grid.getRandomCell(new Random(), c -> false).isPresent(), is(false));
    }

    @Test
    public void testDirections() {

        // right cell

        Optional<MockCell> rightCell = grid.getRight(grid.get(1, 1));

        assertThat(rightCell.isPresent(), is(true));
        assertThat(rightCell.get().getX(), is(2));
        assertThat(rightCell.get().getY(), is(1));

        assertThat(grid.getRight(grid.get(GRID_SIZE-1, 0)).isPresent(), is(false));

        // left Cell

        Optional<MockCell> leftCell = grid.getLeft(grid.get(1, 1));

        assertThat(leftCell.isPresent(), is(true));
        assertThat(leftCell.get().getX(), is(0));
        assertThat(leftCell.get().getY(), is(1));

        assertThat(grid.getLeft(grid.get(0, 1)).isPresent(), is(false));

        // up Cell

        Optional<MockCell> upCell = grid.getUp(grid.get(1, 1));

        assertThat(upCell.isPresent(), is(true));
        assertThat(upCell.get().getX(), is(1));
        assertThat(upCell.get().getY(), is(0));

        assertThat(grid.getUp(grid.get(1,  0)).isPresent(), is(false));


        // down cell

        Optional<MockCell> downCell = grid.getDown(grid.get(1, 1));

        assertThat(downCell.isPresent(), is(true));
        assertThat(downCell.get().getX(), is(1));
        assertThat(downCell.get().getY(), is(2));

        assertThat(grid.getDown(grid.get(1,  GRID_SIZE - 1)).isPresent(), is(false));
    }

    public static class MockCell extends Cell {

        public MockCell(int x, int y) {
            super(x, y);
        }
    }

    public static class MockGridWithCellSize extends Grid<MockCell> {

        public MockGridWithCellSize(int width, int height, int cW, int cH) {
            super(MockCell.class, width, height, cW, cH, MockCell::new);
        }
    }

    public static class MockGrid extends Grid<MockCell> {

        public MockGrid(int width, int height) {
            super(MockCell.class, width, height, MockCell::new);
        }
    }

    public static class MockCellGenerator implements CellGenerator<MockCell> {

        @Override
        public MockCell apply(Integer x, Integer y) {
            return new MockCell(x,y);
        }
    }
}