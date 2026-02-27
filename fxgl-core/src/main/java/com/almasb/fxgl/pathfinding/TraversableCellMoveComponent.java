/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.pathfinding;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.core.util.EmptyRunnable;
import com.almasb.fxgl.core.util.LazyValue;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.component.Required;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.value.ChangeListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@Required(CellMoveComponent.class)
public class TraversableCellMoveComponent<T extends TraversableCell> extends Component {

    private CellMoveComponent moveComponent;

    private LazyValue<Pathfinder<T>> pathfinder;

    private List<T> path = new ArrayList<>();

    private Runnable delayedPathCalc = EmptyRunnable.INSTANCE;

    private ReadOnlyBooleanWrapper isAtDestinationProp = new ReadOnlyBooleanWrapper(true);

    private ChangeListener<Boolean> isAtDestinationListener = (o, old, isAtDestination) -> {
        if (isAtDestination) {
            delayedPathCalc.run();
            delayedPathCalc = EmptyRunnable.INSTANCE;
        }
    };

    /**
     * This ctor is for cases when using a pre-built pathfinder.
     */
    public TraversableCellMoveComponent(Pathfinder<T> pathfinderValue) {
        this(new LazyValue<>(() -> pathfinderValue));
    }

    /**
     * This ctor is for cases when a pathfinder has not been constructed yet.
     */
    public TraversableCellMoveComponent(LazyValue<Pathfinder<T>> pathfinderValue) {
        pathfinder = pathfinderValue;
    }

    @Override
    public void onAdded() {
        moveComponent = entity.getComponent(CellMoveComponent.class);

        moveComponent.atDestinationProperty().addListener(isAtDestinationListener);
    }

    @Override
    public void onRemoved() {
        moveComponent.atDestinationProperty().removeListener(isAtDestinationListener);
    }

    public boolean isMoving() {
        return moveComponent.isMoving();
    }

    public boolean isPathEmpty() {
        return path.isEmpty();
    }

    public ReadOnlyBooleanProperty atDestinationProperty() {
        return isAtDestinationProp.getReadOnlyProperty();
    }

    /**
     * @return true when the path is empty and entity is no longer moving
     */
    public boolean isAtDestination() {
        return isAtDestinationProp.get();
    }

    public TraversableGrid<T> getGrid() {
        return pathfinder.get().getGrid();
    }

    /**
     * Note: entity's anchored position is used to compute this.
     * Note: return type is Optional since it is possible to have
     * the entity placed at a non-grid position.
     *
     * @return cell where this entity is located
     */
    public Optional<T> getCurrentCell() {
        var cellX = moveComponent.getCellX();
        var cellY = moveComponent.getCellY();

        return getGrid().getOptional(cellX, cellY);
    }

    public void stopMovementAt(int cellX, int cellY) {
        path.clear();
        moveComponent.setPositionToCell(cellX, cellY);

        isAtDestinationProp.set(true);
    }

    public void stopMovement() {
        stopMovementAt(moveComponent.getCellX(), moveComponent.getCellY());
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
        getGrid().getRandomCell(random, TraversableCell::isWalkable)
                .ifPresent(this::moveToCell);
    }

    public void moveToCell(TraversableCell cell) {
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
        isAtDestinationProp.set(false);

        if (moveComponent.isAtDestination()) {
            path = pathfinder.get().findPath(startX, startY, targetX, targetY);
        } else {
            delayedPathCalc = () -> path = pathfinder.get().findPath(moveComponent.getCellX(), moveComponent.getCellY(), targetX, targetY);
        }
    }

    @Override
    public void onUpdate(double tpf) {
        if (!isAtDestination() && !isMoving() && isPathEmpty()) {
            isAtDestinationProp.set(true);
        }

        if (path.isEmpty() || !moveComponent.isAtDestination())
            return;

        var next = path.remove(0);

        // move to next adjacent cell
        moveComponent.moveToCell(next.getX(), next.getY());
    }

    @Override
    public boolean isComponentInjectionRequired() {
        return false;
    }
}
