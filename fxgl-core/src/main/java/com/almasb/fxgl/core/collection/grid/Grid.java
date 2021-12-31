/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core.collection.grid;

import com.almasb.fxgl.core.math.FXGLMath;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @param <T> cell type
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class Grid<T extends Cell> {

    private final T[][] data;

    private final int width;
    private final int height;
    private final int cellWidth;
    private final int cellHeight;

    /**
     * Note: all cells are initialized to null.
     * Use populate() to set each cell.
     */
    public Grid(Class<T> type, int width, int height) {
        this(type, width, height, (x, y) -> null);
    }

    public Grid(Class<T> type, int width, int height, CellGenerator<T> populateFunction) {
        this(type, width, height, 0, 0, populateFunction);
    }

    @SuppressWarnings("unchecked")
    public Grid(Class<T> type, int width, int height, int cellWidth, int cellHeight, CellGenerator<T> populateFunction) {
        if (cellWidth < 0 || cellHeight < 0)
            throw new IllegalArgumentException("Cannot create grid with cells of negative size");

        if (width <= 0 || height <= 0)
            throw new IllegalArgumentException("Cannot create grid with 0 or negative size");

        this.width = width;
        this.height = height;
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;

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
     * @return width of cells
     */
    public final int getCellWidth() {
        return cellWidth;
    }

    /**
     * @return height of cells
     */
    public final int getCellHeight() {
        return cellHeight;
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
     * The order is left, up, right, down.
     *
     * @return a new list of neighboring cells to given (x, y)
     */
    public final List<T> getNeighbors(int x, int y) {
        List<T> result = new ArrayList<>();
        getLeft(x, y).ifPresent(result::add);
        getUp(x, y).ifPresent(result::add);
        getRight(x, y).ifPresent(result::add);
        getDown(x, y).ifPresent(result::add);
        return result;
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

    public final Optional<T> getRight(Cell cell) {
        return getRight(cell.getX(), cell.getY());
    }

    public final Optional<T> getLeft(Cell cell) {
        return getLeft(cell.getX(), cell.getY());
    }

    public final Optional<T> getUp(Cell cell) {
        return getUp(cell.getX(), cell.getY());
    }

    public final Optional<T> getDown(Cell cell) {
        return getDown(cell.getX(), cell.getY());
    }

    public final Optional<T> getRight(int x, int y) {
        return getOptional(x + 1, y);
    }

    public final Optional<T> getLeft(int x, int y) {
        return getOptional(x - 1, y);
    }

    public final Optional<T> getUp(int x, int y) {
        return getOptional(x, y - 1);
    }

    public final Optional<T> getDown(int x, int y) {
        return getOptional(x, y + 1);
    }

    /**
     * @param x pixel coord x
     * @param y pixel coord y
     * @return optional grid cell
     */
    public final Optional<T> getOptionalByPixels(double x, double y) {
        if (getCellWidth() == 0 || getCellHeight() == 0)
            return Optional.empty();

        int cellX = (int) (x / getCellWidth());
        int cellY = (int) (y / getCellHeight());

        return getOptional(cellX, cellY);
    }

    /**
     * @param x grid coord x
     * @param y grid coord y
     * @return optional grid cell
     */
    public final Optional<T> getOptional(int x, int y) {
        if (isWithin(x, y))
            return Optional.of(get(x, y));

        return Optional.empty();
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