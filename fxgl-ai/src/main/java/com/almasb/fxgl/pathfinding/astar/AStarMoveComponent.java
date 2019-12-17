/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.pathfinding.astar;

import com.almasb.fxgl.core.util.LazyValue;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.component.Required;
import com.almasb.fxgl.pathfinding.CellMoveComponent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@Required(CellMoveComponent.class)
public final class AStarMoveComponent extends Component {

    private CellMoveComponent moveComponent;

    private LazyValue<AStarPathfinder> pathfinder;

    private List<AStarCell> path = new ArrayList<>();

    public AStarMoveComponent(AStarGrid grid) {
        this(new LazyValue<>(() -> grid));
    }

    /**
     * This ctor is for cases when the grid has not been constructed yet.
     */
    public AStarMoveComponent(LazyValue<AStarGrid> grid) {
        pathfinder = new LazyValue<>(() -> new AStarPathfinder(grid.get()));
    }

    public boolean isMoving() {
        return moveComponent.isMoving();
    }

    public boolean isPathEmpty() {
        return path.isEmpty();
    }

    public AStarGrid getGrid() {
        return pathfinder.get().getGrid();
    }

    public void moveToRightCell() {
        getGrid().getRight(moveComponent.getCellX(), moveComponent.getCellY())
                .ifPresent(this::moveToCell);
    }

    public void moveToLeftCell() {
        getGrid().getLeft(moveComponent.getCellX(), moveComponent.getCellY())
                .ifPresent(this::moveToCell);
    }

    public void moveToUpCell() {
        getGrid().getUp(moveComponent.getCellX(), moveComponent.getCellY())
                .ifPresent(this::moveToCell);
    }

    public void moveToDownCell() {
        getGrid().getDown(moveComponent.getCellX(), moveComponent.getCellY())
                .ifPresent(this::moveToCell);
    }

    public void moveToCell(AStarCell cell) {
        moveToCell(cell.getX(), cell.getY());
    }

    /**
     * Entity's center is used to position it in the cell.
     */
    public void moveToCell(int x, int y) {
        int startX = moveComponent.getCellX();
        int startY = moveComponent.getCellY();

        moveToCell(startX, startY, x, y);
    }

    /**
     * Entity's center is used to position it in the cell.
     * This can be used to explicitly specify the start X and Y of the entity.
     */
    public void moveToCell(int startX, int startY, int targetX, int targetY) {
        path = pathfinder.get().findPath(startX, startY, targetX, targetY);
    }

    @Override
    public void onUpdate(double tpf) {
        if (path.isEmpty() || moveComponent.isMoving())
            return;

        var next = path.remove(0);

        moveComponent.moveToCell(next.getX(), next.getY());
    }
}
