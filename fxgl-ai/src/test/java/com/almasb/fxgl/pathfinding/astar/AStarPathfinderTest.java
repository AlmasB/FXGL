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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AStarPathfinderTest {

    private static final int GRID_SIZE = 20;
    private AStarGrid grid;

    @BeforeEach
    public void setUp() {
        grid = new AStarGrid(GRID_SIZE, GRID_SIZE);
    }

    // TODO: impl
//    @Test
//    public void testGetPath() throws Exception {
//        List<AStarNode> path = grid.getPath(3, 0, 5, 0);
//        assertPathEquals(path,
//                4, 0,
//                5, 0);
//
//        for (int i = 0; i <= 4; i++)
//            grid.setNodeState(4, i, NodeState.NOT_WALKABLE);
//
//        path = grid.getPath(3, 0, 5, 0);
//        assertPathEquals(path,
//                3, 1,
//                3, 2,
//                3, 3,
//                3, 4,
//                3, 5,
//                4, 5,
//                5, 5,
//                5, 4,
//                5, 3,
//                5, 2,
//                5, 1,
//                5, 0);
//
//        for (int i = 0; i <= 19; i++)
//            grid.setNodeState(4, i, CellState.NOT_WALKABLE);
//
//        path = grid.getPath(3, 0, 5, 0);
//        assertTrue(path.isEmpty());
//    }
//
//    private void assertPathEquals(List<AStarNode> path, int... points) {
//        assertEquals(points.length / 2, path.size());
//
//        int i = 0;
//        for (AStarNode node : path) {
//            assertEquals(points[i++], node.getX());
//            assertEquals(points[i++], node.getY());
//        }
//
//        assertEquals(points.length, i);
//    }
}