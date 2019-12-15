/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.pathfinding;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.core.util.Consumer;
import javafx.util.Pair;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @param <T> cell type
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class Grid<T extends Cell> {

    private T[][] data;

    private int width;
    private int height;

    /**
     * Note: all cells are initialized to null.
     * Use populate() to set each cell.
     */
    public Grid(Class<T> type, int width, int height) {
        this(type, width, height, (x, y) -> null);
    }

    @SuppressWarnings("unchecked")
    public Grid(Class<T> type, int width, int height, CellGenerator<T> populateFunction) {
        if (width <= 0 || height <= 0)
            throw new IllegalArgumentException("Cannot create grid with 0 or negative size");

        this.width = width;
        this.height = height;

        data = (T[][]) Array.newInstance(type, width, height);

        populate(populateFunction);
    }

    public final void populate(CellGenerator<T> populateFunction) {
        for (int y = 0; y < data[0].length; y++) {
            for (int x = 0; x < data.length; x++) {
                set(x, y, populateFunction.apply(x, y));
            }
        }
    }

    /**
     * @return number of cells in X direction
     */
    public final int getWidth() {
        // data.length
        return width;
    }

    /**
     * @return number of cells in Y direction
     */
    public final int getHeight() {
        // data[0].length
        return height;
    }

    /**
     * Checks if given (x,y) is within the bounds of the grid,
     * i.e. get(x, y) won't return OOB.
     *
     * @return true IFF the point is within the grid
     */
    public final boolean isWithin(int x, int y) {
        return x >= 0 && x < getWidth()
                && y >= 0 && y < getHeight();
    }

    public final T[][] getData() {
        return data;
    }

    /**
     * @return a new list with grid cells
     */
    public final List<T> getCells() {
        var cells = new ArrayList<T>();
        forEach(cells::add);
        return cells;
    }

    /**
     * Note: returned cells are in the grid (i.e. bounds are checked).
     * Diagonal cells are not included.
     *
     * @return a new list of neighboring cells to given (x, y)
     */
    public final List<T> getNeighbors(int x, int y) {
        // each pair is used as a Point2D (int)
        // Key = X, Value = Y
        List<Pair<Integer, Integer>> points = List.of(
                new Pair<>(x - 1, y),
                new Pair<>(x + 1, y),
                new Pair<>(x, y - 1),
                new Pair<>(x, y + 1)
        );

        return points.stream()
                .filter((p) -> isWithin(p.getKey(), p.getValue()))
                .map((p) -> get(p.getKey(), p.getValue()))
                .collect(Collectors.toList());
    }

    public final T get(int x, int y) {
        return data[x][y];
    }

    public final void set(int x, int y, T node) {
        data[x][y] = node;
    }

    public final void forEach(Consumer<T> function) {
        for (int y = 0; y < data[0].length; y++) {
            for (int x = 0; x < data.length; x++) {
                function.accept(get(x, y));
            }
        }
    }

    /**
     * @return a random cell from the grid
     */
    public final T getRandomCell() {
        return getRandomCell(FXGLMath.getRandom());
    }

    /**
     * @return a random cell from the grid
     */
    public final T getRandomCell(Random random) {
        int x = random.nextInt(getWidth());
        int y = random.nextInt(getHeight());

        return get(x, y);
    }

    /**
     * @param predicate filter condition
     * @return a random cell that passes the filter or {@link Optional#empty()}
     * if no such cell exists
     */
    public final Optional<T> getRandomCell(Predicate<T> predicate) {
        return getRandomCell(FXGLMath.getRandom(), predicate);
    }

    /**
     * @param predicate filter condition
     * @return a random cell that passes the filter or {@link Optional#empty()}
     * if no such cell exists
     */
    public final Optional<T> getRandomCell(Random random, Predicate<T> predicate) {
        List<T> filtered = getCells().stream()
                .filter(predicate)
                .collect(Collectors.toList());

        if (filtered.isEmpty())
            return Optional.empty();

        int index = random.nextInt(filtered.size());

        return Optional.of(filtered.get(index));
    }
}