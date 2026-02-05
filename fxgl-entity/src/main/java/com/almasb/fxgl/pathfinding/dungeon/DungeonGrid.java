/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.pathfinding.dungeon;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.pathfinding.CellState;
import com.almasb.fxgl.pathfinding.TraversableGrid;

import static java.lang.Math.abs;
import static java.lang.Math.sqrt;

public class DungeonGrid extends TraversableGrid<DungeonCell> {

    private static final DungeonConfig DEFAULT = new DungeonConfig(
            FXGLMath.getRandom(),
            6,
            4, 9,
            4, 9
    );

    private DungeonCell[][] tileMap;

    private DungeonConfig config;
    
    public DungeonGrid(int width, int height) {
        this(width, height, DEFAULT);
    }
    
    public DungeonGrid(int width, int height, DungeonConfig config) {
        super(DungeonCell.class, width, height, DungeonCell::new);
        tileMap = getData();
        this.config = config;
        generateDungeon();
    }

    private void generateDungeon() {
        if (config.numRooms() < 1)
            throw new IllegalArgumentException("A dungeon must have at least 1 room.");

        if (config.minRoomWidth() < 2)
            throw new IllegalArgumentException("Minimum room width must be at least 2.");

        if (config.minRoomHeight() < 2)
            throw new IllegalArgumentException("Minimum room height must be at least 2.");

        record Pos(int x, int y) {}

        var random = config.random();

        Pos[] roomPositions = new Pos[config.numRooms()];

        // Pick initial room positions
        for (int i = 0; i < roomPositions.length; i++) {
            roomPositions[i] = new Pos(random.nextInt(getWidth()), random.nextInt(getHeight()));
        }

        // Clear out rooms
        for (int i = 0; i < roomPositions.length; i++) {
            if (random.nextInt(3) == 0) {
                clearCircle(roomPositions[i].x, roomPositions[i].y, random.nextInt(3) + 1);
            } else {
                clearRect(roomPositions[i].x, roomPositions[i].y, randomRectWidth(), randomRectHeight());
            }

            // if there is just 1 room, then there is nothing to connect it to
            if (config.numRooms() == 1)
                break;

            // Connect room i to a random room
            int randomRoom = random.nextInt(roomPositions.length);

            // Ensure i is not the same as the chosen random room
            while (randomRoom == i) {
                randomRoom = random.nextInt(roomPositions.length);
            }

            clearPath(roomPositions[i].x, roomPositions[i].y, roomPositions[randomRoom].x, roomPositions[randomRoom].y);
        }
    }

    private void clearRect(int x, int y, int width, int height) {
        for (int i = 0; i < tileMap.length; i++) {
            for (int j = 0; j < tileMap[0].length; j++) {
                int xDis = abs(i - x);
                int yDis = abs(j - y);

                if (xDis <= width / 2 && yDis <= height / 2) {
                    tileMap[i][j].setState(CellState.WALKABLE);
                }
            }
        }
    }

    private void clearCircle(int xPos, int yPos, int radius) {
        for (int i = 0; i < tileMap.length; i++) {
            for (int j = 0; j < tileMap[0].length; j++) {
                int xDis = abs(i - xPos);
                int yDis = abs(j - yPos);

                double tileDis = sqrt(xDis * xDis + yDis * yDis);
                if (tileDis <= radius) {
                    tileMap[i][j].setState(CellState.WALKABLE);
                }
            }
        }
    }

    private void clearPath(int xStart, int yStart, int xEnd, int yEnd) {
        int[] clearPos = new int[2];
        clearPos[0] = xStart;
        clearPos[1] = yStart;

        while (clearPos[0] != xEnd || clearPos[1] != yEnd) {
            if (clearPos[0] < xEnd) clearPos[0]++;
            else if (clearPos[0] > xEnd) clearPos[0]--;
            else if (clearPos[1] < yEnd) clearPos[1]++;
            else if (clearPos[1] > yEnd) clearPos[1]--;

            tileMap[clearPos[0]][clearPos[1]].setState(CellState.WALKABLE);
        }
    }

    private int randomRectWidth() {
        return config.minRoomWidth() + config.random().nextInt(config.maxRoomWidth() - config.minRoomWidth() + 1);
    }

    private int randomRectHeight() {
        return config.minRoomHeight() + config.random().nextInt(config.maxRoomHeight() - config.minRoomHeight() + 1);
    }
}