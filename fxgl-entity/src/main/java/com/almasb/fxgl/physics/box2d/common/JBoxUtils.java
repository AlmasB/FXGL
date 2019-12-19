/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
/*
 * JBox2D - A Java Port of Erin Catto's Box2D
 * 
 * JBox2D homepage: http://jbox2d.sourceforge.net/
 * Box2D homepage: http://www.box2d.org
 * 
 * This software is provided 'as-is', without any express or implied
 * warranty.  In no event will the authors be held liable for any damages
 * arising from the use of this software.
 * 
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 * 
 * 1. The origin of this software must not be misrepresented; you must not
 * claim that you wrote the original software. If you use this software
 * in a product, an acknowledgment in the product documentation would be
 * appreciated but is not required.
 * 2. Altered source versions must be plainly marked as such, and must not be
 * misrepresented as being the original software.
 * 3. This notice may not be removed or altered from any source distribution.
 */

package com.almasb.fxgl.physics.box2d.common;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.core.math.Vec2;

/**
 * A few math methods that don't fit very well anywhere else.
 */
public final class JBoxUtils {

    private static final float[] sinLUT = new float[JBoxSettings.SINCOS_LUT_LENGTH];

    static {
        for (int i = 0; i < JBoxSettings.SINCOS_LUT_LENGTH; i++) {
            sinLUT[i] = (float) Math.sin(i * JBoxSettings.SINCOS_LUT_PRECISION);
        }
    }

    public static float sin(float x) {
        return sinLUT(x);
    }

    public static float cos(float x) {
        return sinLUT((float) Math.PI / 2 - x);
    }

    public static float sinLUT(float x) {
        x %= FXGLMath.PI2_F;

        if (x < 0) {
            x += FXGLMath.PI2_F;
        }

        return sinLUT[floor(x / JBoxSettings.SINCOS_LUT_PRECISION + .5f) % JBoxSettings.SINCOS_LUT_LENGTH];
    }

    public static int floor(float x) {
        int y = (int) x;
        if (x < y) {
            return y - 1;
        }
        return y;
    }

    /** Returns the closest value to 'a' that is in between 'low' and 'high' */
    public static float clamp(float a, float low, float high) {
        return Math.max(low, Math.min(a, high));
    }

    public static float sqrt(float x) {
        return (float) StrictMath.sqrt(x);
    }

    public static float distanceSquared(Vec2 v1, Vec2 v2) {
        float dx = v1.x - v2.x;
        float dy = v1.y - v2.y;
        return dx * dx + dy * dy;
    }

    public static float distance(Vec2 v1, Vec2 v2) {
        return sqrt(distanceSquared(v1, v2));
    }

    // from Body

//    /**
//     * Get the world coordinates of a point given the local coordinates.
//     *
//     * @param localPoint a point on the body measured relative the the body's origin.
//     * @return the same point expressed in world coordinates.
//     */
//    public Vec2 getWorldPoint(Vec2 localPoint) {
//        Vec2 v = new Vec2();
//        getWorldPointToOut(localPoint, v);
//        return v;
//    }
//

//
//    /**
//     * Get the world coordinates of a vector given the local coordinates.
//     *
//     * @param localVector a vector fixed in the body.
//     * @return the same vector expressed in world coordinates.
//     */
//    public Vec2 getWorldVector(Vec2 localVector) {
//        Vec2 out = new Vec2();
//        getWorldVectorToOut(localVector, out);
//        return out;
//    }
//
//
//    /**
//     * Gets a local vector given a world vector.
//     *
//     * @param worldVector vector in world coordinates.
//     * @return the corresponding local vector.
//     */
//    public Vec2 getLocalVector(Vec2 worldVector) {
//        Vec2 out = new Vec2();
//        getLocalVectorToOut(worldVector, out);
//        return out;
//    }
//
//
//    public void getLocalVectorToOutUnsafe(Vec2 worldVector, Vec2 out) {
//        Rotation.mulTransUnsafe(m_xf.q, worldVector, out);
//    }
//
//    /**
//     * Get the world linear velocity of a world point attached to this body.
//     *
//     * @param worldPoint point in world coordinates.
//     * @return the world velocity of a point.
//     */
//    public Vec2 getLinearVelocityFromWorldPoint(Vec2 worldPoint) {
//        Vec2 out = new Vec2();
//        getLinearVelocityFromWorldPointToOut(worldPoint, out);
//        return out;
//    }
//
//    public void getLinearVelocityFromWorldPointToOut(Vec2 worldPoint, Vec2 out) {
//        float tempX = worldPoint.x - m_sweep.c.x;
//        float tempY = worldPoint.y - m_sweep.c.y;
//        out.x = -m_angularVelocity * tempY + m_linearVelocity.x;
//        out.y = m_angularVelocity * tempX + m_linearVelocity.y;
//    }
//
//    /**
//     * Get the world velocity of a local point.
//     *
//     * @param localPoint point in local coordinates.
//     * @return the world velocity of a point.
//     */
//    public Vec2 getLinearVelocityFromLocalPoint(Vec2 localPoint) {
//        Vec2 out = new Vec2();
//        getLinearVelocityFromLocalPointToOut(localPoint, out);
//        return out;
//    }
//
//    public void getLinearVelocityFromLocalPointToOut(Vec2 localPoint, Vec2 out) {
//        getWorldPointToOut(localPoint, out);
//        getLinearVelocityFromWorldPointToOut(out, out);
//    }
}