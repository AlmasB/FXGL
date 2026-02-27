/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.pathfinding.astar;

import com.almasb.fxgl.core.util.LazyValue;
import com.almasb.fxgl.entity.component.Required;
import com.almasb.fxgl.pathfinding.CellMoveComponent;
import com.almasb.fxgl.pathfinding.TraversableCellMoveComponent;
import com.almasb.fxgl.pathfinding.TraversableGrid;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@Required(CellMoveComponent.class)
public final class AStarMoveComponent<T extends AStarCell> extends TraversableCellMoveComponent<T> {

    public AStarMoveComponent(TraversableGrid<T> grid) {
        this(new LazyValue<>(() -> grid));
    }

    /**
     * This ctor is for cases when the grid has not been constructed yet.
     */
    public AStarMoveComponent(LazyValue<TraversableGrid<T>> grid) {
        super(new LazyValue<>(() -> new AStarPathfinder<>(grid.get())));
    }

    /**
     * This ctor is for cases when using a pre-built pathfinder.
     */
    public AStarMoveComponent(AStarPathfinder<T> pathfinderValue) {
        super(pathfinderValue);
    }
}
