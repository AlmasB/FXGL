/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.components;

import com.almasb.fxgl.devtools.DeveloperEditable;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.component.CopyableComponent;
import com.almasb.fxgl.entity.component.CoreComponent;
import com.almasb.fxgl.entity.component.SerializableComponent;
import com.almasb.fxgl.io.serialization.Bundle;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;
import org.jetbrains.annotations.NotNull;

/**
 * Component that adds a 2d position to an entity.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
@CoreComponent
public class PositionComponent extends Component
        implements SerializableComponent, CopyableComponent<PositionComponent> {

    private final DoubleProperty x;
    private final DoubleProperty y;

    /**
     * Constructs a position component from given x and y.
     *
     * @param x x coordinate
     * @param y y coordinate
     */
    public PositionComponent(double x, double y) {
        this.x = new SimpleDoubleProperty(x);
        this.y = new SimpleDoubleProperty(y);
    }

    /**
     * Constructs a position component from given point.
     *
     * @param position point
     */
    public PositionComponent(Point2D position) {
        this(position.getX(), position.getY());
    }

    /**
     * Constructs a position component with x = y = 0.
     */
    public PositionComponent() {
        this(0, 0);
    }

    /**
     * @return x coordinate
     */
    public double getX() {
        return x.get();
    }

    /**
     * @return x property
     */
    @DeveloperEditable("X")
    public DoubleProperty xProperty() {
        return x;
    }

    /**
     * Set x.
     *
     * @param x x coordinate
     */
    public void setX(double x) {
        this.x.set(x);
    }

    /**
     * @return y coordinate
     */
    public double getY() {
        return y.get();
    }

    /**
     * @return y property
     */
    @DeveloperEditable("Y")
    public DoubleProperty yProperty() {
        return y;
    }

    /**
     * Set y.
     *
     * @param y y coordinate
     */
    public void setY(double y) {
        this.y.set(y);
    }

    /**
     * @return position
     */
    public Point2D getValue() {
        return new Point2D(getX(), getY());
    }

    /**
     * Set position.
     *
     * @param x x coordinate
     * @param y y coordinate
     */
    public void setValue(double x, double y) {
        setX(x);
        setY(y);
    }

    /**
     * Set position.
     *
     * @param position position
     */
    public void setValue(Point2D position) {
        setValue(position.getX(), position.getY());
    }

    /**
     * Translate X by given value.
     *
     * @param x dx
     */
    public void translateX(double x) {
        setX(getX() + x);
    }

    /**
     * Translate Y by given value.
     *
     * @param y dy
     */
    public void translateY(double y) {
        setY(getY() + y);
    }

    /**
     * Translate x and y by given values.
     *
     * @param x dx value
     * @param y dy value
     */
    public void translate(double x, double y) {
        translateX(x);
        translateY(y);
    }

    /**
     * Translate x and y by given vector.
     *
     * @param vector translate vector
     */
    public void translate(Point2D vector) {
        translate(vector.getX(), vector.getY());
    }

    /**
     * @param position the point to move towards
     * @param distance the distance to move
     */
    public void translateTowards(Point2D position, double distance) {
        translate(position.subtract(getX(), getY()).normalize().multiply(distance));
    }

    /**
     * @param other the other component
     * @return distance in pixels from this position to the other
     */
    public double distance(PositionComponent other) {
        return getValue().distance(other.getValue());
    }

    /**
     * Note: if there is no bbox attached, entity is considered as a point,
     * else as a rectangle with the center placed in the cell.
     *
     * @param cellSize size of each cell in the grid
     * @return x position of the entity in the grid
     */
    public int getGridX(int cellSize) {
        double centerX = getEntity().getComponentOptional(BoundingBoxComponent.class)
                .map(bbox -> bbox.getMinXWorld() + bbox.getWidth() / 2)
                .orElseGet(this::getX);

        return (int) (centerX / cellSize);
    }

    /**
     * Note: if there is no bbox attached, entity is considered as a point,
     * else as a rectangle with the center placed in the cell.
     *
     * @param cellSize size of each cell in the grid
     * @return y position of the entity in the grid
     */
    public int getGridY(int cellSize) {
        double centerY = getEntity().getComponentOptional(BoundingBoxComponent.class)
                .map(bbox -> bbox.getMinYWorld() + bbox.getHeight() / 2)
                .orElseGet(this::getY);

        return (int) (centerY / cellSize);
    }

    @Override
    public String toString() {
        return "Position(" + getX() + "," + getY() + ")";
    }

    @Override
    public void write(@NotNull Bundle bundle) {
        bundle.put("x", getX());
        bundle.put("y", getY());
    }

    @Override
    public void read(@NotNull Bundle bundle) {
        setValue(bundle.get("x"), bundle.get("y"));
    }

    @Override
    public PositionComponent copy() {
        return new PositionComponent(getX(), getY());
    }
}
