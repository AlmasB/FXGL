/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package com.almasb.fxgl.physics;

import com.almasb.fxgl.physics.box2d.collision.shapes.ShapeType;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;

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
     * Creates a hit box with the given shape.
     * The name of the {@code {@link HitBox}} will be auto generated
     * based upon the {@code hashCode} of the given {@code {@link BoundingShape}}.
     *
     * @param shape bounding shape
     */
    public HitBox(BoundingShape shape) {
        this(String.valueOf(shape.hashCode()), shape);
    }

    /**
     * Creates a hit box with the given local origin and shape.
     * The name of the {@code {@link HitBox}} will be auto generated
     * based upon the {@code hashCode} of the given {@code {@link BoundingShape}}.
     *
     * @param localOrigin origin of hit box
     * @param shape bounding shape
     */
    public HitBox(Point2D localOrigin, BoundingShape shape){
        this(String.valueOf(shape.hashCode()), localOrigin, shape);
    }

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

    /*
     * These are lazily evaluated and used for caching
     * bbox data in the world coord space.
     * Otherwise these would have to be recomputed every time
     * this hit box is used for collision detection.
     */
    private transient DoubleProperty minXWorld = new SimpleDoubleProperty();
    private transient DoubleProperty maxXWorld = new SimpleDoubleProperty();
    private transient DoubleProperty minYWorld = new SimpleDoubleProperty();
    private transient DoubleProperty maxYWorld = new SimpleDoubleProperty();

    /**
     * Bind to x property of entity.
     *
     * @param xProperty x property
     */
    public void bindX(DoubleProperty xProperty) {
        minXWorld.bind(xProperty.add(getMinX()));
        maxXWorld.bind(minXWorld.add(getWidth()));
    }

    /**
     * Bind to y property of entity.
     *
     * @param yProperty y property
     */
    public void bindY(DoubleProperty yProperty) {
        minYWorld.bind(yProperty.add(getMinY()));
        maxYWorld.bind(minYWorld.add(getHeight()));
    }

    /**
     * Unbind the hit box.
     */
    public void unbind() {
        minXWorld.unbind();
        maxXWorld.unbind();
        minYWorld.unbind();
        maxYWorld.unbind();
    }

    public double getMinXWorld() {
        return minXWorld.get();
    }

    public double getMaxXWorld() {
        return maxXWorld.get();
    }

    public double getMinYWorld() {
        return minYWorld.get();
    }

    public double getMaxYWorld() {
        return maxYWorld.get();
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

    public HitBox copy() {
        return new HitBox(name, new Point2D(bounds.getMinX(), bounds.getMinY()), shape);
    }

    @Override
    public String toString() {
        return "HitBox(" + name + "," + shape + ")";
    }
}
