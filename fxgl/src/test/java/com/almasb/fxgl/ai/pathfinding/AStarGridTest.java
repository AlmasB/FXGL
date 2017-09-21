/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ai.pathfinding;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.*;
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
        int count = 0;

        try {
            new AStarGrid(-1, 5);
        } catch (IllegalArgumentException e) {
            count++;
        }

        assertThat(count, is(1));

        try {
            new AStarGrid(5, -1);
        } catch (IllegalArgumentException e) {
            count++;
        }

        assertThat(count, is(2));
    }

    @Test
    public void testIsWithin() throws Exception {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                assertTrue(grid.isWithin(j, i));
            }
        }

        assertFalse(grid.isWithin(GRID_SIZE, 0));
        assertFalse(grid.isWithin(0, GRID_SIZE));
    }

    @Test
    public void testGetNodeState() throws Exception {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                assertEquals(NodeState.WALKABLE, grid.getNodeState(j, i));
            }
        }
    }

    @Test
    public void testRandomNode() {
        Optional<AStarNode> maybe = grid.getRandomNode(node -> node.getX() < 3 && node.getY() > 15);
        assertThat(maybe.isPresent(), is(true));

        AStarNode node = maybe.get();
        assertThat(node.getX(), anyOf(is(0), is(1), is(2)));
        assertThat(node.getY(), anyOf(is(16), is(17), is(18), is(19)));
    }

    @Test
    public void testGetPath() throws Exception {
        List<AStarNode> path = grid.getPath(3, 0, 5, 0);
        assertPathEquals(path,
                4, 0,
                5, 0);

        for (int i = 0; i <= 4; i++)
            grid.setNodeState(4, i, NodeState.NOT_WALKABLE);

        path = grid.getPath(3, 0, 5, 0);
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

        for (int i = 0; i <= 19; i++)
            grid.setNodeState(4, i, NodeState.NOT_WALKABLE);

        path = grid.getPath(3, 0, 5, 0);
        assertTrue(path.isEmpty());
    }

    private void assertPathEquals(List<AStarNode> path, int... points) {
        assertEquals(points.length / 2, path.size());

        int i = 0;
        for (AStarNode node : path) {
            assertEquals(points[i++], node.getX());
            assertEquals(points[i++], node.getY());
        }

        assertEquals(points.length, i);
    }
}