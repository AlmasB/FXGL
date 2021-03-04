/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.pathfinding;

import com.almasb.fxgl.core.collection.grid.Cell;
import com.almasb.fxgl.entity.component.Component;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;

import static java.lang.Math.abs;

/**
 * Enables cell based movement for an entity.
 * Can potentially be used for any top-down movement, though may not be as effective.
 * For all entity position calculations, entity's anchored position is used.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public final class CellMoveComponent extends Component {

    private int nextCellX;
    private int nextCellY;

    private int cellWidth;
    private int cellHeight;
    private double speed;
    private boolean isAllowRotation = false;

    private ReadOnlyBooleanWrapper isAtDestinationProp = new ReadOnlyBooleanWrapper(true);

    private boolean isMovingUp = false;
    private boolean isMovingDown = false;
    private boolean isMovingLeft = false;
    private boolean isMovingRight = false;

    public CellMoveComponent(int cellWidth, int cellHeight, double speed) {
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;
        this.speed = speed;
    }

    public ReadOnlyBooleanProperty atDestinationProperty() {
        return isAtDestinationProp.getReadOnlyProperty();
    }

    /**
     * @return true if the entity has reached the destination cell and is no longer moving
     */
    public boolean isAtDestination() {
        return isAtDestinationProp.getValue();
    }

    /**
     * @return true if the entity has not yet reached its destination
     */
    public boolean isMoving() {
        return !isAtDestination();
    }

    public boolean isMovingUp() {
        return isMovingUp;
    }

    public boolean isMovingDown() {
        return isMovingDown;
    }

    public boolean isMovingLeft() {
        return isMovingLeft;
    }

    public boolean isMovingRight() {
        return isMovingRight;
    }

    public int getCellWidth() {
        return cellWidth;
    }

    public int getCellHeight() {
        return cellHeight;
    }

    public void setCellWidth(int cellWidth) {
        this.cellWidth = cellWidth;
    }

    public void setCellHeight(int cellHeight) {
        this.cellHeight = cellHeight;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    /**
     * Note: entity's anchored position is used to compute this.
     *
     * @return the cell x where entity is currently at
     */
    public int getCellX() {
        return (int) (entity.getAnchoredPosition().getX() / cellWidth);
    }

    /**
     * Note: entity's anchored position is used to compute this.
     *
     * @return the cell y where entity is currently at
     */
    public int getCellY() {
        return (int) (entity.getAnchoredPosition().getY() / cellHeight);
    }

    /**
     * Sets (anchored) position of the entity to given cell.
     */
    public void setPositionToCell(Cell cell) {
        setPositionToCell(cell.getX(), cell.getY());
    }

    /**
     * Sets (anchored) position of the entity to given cell, identified using given cell X, Y.
     */
    public void setPositionToCell(int cellX, int cellY) {
        // cell center
        int cx = cellX * cellWidth + cellWidth / 2;
        int cy = cellY * cellHeight + cellHeight / 2;

        entity.setAnchoredPosition(cx, cy);

        isAtDestinationProp.setValue(true);
        isMovingLeft = false;
        isMovingRight = false;
        isMovingUp = false;
        isMovingDown = false;
    }

    public void moveToCell(Cell cell) {
        moveToCell(cell.getX(), cell.getY());
    }

    public void moveToCell(int cellX, int cellY) {
        nextCellX = cellX;
        nextCellY = cellY;

        isAtDestinationProp.set(false);
    }

    /**
     * Allows entity to rotate (only 4 directions) based on its velocity.
     */
    public CellMoveComponent allowRotation(boolean isAllowRotation) {
        this.isAllowRotation = isAllowRotation;
        return this;
    }

    @Override
    public void onUpdate(double tpf) {
        if (isAtDestination())
            return;

        double tpfSpeed = tpf * speed;

        // cell center
        int cx = nextCellX * cellWidth + cellWidth / 2;
        int cy = nextCellY * cellHeight + cellHeight / 2;

        var entityAnchoredPosition = entity.getAnchoredPosition();

        // move in x and y per frame
        double dx = cx - entityAnchoredPosition.getX();
        double dy = cy - entityAnchoredPosition.getY();

        if (isAllowRotation)
            updateRotation(dx, dy);

        int offsetX = (int) (entityAnchoredPosition.getX() - entity.getX());
        int offsetY = (int) (entityAnchoredPosition.getY() - entity.getY());

        if (abs(dx) <= tpfSpeed) {
            isMovingLeft = false;
            isMovingRight = false;
            entity.setX(cx - offsetX);
        } else {
            isMovingLeft = Math.signum(dx) < 0;
            isMovingRight = Math.signum(dx) > 0;
            entity.translateX(tpfSpeed * Math.signum(dx));
        }

        if (abs(dy) <= tpfSpeed) {
            isMovingUp = false;
            isMovingDown = false;
            entity.setY(cy - offsetY);
        } else {
            isMovingUp = Math.signum(dy) < 0;
            isMovingDown = Math.signum(dy) > 0;
            entity.translateY(tpfSpeed * Math.signum(dy));
        }

        // get position after movement
        entityAnchoredPosition = entity.getAnchoredPosition();

        if ((int) entityAnchoredPosition.getX() == cx && (int) entityAnchoredPosition.getY() == cy) {
            setPositionToCell(nextCellX, nextCellY);
        }
    }

    /**
     * @param dx move distance in X
     * @param dy move distance in Y
     */
    private void updateRotation(double dx, double dy) {
        if (dx > 0) {
            entity.setRotation(0);
        } else if (dx < 0) {
            entity.setRotation(180);
        } else if (dy > 0) {
            entity.setRotation(90);
        } else if (dy < 0) {
            entity.setRotation(270);
        }
    }

    @Override
    public boolean isComponentInjectionRequired() {
        return false;
    }
}
