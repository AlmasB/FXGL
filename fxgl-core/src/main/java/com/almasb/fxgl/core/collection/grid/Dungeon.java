/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core.collection.grid;

import java.util.Random;
import static java.lang.Math.abs;
import static java.lang.Math.sqrt;


public class Dungeon {

  // Create tilemap of given height and width
  private DungeonCell[][] tileMap;
  
  private int[][] roomPositions = new int[20][2];
  
  public void GenerateDungeon(int dungeonWidth, int dungeonHeight){

    Random rand = new Random();
    tileMap = new DungeonCell[dungeonWidth][dungeonHeight];
  
    // Setup empty dungeon
    for (int i = 0; i < tileMap.length; i++){
      for (int j = 0; j < tileMap[0].length; j++){
        tileMap[i][j] = new DungeonCell(i, j);
      }
    }
  
    // Pick initial room positions
    for (int i = 0; i < roomPositions.length; i++){
      roomPositions[i][0] = rand.nextInt(dungeonWidth);      
      roomPositions[i][1] = rand.nextInt(dungeonHeight);
    }
  
    // Clear out rooms
    for (int i = 0; i < roomPositions.length; i++){
      int subRooms = rand.nextInt(1) + 1;
  
      // Clear Circle
      if (rand.nextInt(3) == 0) {
          ClearCircle(roomPositions[i][0], roomPositions[i][1], rand.nextInt(3) + 1);
      }
        
      // Clear Rect
      else {
          ClearRect(roomPositions[i][0], roomPositions[i][1], rand.nextInt(3) + 4, rand.nextInt(3) + 4);
      }
      
      // Connect Rooms
      int connectRoom = rand.nextInt(roomPositions.length);
  
      ClearPath(roomPositions[i][0], roomPositions[i][1], roomPositions[connectRoom][0], roomPositions[connectRoom][1]);

    }
  }

  void ClearRect(int xPos, int yPos, int width, int height){
    for (int i = 0; i < tileMap.length; i++){
      for (int j = 0; j < tileMap[0].length; j++){
          int xDis = abs(i - xPos);
          int yDis = abs(j - yPos);

          if (xDis <= width/2 && yDis <= height/2) { tileMap[i][j].SetType(0); }
      }
    }
  }
  
  void ClearCircle(int xPos, int yPos, int radius){
    for (int i = 0; i < tileMap.length; i++){
      for (int j = 0; j < tileMap[0].length; j++){
          int xDis = abs(i - xPos);
          int yDis = abs(j - yPos);

          double tileDis = sqrt(xDis*xDis + yDis*yDis);
          if (tileDis <= radius) { tileMap[i][j].SetType(0); }
      }
    }
  }
  
  void ClearPath(int xStart, int yStart, int xEnd, int yEnd){
      int[] clearPos = new int[2];
      clearPos[0] = xStart;
      clearPos[1] = yStart;

      while (clearPos[0] != xEnd || clearPos[1] != yEnd){
          if (clearPos[0] < xEnd) clearPos[0]++;
          else if (clearPos[0] > xEnd) clearPos[0]--;
          else if (clearPos[1] < yEnd) clearPos[1]++;
          else if (clearPos[1] > yEnd) clearPos[1]--;

          tileMap[clearPos[0]][clearPos[1]].SetType(0);
      }
  }
}