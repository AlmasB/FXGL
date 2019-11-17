/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics;

import com.almasb.fxgl.core.math.Vec2;
import javafx.geometry.Point2D;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public interface PhysicsUnitConverter {

    /**
     * Converts pixels to meters
     *
     * @param pixels value in pixels
     * @return value in meters
     */
    default float toMetersF(double pixels) {
        return (float) toMeters(pixels);
    }

    double toMeters(double pixels);

    /**
     * Converts meters to pixels
     *
     * @param meters value in meters
     * @return value in pixels
     */
    default float toPixelsF(double meters) {
        return (float) toPixels(meters);
    }

    double toPixels(double meters);

    /**
     * Converts a vector of type Point2D to vector of type Vec2
     *
     * @param v vector in pixels
     * @return vector in meters
     */
    default Vec2 toVector(Point2D v) {
        return new Vec2(toMetersF(v.getX()), toMetersF(-v.getY()));
    }

    /**
     * Converts a vector of type Vec2 to vector of type Point2D
     *
     * @param v vector in meters
     * @return vector in pixels
     */
    default Point2D toVector(Vec2 v) {
        return new Point2D(toPixels(v.x), toPixels(-v.y));
    }

    /**
     * Converts a point in pixel space to a point in physics space.
     *
     * @param p point in pixel space
     * @return point in physics space
     */
    Vec2 toPoint(Point2D p);

    /**
     * Converts a point in physics space to a point in pixel space.
     *
     * @param p point in physics space
     * @return point in pixel space
     */
    Point2D toPoint(Vec2 p);
}
