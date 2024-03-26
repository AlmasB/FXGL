/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.pathfinding.dungeon;

import java.util.Random;

/**
 * @author Almas Baim (https://github.com/AlmasB)
 */
public record DungeonConfig(
        Random random,
        int numRooms,
        int minRoomWidth,
        int maxRoomWidth,
        int minRoomHeight,
        int maxRoomHeight
) { }
