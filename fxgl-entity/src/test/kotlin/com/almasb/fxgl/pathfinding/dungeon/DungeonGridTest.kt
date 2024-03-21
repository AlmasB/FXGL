/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.pathfinding.dungeon

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

/**
 * @author Almas Baim (https://github.com/AlmasB)
 */
class DungeonGridTest {

    @Test
    fun `Generation fails if fewer than 1 room or min unit width or height`() {
        assertThrows<IllegalArgumentException> {
            DungeonGrid(60, 40, DungeonConfig(Random(3), 0, 4, 6, 4, 6))
        }

        assertThrows<IllegalArgumentException> {
            DungeonGrid(60, 40, DungeonConfig(Random(3), 3, 0, 6, 4, 6))
        }

        assertThrows<IllegalArgumentException> {
            DungeonGrid(60, 40, DungeonConfig(Random(3), 3, 4, 6, 0, 6))
        }
    }
}