/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics;

import com.almasb.fxgl.physics.box2d.collision.shapes.ShapeType;
import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;

/**
 * Defines bounding shapes to be used for hit boxes.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public final class BoundingShape {

    final ShapeType type;
    final Object data;
    final Dimension2D size;

    private BoundingShape(ShapeType type, Object data, Dimension2D size) {
        this.type = type;
        this.data = data;
        this.size = size;
    }

    /**
     * @return 2d size of this bounding shape
     */
    Dimension2D getSize() {
        return size;
    }

    /**
     * @return true if the type of this shape is a circle
     */
    public boolean isCircle() {
        return type == ShapeType.CIRCLE;
    }

    /**
     * @return true if the type of this shape is a rectangle/square
     */
    public boolean isRectangle() {
        return type == ShapeType.POLYGON;
    }

    /**
     * Constructs new circular bounding shape with given radius.
     *
     * @param radius circle radius
     * @return circular bounding shape
     */
    public static BoundingShape circle(double radius) {
        return new BoundingShape(ShapeType.CIRCLE, new Dimension2D(radius * 2, radius * 2), new Dimension2D(radius * 2, radius * 2));
    }

    /**
     * Constructs new rectangular bounding shape with given width and height.
     *
     * @param width box width
     * @param height box height
     * @return rectangular bounding shape
     */
    public static BoundingShape box(double width, double height) {
        return new BoundingShape(ShapeType.POLYGON, new Dimension2D(width, height), new Dimension2D(width, height));
    }

    /**
     * Constructs new closed chain shaped bounding shape.
     * Note: chain shape can only be used with static objects.
     * Note: chain shape must have at least 2 points
     *
     * @param points points to use in a chain
     * @return closed chain bounding shape
     * @throws IllegalArgumentException if number of points is less than 2
     */
    public static BoundingShape chain(Point2D... points) {
        if (points.length < 2)
            throw new IllegalArgumentException("Chain shape requires at least 2 points. Given points: " + points.length);

        double maxX = points[0].getX();
        double maxY = points[0].getY();

        for (Point2D p : points) {
            if (p.getX() > maxX) {
                maxX = p.getX();
            }

            if (p.getY() > maxY) {
                maxY = p.getY();
            }
        }

        return new BoundingShape(ShapeType.CHAIN, points, new Dimension2D(maxX, maxY));
    }

    public static BoundingShape polygon(Point2D... points) {
        if (points.length < 3)
            throw new IllegalArgumentException("Polygon shape requires at least 3 points. Given points: " + points.length);

        double maxX = points[0].getX();
        double maxY = points[0].getY();

        for (Point2D p : points) {
            if (p.getX() > maxX) {
                maxX = p.getX();
            }

            if (p.getY() > maxY) {
                maxY = p.getY();
            }
        }

        return new BoundingShape(ShapeType.POLYGON, points, new Dimension2D(maxX, maxY));
    }

    @Override
    public String toString() {
        return type.toString();
    }
}
