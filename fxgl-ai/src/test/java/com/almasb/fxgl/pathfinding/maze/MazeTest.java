/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.pathfinding.maze;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class MazeTest {
    @Test
    public void TestMaze() {
        var maze = new Maze(8,5);

        var atLeastOneHasLeftWall = maze.getCells()
                .stream()
                .anyMatch(c -> c.hasLeftWall());

        var atLeastOneHasTopWall = maze.getCells()
                .stream()
                .anyMatch(c -> c.hasTopWall());

        assertTrue(atLeastOneHasLeftWall);
        assertTrue(atLeastOneHasTopWall);
    }
}
