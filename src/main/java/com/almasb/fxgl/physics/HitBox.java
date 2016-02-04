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
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.almasb.fxgl.physics;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;

/**
 * A bounding collision box.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public final class HitBox {
    private final Bounds bounds;
    private final String name;

    private final BoundingShape shape;

    /**
     * Creates a hit box with given name and bounds.
     * The bounds are calculated from the local entity origin
     * in local coordinates.
     * <p>
     * For example: an entity with width 40 and height 80 could have 2 hit boxes.
     * one for HEAD and one for BODY as follows:
     * </p>
     * <pre>
     *     Entity entity = ...
     *     entity.addHitBox(new HitBox("HEAD", new BoundingBox(0, 0, 40, 40));
     *     entity.addHitBox(new HitBox("BODY", new BoundingBox(0, 40, 40, 40));
     *
     * </pre>
     * Note, the 2nd bounding box has y = 40.
     *
     * @param name hit box name
     * @param bounds hit box bounds
     */
    public HitBox(String name, BoundingBox bounds) {
        this(name, bounds, BoundingShape.BOX);
    }

    public HitBox(String name, BoundingBox bounds, BoundingShape shape) {
        this.name = name;
        this.bounds = bounds;
        this.shape = shape;
    }

    /**
     * Computes new bounds based on translated X and Y
     * of an entity.
     *
     * @param x entity x
     * @param y entity y
     * @return computed bounds
     */
    public Bounds translate(double x, double y) {
        return new BoundingBox(x + bounds.getMinX(), y + bounds.getMinY(),
                bounds.getWidth(), bounds.getHeight());
    }

    /**
     * Computes new bounds based on translated X and Y
     * of an entity with X axis being flipped.
     *
     * @param x entity x
     * @param y entity y
     * @param entityWidth entity width
     * @return computed bounds
     */
    public Bounds translateXFlipped(double x, double y, double entityWidth) {
        return new BoundingBox(x + entityWidth - bounds.getMinX() - bounds.getWidth(), y + bounds.getMinY(),
                bounds.getWidth(), bounds.getHeight());
    }

    public Bounds getBounds() {
        return bounds;
    }

    public BoundingShape getShape() {
        return shape;
    }

    public double getMinX() {
        return bounds.getMinX();
    }

    public double getMinY() {
        return bounds.getMinY();
    }

    /**
     *
     * @return maxX of internal bounds (x + width)
     */
    public double getMaxX() {
        return bounds.getMaxX();
    }

    /**
     *
     * @return maxY of internal bounds (y + height)
     */
    public double getMaxY() {
        return bounds.getMaxY();
    }

    /**
     *
     * @return hit box name
     */
    public String getName() {
        return name;
    }
}
