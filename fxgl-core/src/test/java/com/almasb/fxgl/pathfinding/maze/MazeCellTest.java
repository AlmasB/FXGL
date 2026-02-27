/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.pathfinding.maze;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MazeCellTest {

    @Test
    public void testMazeCell() {
        var cell = new MazeCell(5,6);

        assertFalse(cell.hasLeftWall());
        assertFalse(cell.hasTopWall());

        cell.setLeftWall(true);
        cell.setTopWall(true);

        assertTrue(cell.hasLeftWall());
        assertTrue(cell.hasTopWall());
    }
}
