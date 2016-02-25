/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2016 AlmasB (almaslvl@gmail.com)
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

package com.almasb.fxgl.scene;

import com.almasb.ents.Entity;
import com.almasb.fxgl.entity.component.PositionComponent;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;

/**
 * Game scene viewport.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public final class Viewport {

    private final double width, height;

    /**
     * Constructs a viewport with given width and height.
     *
     * @param width viewport width
     * @param height viewport height
     */
    public Viewport(double width, double height) {
        this.width = width;
        this.height = height;
    }

    /**
     * @return viewport width
     */
    public double getWidth() {
        return width;
    }

    /**
     * @return viewport height
     */
    public double getHeight() {
        return height;
    }

    /**
     * @return current visible viewport area
     */
    public Rectangle2D getVisibleArea() {
        return new Rectangle2D(getX(), getY(), getX() + getWidth(), getY() + getHeight());
    }

    private DoubleProperty x = new SimpleDoubleProperty();
    private DoubleProperty y = new SimpleDoubleProperty();

    /**
     *
     * @return origin x
     */
    public double getX() {
        return x.get();
    }

    /**
     *
     * @return origin x property
     */
    public DoubleProperty xProperty() {
        return x;
    }

    /**
     * Set origin x. Note, bound viewport cannot be set.
     *
     * @param x x coordinate
     */
    public void setX(double x) {
        this.x.set(x);
    }

    /**
     *
     * @return origin y
     */
    public double getY() {
        return y.get();
    }

    /**
     *
     * @return origin y property
     */
    public DoubleProperty yProperty() {
        return y;
    }

    /**
     * Set origin y. Note, bound viewport cannot be set.
     *
     * @param y y property
     */
    public void setY(double y) {
        this.y.set(y);
    }

    /**
     *
     * @return viewport origin (x, y)
     */
    public Point2D getOrigin() {
        return new Point2D(getX(), getY());
    }

    /**
     * Binds the viewport to entity so that it follows the given entity.
     * distX and distY represent bound distance between entity and viewport origin.
     * <pre>
     * bindToEntity(player, getWidth() / 2, getHeight() / 2);
     * </pre>
     * the code above centers the camera on player.
     *
     * @param entity the entity to follow
     * @param distX distance in X between origin and entity
     * @param distY distance in Y between origin and entity
     */
    public void bindToEntity(Entity entity, double distX, double distY) {
        PositionComponent position = entity.getComponent(PositionComponent.class)
                .orElseThrow(() -> new IllegalArgumentException("Cannot bind to entity without PositionComponent"));

        // origin X Y with no bounds
        NumberBinding bx = position.xProperty().add(-distX);
        NumberBinding by = position.yProperty().add(-distY);

        // origin X Y with bounds applied
        NumberBinding boundX = Bindings.when(bx.lessThan(minX)).then(minX).otherwise(position.xProperty().add(-distX));
        NumberBinding boundY = Bindings.when(by.lessThan(minY)).then(minY).otherwise(position.yProperty().add(-distY));

        boundX = Bindings.when(bx.greaterThan(maxX.subtract(width))).then(maxX.subtract(width)).otherwise(boundX);
        boundY = Bindings.when(by.greaterThan(maxY.subtract(height))).then(maxY.subtract(height)).otherwise(boundY);

        x.bind(boundX);
        y.bind(boundY);
    }

    /**
     * Unbind viewport.
     */
    public void unbind() {
        xProperty().unbind();
        yProperty().unbind();
    }

    private IntegerProperty minX = new SimpleIntegerProperty(Integer.MIN_VALUE);
    private IntegerProperty minY = new SimpleIntegerProperty(Integer.MIN_VALUE);
    private IntegerProperty maxX = new SimpleIntegerProperty(Integer.MAX_VALUE);
    private IntegerProperty maxY = new SimpleIntegerProperty(Integer.MAX_VALUE);

    /**
     * Set bounds to viewport so that the viewport will not move outside the bounds
     * when following an entity.
     *
     * @param minX min x
     * @param minY min y
     * @param maxX max x
     * @param maxY max y
     */
    public void setBounds(int minX, int minY, int maxX, int maxY) {
        this.minX.set(minX);
        this.minY.set(minY);
        this.maxX.set(maxX);
        this.maxY.set(maxY);
    }
}
