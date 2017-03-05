/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
