/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics;

import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.entity.components.TransformComponent;

/**
 * Separating Axis Theorem based check for collision.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public final class SAT {

    private SAT() {}

    private static final MinMax box2axis1 = new MinMax();
    private static final MinMax box2axis2 = new MinMax();
    private static final MinMax box1axis3 = new MinMax();
    private static final MinMax box1axis4 = new MinMax();

    /**
     * Note: NOT thread-safe but GC-friendly.
     *
     * @param box1 hit box 1
     * @param box2 hit box 2
     * @param angle1 angle of hit box 1
     * @param angle2 angle of hit box 2
     * @param t1 transform of hit box 1
     * @param t2 transform of hit box 2
     * @return true if two hit boxes are colliding
     */
    public static boolean isColliding(HitBox box1, HitBox box2, double angle1, double angle2,
                                      TransformComponent t1, TransformComponent t2) {

        Vec2 axis1 = box1.axes[0];
        Vec2 axis2 = box1.axes[1];
        Vec2 axis3 = box2.axes[0];
        Vec2 axis4 = box2.axes[1];

        computeMinMax(box2.corners, axis1, box2axis1);

        if (box1.axis1MinMax.isSeparated(box2axis1))
            return false;

        computeMinMax(box2.corners, axis2, box2axis2);

        if (box1.axis2MinMax.isSeparated(box2axis2))
            return false;

        computeMinMax(box1.corners, axis3, box1axis3);

        if (box2.axis1MinMax.isSeparated(box1axis3))
            return false;

        computeMinMax(box1.corners, axis4, box1axis4);

        if (box2.axis2MinMax.isSeparated(box1axis4))
            return false;

        return true;
    }

    /**
     * Calculate a projection of corners to an axis and populate the minMax object.
     *
     * @param corners a 4-value array defining corner points
     * @param axis the axis on which to project corners
     * @param minMax the storage object
     */
    static void computeMinMax(Vec2[] corners, Vec2 axis, MinMax minMax) {
        float value = Vec2.dot(corners[0], axis);

        float min = value;
        float max = value;

        for (int i = 1; i < 4; i++) {
            value = Vec2.dot(corners[i], axis);

            min = Math.min(min, value);
            max = Math.max(max, value);
        }

        minMax.min = min;
        minMax.max = max;
    }

    /**
     * Data structure for min-max values on an axis.
     */
    public static class MinMax {
        private float min = 0;
        private float max = 0;

        /**
         * @param other another min-max (assumed on the same axis)
         * @return true if this min-max is separated from other
         */
        private boolean isSeparated(MinMax other) {
            return max < other.min || other.max < min;
        }
    }
}
