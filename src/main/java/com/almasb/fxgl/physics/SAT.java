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

package com.almasb.fxgl.physics;

import com.almasb.fxgl.app.FXGL;
import com.almasb.gameutils.collection.Array;
import com.almasb.gameutils.math.Vec2;

/**
 * Separating Axis Theorem based check for collision.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public final class SAT {

    private SAT() {}

    // there can be only 2 axes per angle, hence 2 * 2 = 4
    private static final Array<Vec2> axes = new Array<>(false, 4);

    // each hit box has 4 corners
    private static final Array<Vec2> corners1 = new Array<>(false, 4);
    private static final Array<Vec2> corners2 = new Array<>(false, 4);

    /**
     * Note: NOT thread-safe but GC-friendly.
     *
     * @param box1 hit box 1
     * @param box2 hit box 2
     * @param angle1 angle of hit box 1
     * @param angle2 angle of hit box 2
     * @return true if two hit boxes with respective angles are colliding
     */
    public static boolean isColliding(HitBox box1, HitBox box2, double angle1, double angle2) {
        populateAxes(angle1);
        populateAxes(angle2);

        corners(box1, angle1, corners1);
        corners(box2, angle2, corners2);

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

        cleanArrays();

        return result;
    }

    private static void cleanArrays() {
        for (Vec2 v : axes)
            freeVec(v);

        for (Vec2 v : corners1)
            freeVec(v);

        for (Vec2 v : corners2)
            freeVec(v);

        axes.clear();
        corners1.clear();
        corners2.clear();
    }

    private static void populateAxes(double angle) {
        axes.add(newVec(cos(angle), sin(angle)).normalizeLocal());
        axes.add(newVec(cos(angle + 90), sin(angle + 90)).normalizeLocal());
    }

    private static Vec2 center(HitBox box) {
        return newVec(box.getMinXWorld() + box.getWidth() / 2,
                box.getMinYWorld() + box.getHeight() / 2);
    }

    private static void corners(HitBox box, double angle, Array<Vec2> array) {
        Vec2 center = center(box);

        Vec2 topLeft = newVec(box.getMinXWorld(), box.getMinYWorld());
        Vec2 topRight = newVec(box.getMaxXWorld(), box.getMinYWorld());
        Vec2 botRight = newVec(box.getMaxXWorld(), box.getMaxYWorld());
        Vec2 botLeft = newVec(box.getMinXWorld(), box.getMaxYWorld());

        array.addAll(topLeft, topRight, botRight, botLeft);

        for (Vec2 v : array) {
            double cos = cos(angle);
            double sin = sin(angle);

            v.subLocal(center);
            v.set((float)(v.x * cos - v.y * sin), (float)(v.x * sin + v.y * cos));
            v.addLocal(center);
        }

        freeVec(center);
    }

    private static float getMin(Array<Vec2> arrayCorners, Vec2 axis) {
        float min = Float.MAX_VALUE;

        for (Vec2 corner : arrayCorners) {
            float value = Vec2.dot(corner, axis);
            if (value < min)
                min = value;
        }

        return min;
    }

    private static float getMax(Array<Vec2> arrayCorners, Vec2 axis) {
        float max = Integer.MIN_VALUE;

        for (Vec2 corner : arrayCorners) {
            float value = Vec2.dot(corner, axis);
            if (value > max)
                max = value;
        }

        return max;
    }

    private static Vec2 newVec(double x, double y) {
        return FXGL.getPooler()
                .get(Vec2.class)
                .set((float)x, (float)y);
    }

    private static void freeVec(Vec2 vec) {
        FXGL.getPooler().put(vec);
    }

    private static double cos(double angle) {
        return Math.cos(Math.toRadians(angle));
    }

    private static double sin(double angle) {
        return Math.sin(Math.toRadians(angle));
    }
}
