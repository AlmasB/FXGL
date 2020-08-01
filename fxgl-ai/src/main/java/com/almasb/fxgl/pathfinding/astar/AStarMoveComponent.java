/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.pathfinding.astar;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.core.util.EmptyRunnable;
import com.almasb.fxgl.core.util.LazyValue;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.component.Required;
import com.almasb.fxgl.pathfinding.CellMoveComponent;
import javafx.beans.value.ObservableValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@Required(CellMoveComponent.class)
public final class AStarMoveComponent extends Component {

    private CellMoveComponent moveComponent;

    private LazyValue<AStarPathfinder> pathfinder;

    private List<AStarCell> path = new ArrayList<>();

    private Runnable delayedPathCalc = EmptyRunnable.INSTANCE;

    public AStarMoveComponent(AStarGrid grid) {
        this(new LazyValue<>(() -> grid));
    }

    /**
     * This ctor is for cases when the grid has not been constructed yet.
     */
    public AStarMoveComponent(LazyValue<AStarGrid> grid) {
        pathfinder = new LazyValue<>(() -> new AStarPathfinder(grid.get()));
    }

    @Override
    public void onAdded() {
        moveComponent.atDestinationProperty().addListener(this::atDestinationListener);
    }

    @Override
    public void onRemoved() {
        moveComponent.atDestinationProperty().removeListener(this::atDestinationListener);
    }

    public boolean isMoving() {
        return moveComponent.isMoving();
    }

    public boolean isPathEmpty() {
        return path.isEmpty();
    }

    /**
     * @return true when the path is empty and entity is no longer moving
     */
    public boolean isAtDestination() {
        return !isMoving() && isPathEmpty();
    }

    public AStarGrid getGrid() {
        return pathfinder.get().getGrid();
    }

    public void stopMovementAt(int cellX, int cellY) {
        path.clear();
        moveComponent.setPositionToCell(cellX, cellY);
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

    public void moveToRandomCell() {
        moveToRandomCell(FXGLMath.getRandom());
    }

    public void moveToRandomCell(Random random) {
        getGrid().getRandomCell(random, AStarCell::isWalkable)
                .ifPresent(this::moveToCell);
    }

    public void moveToCell(AStarCell cell) {
        moveToCell(cell.getX(), cell.getY());
    }

    /**
     * Entity's anchored position is used to position it in the cell.
     */
    public void moveToCell(int x, int y) {
        int startX = moveComponent.getCellX();
        int startY = moveComponent.getCellY();

        moveToCell(startX, startY, x, y);
    }

    /**
     * Entity's anchored position is used to position it in the cell.
     * This can be used to explicitly specify the start X and Y of the entity.
     */
    public void moveToCell(int startX, int startY, int targetX, int targetY) {
        if (moveComponent.isAtDestination()) {
            path = pathfinder.get().findPath(startX, startY, targetX, targetY);
        } else {
            delayedPathCalc = () ->
                    path = pathfinder.get().findPath(
                            moveComponent.getCellX(),
                            moveComponent.getCellY(),
                            targetX,
                            targetY);
        }
    }

    @Override
    public void onUpdate(double tpf) {
        if (path.isEmpty() || !moveComponent.isAtDestination())
            return;

        var next = path.remove(0);

        // move to next adjacent cell
        moveComponent.moveToCell(next.getX(), next.getY());
    }

    @SuppressWarnings("java:S5411")
    private void atDestinationListener(ObservableValue<? extends Boolean> o, Boolean old, Boolean isAtDestination) {
        if (isAtDestination) {
            delayedPathCalc.run();
            delayedPathCalc = EmptyRunnable.INSTANCE;
        }
    }
}
