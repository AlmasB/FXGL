/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core.collection.grid;

public class DungeonCell extends Cell {
    private int cellType;

    public DungeonCell(int x, int y) {
        super(x, y);
        cellType = 1;
    }

    public void SetType(int type){
      cellType = type;
    }

    public int GetType(){
      return cellType;
    }
}