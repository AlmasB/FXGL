/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.pathfinding.dungeon;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.pathfinding.CellState;
import com.almasb.fxgl.pathfinding.astar.TraversableGrid;

import java.util.Random;

import static java.lang.Math.abs;
import static java.lang.Math.sqrt;

public class DungeonGrid extends TraversableGrid<DungeonCell> {

    private DungeonCell[][] tileMap;

    private int[][] roomPositions = new int[20][2];

    private Random random;
    
    public DungeonGrid(int width, int height) {
        this(width, height, FXGLMath.getRandom());
    }
    
    public DungeonGrid(int width, int height, Random random) {
        super(DungeonCell.class, width, height, DungeonCell::new);
        tileMap = getData();
        this.random = random;
        generateDungeon();
    }

    private void generateDungeon() {
        // Pick initial room positions
        for (int i = 0; i < roomPositions.length; i++) {
            roomPositions[i][0] = random.nextInt(getWidth());
            roomPositions[i][1] = random.nextInt(getHeight());
        }

        // Clear out rooms
        for (int i = 0; i < roomPositions.length; i++) {
            if (random.nextInt(3) == 0) {
                clearCircle(roomPositions[i][0], roomPositions[i][1], random.nextInt(3) + 1);
            } else {
                clearRect(roomPositions[i][0], roomPositions[i][1], random.nextInt(3) + 4, random.nextInt(3) + 4);
            }

            // Connect Rooms
            int connectRoom = random.nextInt(roomPositions.length);

            clearPath(roomPositions[i][0], roomPositions[i][1], roomPositions[connectRoom][0], roomPositions[connectRoom][1]);
        }
    }

    private void clearRect(int xPos, int yPos, int width, int height) {
        for (int i = 0; i < tileMap.length; i++) {
            for (int j = 0; j < tileMap[0].length; j++) {
                int xDis = abs(i - xPos);
                int yDis = abs(j - yPos);

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
}