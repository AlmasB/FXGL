/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.pathfinding.astar;

import com.almasb.fxgl.pathfinding.CellState;
import com.almasb.fxgl.pathfinding.Pathfinder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

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
    
    private void assertPathEquals(List<AStarCell> path, int... points) {
        assertEquals(points.length / 2, path.size());

        int i = 0;
        for (AStarCell cell : path) {
            assertEquals(points[i++], cell.getX());
            assertEquals(points[i++], cell.getY());
        }

        assertEquals(points.length, i);
    }
}