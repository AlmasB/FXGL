/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.pathfinding.astar;

import com.almasb.fxgl.pathfinding.CellState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AStarPathfinderTest {

    private static final int GRID_SIZE = 20;
    private AStarGrid grid;
    private AStarPathfinder pathfinder;

    @BeforeEach
    public void setUp() {
        grid = new AStarGrid(GRID_SIZE, GRID_SIZE);
        pathfinder = new AStarPathfinder(grid);
    }

    @Test
    public void testFindPath() {
        List<AStarCell> path = pathfinder.findPath(3, 0, 5, 0);
        assertPathEquals(path, 4, 0, 5, 0);

        // Add barriers.
        for (int i = 0; i <= 4; i++)
            grid.get(4, i).setState(CellState.NOT_WALKABLE);
        path = pathfinder.findPath(3, 0, 5, 0);
        assertPathEquals(path,
                3, 1,
                3, 2,
                3, 3,
                3, 4,
                3, 5,
                4, 5,
                5, 5,
                5, 4,
                5, 3,
                5, 2,
                5, 1,
                5, 0);

        // Make passing impossible.
        for (int i = 0; i <= 19; i++)
            grid.get(4, i).setState(CellState.NOT_WALKABLE);
        path = pathfinder.findPath(3, 0, 5, 0);
        assertTrue(path.isEmpty());
    }

    @Test
    public void testFindPathWithBusyCells() {
        grid.get(3, 0).setState(CellState.NOT_WALKABLE);
        grid.get(3, 1).setState(CellState.NOT_WALKABLE);
        grid.get(3, 2).setState(CellState.NOT_WALKABLE);
        grid.get(3, 3).setState(CellState.NOT_WALKABLE);
        grid.get(3, 5).setState(CellState.NOT_WALKABLE);
        grid.get(1, 4).setState(CellState.NOT_WALKABLE);


        List<AStarCell> path = pathfinder.findPath(1, 1, 4, 5, new ArrayList<>());
        assertPathEquals(path,
                2, 1,
                2, 2,
                2, 3,
                2, 4,
                3, 4,
                4, 4,
                4, 5);
        List<AStarCell> pathWithBusyCell = pathfinder.findPath(1, 1, 4, 5, Collections.singletonList(grid.get(3, 4)));
        assertPathEquals(pathWithBusyCell,
                2, 1,
                2, 2,
                2, 3,
                2, 4,
                2, 5,
                2, 6,
                3, 6,
                4, 6,
                4, 5);
    }

    private void assertPathEquals(List<AStarCell> path, int... points) {
        assertEquals(points.length / 2, path.size(), reportNotMatchingPaths(path, points));

        int i = 0;
        for (AStarCell cell : path) {
            assertEquals(points[i++], cell.getX(), reportNotMatchingPaths(path, points));
            assertEquals(points[i++], cell.getY(), reportNotMatchingPaths(path, points));
        }

        assertEquals(points.length, i, reportNotMatchingPaths(path, points));
    }

    private Supplier<String> reportNotMatchingPaths(List<AStarCell> path, int... points){
        return () -> "Paths do not match: \n" + path + "\n != \n" + Arrays.toString(points);
    }
}