/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.pathfinding.astar;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.GameWorld;
import com.almasb.fxgl.pathfinding.CellState;
import com.almasb.fxgl.pathfinding.Grid;
import javafx.geometry.Rectangle2D;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class AStarGrid extends Grid<AStarCell> {

    /**
     * Constructs A* grid with A* cells using given width and height.
     * All cells are initially {@link CellState#WALKABLE}.
     */
    public AStarGrid(int width, int height) {
        super(AStarCell.class, width, height, (x, y) -> new AStarCell(x, y, CellState.WALKABLE));
    }

    public List<AStarCell> getWalkableCells() {
        return getCells()
                .stream()
                .filter(c -> c.getState().isWalkable())
                .collect(Collectors.toList());
    }

    /**
     * Generates an A* grid from the given world data.
     *
     * @param world game world used to check for entities
     * @param worldWidth in cell units (not pixels)
     * @param worldHeight in cell units (not pixels)
     * @param cellWidth in pixels
     * @param cellHeight in pixels
     * @param mapping maps entity types to WALKABLE / NOT_WALKABLE cells
     */
    public static AStarGrid fromWorld(GameWorld world,
                                      int worldWidth, int worldHeight,
                                      int cellWidth, int cellHeight,
                                      Function<Object, CellState> mapping) {

        var grid = new AStarGrid(worldWidth, worldHeight);
        grid.populate((x, y) -> {

            int worldX = x * cellWidth + cellWidth / 2;
            int worldY = y * cellHeight + cellHeight / 2;

            // size 4 is a "good enough" value
            List<Object> collidingTypes = world.getEntitiesInRange(new Rectangle2D(worldX-2, worldY-2, 4, 4))
                    .stream()
                    .map(Entity::getType)
                    .collect(Collectors.toList());

            boolean isWalkable;

            if (collidingTypes.isEmpty()) {
                // if no types found at given worldX, worldY, then just see what mapping returns by default
                isWalkable = mapping.apply("") == CellState.WALKABLE;
            } else {
                isWalkable = collidingTypes.stream()
                        .map(mapping)
                        .noneMatch(state -> state == CellState.NOT_WALKABLE);
            }

            return new AStarCell(x, y, isWalkable ? CellState.WALKABLE : CellState.NOT_WALKABLE);
        });

        return grid;
    }
}
