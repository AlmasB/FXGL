/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.almasb.fxgl.entity.component;

import com.almasb.fxgl.devtools.DeveloperEditable;
import com.almasb.fxgl.ecs.AbstractComponent;
import com.almasb.fxgl.ecs.CopyableComponent;
import com.almasb.fxgl.ecs.serialization.SerializableComponent;
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
public class PositionComponent extends AbstractComponent
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
     * @param speed the speed at which to move
     */
    public void translateTowards(Point2D position, double speed) {
        translate(position.subtract(getX(), getY()).normalize().multiply(speed));
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
        double centerX = getEntity().getComponent(BoundingBoxComponent.class)
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
        double centerY = getEntity().getComponent(BoundingBoxComponent.class)
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
