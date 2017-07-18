/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxglgames.tictactoe;

import java.util.Arrays;
import java.util.List;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class TileCombo {

    private TileEntity tile1, tile2, tile3;
    private List<TileEntity> tiles;

    public TileCombo(TileEntity tile1, TileEntity tile2, TileEntity tile3) {
        this.tile1 = tile1;
        this.tile2 = tile2;
        this.tile3 = tile3;

        tiles = Arrays.asList(tile1, tile2, tile3);
    }

    public TileEntity getTile1() {
        return tile1;
    }

    public TileEntity getTile2() {
        return tile2;
    }

    public TileEntity getTile3() {
        return tile3;
    }

    public boolean isComplete() {
        return tile1.getValue() != TileValue.NONE
                && tile1.getValue() == tile2.getValue()
                && tile1.getValue() == tile3.getValue();
    }

    /**
     * @return true if all tiles are empty
     */
    public boolean isOpen() {
        return tiles.stream()
                .allMatch(t -> t.getValue() == TileValue.NONE);
    }

    /**
     * @param value tile value
     * @return true if this combo has 2 of value and an empty slot
     */
    public boolean isTwoThirds(TileValue value) {
        TileValue oppositeValue = value == TileValue.X ? TileValue.O : TileValue.X;

        if (tiles.stream().anyMatch(t -> t.getValue() == oppositeValue))
            return false;

        return tiles.stream()
                .filter(t -> t.getValue() == TileValue.NONE)
                .count() == 1;
    }

    /**
     * @param value tile value
     * @return true if this combo has 1 of value and 2 empty slots
     */
    public boolean isOneThird(TileValue value) {
        TileValue oppositeValue = value == TileValue.X ? TileValue.O : TileValue.X;

        if (tiles.stream().anyMatch(t -> t.getValue() == oppositeValue))
            return false;

        return tiles.stream()
                .filter(t -> t.getValue() == TileValue.NONE)
                .count() == 2;
    }

    /**
     * @return first empty tile or null if no empty tiles
     */
    public TileEntity getFirstEmpty() {
        return tiles.stream()
                .filter(t -> t.getValue() == TileValue.NONE)
                .findAny()
                .orElse(null);
    }

    public String getWinSymbol() {
        return tile1.getValue().symbol;
    }
}
