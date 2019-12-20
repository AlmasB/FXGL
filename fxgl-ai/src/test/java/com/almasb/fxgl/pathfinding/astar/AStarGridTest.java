/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.pathfinding.astar;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.GameWorld;
import com.almasb.fxgl.pathfinding.CellState;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.*;
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
                assertFalse(grid.get(x, y).getState().isNotWalkable());
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

        grid.get(1, 1).setState(CellState.NOT_WALKABLE);
        grid.get(3, 5).setState(CellState.NOT_WALKABLE);

        assertTrue(grid.get(1, 1).getState().isNotWalkable());
        assertFalse(grid.get(1, 1).isWalkable());
        assertTrue(grid.get(3, 5).getState().isNotWalkable());
        assertFalse(grid.get(3, 5).isWalkable());

        assertThat(grid.getWalkableCells(), not(hasItem(grid.get(1, 1))));
        assertThat(grid.getWalkableCells(), not(hasItem(grid.get(3, 5))));
        assertThat(grid.getWalkableCells().size(), is(cells.size() - 2));
    }

    @Test
    public void testMakeGridFromWorld() {
        var world = new GameWorld();

        var e = new Entity();
        e.setPosition(0, 40);
        e.setType("PLAYER");

        var wall1 = new Entity();
        wall1.setPosition(40, 0);
        wall1.setType("WALL");
        wall1.getBoundingBoxComponent().addHitBox(new HitBox(BoundingShape.box(40, 40)));

        var wall2 = new Entity();
        wall2.setPosition(80, 40);
        wall2.setType("WALL");
        wall2.getBoundingBoxComponent().addHitBox(new HitBox(BoundingShape.box(40, 40)));

        world.addEntities(e, wall1, wall2);

        var grid = AStarGrid.fromWorld(world, 3, 3, 40, 40, (type) -> {
            if (type.equals("WALL"))
                return CellState.NOT_WALKABLE;

            return CellState.WALKABLE;
        });

        assertThat(grid.getWidth(), is(3));
        assertThat(grid.getHeight(), is(3));

        assertThat(grid.getWalkableCells(), not(hasItem(grid.get(1, 0))));
        assertThat(grid.getWalkableCells(), not(hasItem(grid.get(2, 1))));
        assertThat(grid.getWalkableCells().size(), is(9 - 2));
    }
}