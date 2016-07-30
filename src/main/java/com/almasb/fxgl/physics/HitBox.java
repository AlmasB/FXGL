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
package com.almasb.fxgl.physics;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import org.jbox2d.collision.shapes.ShapeType;

import java.io.IOException;
import java.io.Serializable;

/**
 * A bounding collision box.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public final class HitBox implements Serializable {

    /**
     * Name of this hit box.
     */
    private String name;

    /**
     * Shape of this hit box.
     */
    private BoundingShape shape;

    /**
     * Bounding box (computed from shape) of this hit box.
     */
    private Bounds bounds;

    /**
     * Creates a hit box with given name and shape.
     * Local origin is set to default (0, 0).
     *
     * @param name name of the hit box
     * @param shape bounding shape
     */
    public HitBox(String name, BoundingShape shape) {
        this(name, Point2D.ZERO, shape);
    }

    /**
     * Creates a hit box with given name, local origin (top-left) and shape.
     *
     * @param name name of the hit box
     * @param localOrigin origin of hit box
     * @param shape bounding shape
     */
    public HitBox(String name, Point2D localOrigin, BoundingShape shape) {
        this.name = name;
        this.shape = shape;
        this.bounds = new BoundingBox(localOrigin.getX(), localOrigin.getY(),
                shape.getSize().getWidth(), shape.getSize().getHeight());
    }

    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.writeObject(name);

        out.writeDouble(bounds.getMinX());
        out.writeDouble(bounds.getMinY());
        out.writeDouble(bounds.getWidth());
        out.writeDouble(bounds.getHeight());

        out.writeDouble(shape.size.getWidth());
        out.writeDouble(shape.size.getHeight());

        out.writeObject(shape.type);

        if (shape.type == ShapeType.CHAIN) {
            Point2D[] points = (Point2D[]) shape.data;
            out.writeInt(points.length);

            for (Point2D p : points) {
                out.writeDouble(p.getX());
                out.writeDouble(p.getY());
            }
        }
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        name = (String) in.readObject();

        bounds = new BoundingBox(in.readDouble(), in.readDouble(), in.readDouble(), in.readDouble());

        Dimension2D size = new Dimension2D(in.readDouble(), in.readDouble());

        ShapeType type = (ShapeType) in.readObject();

        switch (type) {

            case CIRCLE:
                shape = BoundingShape.circle(size.getWidth() / 2);
                break;

            case POLYGON:
                shape = BoundingShape.box(size.getWidth(), size.getHeight());
                break;

            case CHAIN:
                int length = in.readInt();

                Point2D[] points = new Point2D[length];
                for (int i = 0; i < length; i++) {
                    points[i] = new Point2D(in.readDouble(), in.readDouble());
                }
                shape = BoundingShape.chain(points);
                break;

            default:
                throw new IllegalArgumentException("Unknown shape type");
        }
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

    /**
     * @return bounds
     */
    public Bounds getBounds() {
        return bounds;
    }

    /**
     * @return hit box shape
     */
    public BoundingShape getShape() {
        return shape;
    }

    /**
     * @return min x
     */
    public double getMinX() {
        return bounds.getMinX();
    }

    /**
     * @return min y
     */
    public double getMinY() {
        return bounds.getMinY();
    }

    /**
     * @return maxX of internal bounds (x + width)
     */
    public double getMaxX() {
        return bounds.getMaxX();
    }

    /**
     * @return maxY of internal bounds (y + height)
     */
    public double getMaxY() {
        return bounds.getMaxY();
    }

    /**
     * @return width of this hit box
     */
    public double getWidth() {
        return bounds.getWidth();
    }

    /**
     * @return height of this hit box
     */
    public double getHeight() {
        return bounds.getHeight();
    }

    /**
     * @return hit box name
     */
    public String getName() {
        return name;
    }

    /**
     * @return center point of this hit box, local to 0,0 (top,left) of the entity
     */
    public Point2D centerLocal() {
        return new Point2D((bounds.getMinX() + bounds.getMaxX()) / 2,
                (bounds.getMinY() + bounds.getMaxY()) / 2);
    }

    /**
     * @param x position x of entity
     * @param y position y of entity
     * @return center point of this hit box in world coordinates
     */
    public Point2D centerWorld(double x, double y) {
        return centerLocal().add(x, y);
    }
}
