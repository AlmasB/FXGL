/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core.collection;

import com.almasb.fxgl.util.BiFunction;
import com.almasb.fxgl.util.Consumer;
import com.almasb.fxgl.util.TriConsumer;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class Grid<T> {

    private T[][] data;

    public Grid(int width, int height) {
        this(width, height, (x, y) -> null);
    }

    @SuppressWarnings("unchecked")
    public Grid(int width, int height, BiFunction<Integer, Integer, T> initFunction) {
        if (width <= 0 || height <= 0)
            throw new IllegalArgumentException("Cannot create grid with 0 or negative size");

        data = (T[][]) new Object[width][height];

        populate(initFunction);
    }

    public void populate(BiFunction<Integer, Integer, T> populateFunction) {
        for (int y = 0; y < data[0].length; y++) {
            for (int x = 0; x < data.length; x++) {
                set(x, y, populateFunction.apply(x, y));
            }
        }
    }

    public T get(int x, int y) {
        return data[x][y];
    }

    public void set(int x, int y, T node) {
        data[x][y] = node;
    }

    public void forEach(Consumer<T> function) {
        for (int y = 0; y < data[0].length; y++) {
            for (int x = 0; x < data.length; x++) {
                function.accept(get(x, y));
            }
        }
    }

    public void forEach(TriConsumer<T, Integer, Integer> function) {
        for (int y = 0; y < data[0].length; y++) {
            for (int x = 0; x < data.length; x++) {
                function.accept(get(x, y), x, y);
            }
        }
    }
}
