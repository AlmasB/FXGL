/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015 AlmasB (almaslvl@gmail.com)
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

import com.almasb.fxgl.entity.Entity;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;

/**
 * Game scene viewport.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public final class Viewport {

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
     * the code above centers the camera on player.<br/>
     * For most platformers / side scrollers use:
     * <pre>
     * bindToEntity(player, getWidth() / 2);
     * </pre>
     *
     * @param entity the entity to follow
     * @param distX distance in X between origin and entity
     * @param distY distance in Y between origin and entity
     */
    public void bindToEntity(Entity entity, double distX, double distY) {
        xProperty().bind(entity.xProperty().negate().add(distX));
        yProperty().bind(entity.yProperty().negate().add(distY));
    }

    /**
     * Unbind viewport.
     */
    public void unbind() {
        xProperty().unbind();
        yProperty().unbind();
    }
}
