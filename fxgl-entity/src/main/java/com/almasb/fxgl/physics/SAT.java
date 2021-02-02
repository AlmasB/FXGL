/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics;

import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.entity.components.TransformComponent;

import static com.almasb.fxgl.core.math.FXGLMath.*;

/**
 * Separating Axis Theorem based check for collision.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public final class SAT {

    private SAT() {}

    // there can be only 2 axes per angle, hence 2 * 2 = 4
    private static final Vec2[] axes = new Vec2[4];

    // each hit box has 4 corners
    private static final Vec2[] corners1 = new Vec2[4];
    private static final Vec2[] corners2 = new Vec2[4];

    private static final MinMax box2axis1 = new MinMax();
    private static final MinMax box2axis2 = new MinMax();
    private static final MinMax box1axis3 = new MinMax();
    private static final MinMax box1axis4 = new MinMax();

    static {
        // 4 because all above arrays have exactly 4 elements
        for (int i = 0; i < 4; i++) {
            axes[i] = new Vec2();
            corners1[i] = new Vec2();
            corners2[i] = new Vec2();
        }
    }
    
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
        populateAxes(angle1, angle2);

        corners(box1, angle1, t1, corners1);
        corners(box2, angle2, t2, corners2);

        boolean result = true;

        for (Vec2 axis : axes) {
            float e1Min = getMin(corners1, axis);
            float e1Max = getMax(corners1, axis);

            float e2Min = getMin(corners2, axis);
            float e2Max = getMax(corners2, axis);

            if (e1Max < e2Min || e2Max < e1Min) {
                result = false;
                break;
            }
        }

        return result;
    }

    private static void populateAxes(double angle1, double angle2) {
        // first object
        axes[0].set(cosDegF(angle1), sinDegF(angle1)).normalizeLocal();
        axes[1].set(cosDegF(angle1 + 90), sinDegF(angle1 + 90)).normalizeLocal();

        // second object
        axes[2].set(cosDegF(angle2), sinDegF(angle2)).normalizeLocal();
        axes[3].set(cosDegF(angle2 + 90), sinDegF(angle2 + 90)).normalizeLocal();
    }

    private static void corners(HitBox box, double angle, TransformComponent t, Vec2[] array) {
        var origin = t.getRotationOrigin();

        // origin in world coord
        double originX = origin.getX() + t.getX();
        double originY = origin.getY() + t.getY();

        // top left
        array[0].set((float) box.getMinXWorld(), (float) box.getMinYWorld());

        // top right
        array[1].set((float) box.getMaxXWorld(), (float) box.getMinYWorld());

        // bot right
        array[2].set((float) box.getMaxXWorld(), (float) box.getMaxYWorld());

        // bot left
        array[3].set((float) box.getMinXWorld(), (float) box.getMaxYWorld());

        // min, max are already scaled inside HitBox, so we just need to rotate them
        float cos = cosDegF(angle);
        float sin = sinDegF(angle);

        for (Vec2 v : array) {
            v.subLocal(originX, originY);
            v.set(v.x * cos - v.y * sin, v.x * sin + v.y * cos);
            v.addLocal(originX, originY);
        }
    }

    private static float getMin(Vec2[] arrayCorners, Vec2 axis) {
        float min = Float.MAX_VALUE;

        for (Vec2 corner : arrayCorners) {
            float value = Vec2.dot(corner, axis);
            if (value < min)
                min = value;
        }

        return min;
    }

    private static float getMax(Vec2[] arrayCorners, Vec2 axis) {
        float max = Integer.MIN_VALUE;

        for (Vec2 corner : arrayCorners) {
            float value = Vec2.dot(corner, axis);
            if (value > max)
                max = value;
        }

        return max;
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
