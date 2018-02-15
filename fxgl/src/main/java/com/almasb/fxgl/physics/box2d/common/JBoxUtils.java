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

import com.almasb.fxgl.core.math.Vec2;

/**
 * A few math methods that don't fit very well anywhere else.
 */
public class JBoxUtils {
    public static final float PI = (float) Math.PI;
    public static final float TWOPI = (float) (Math.PI * 2);
    public static final float HALF_PI = PI / 2;

    public static final float[] sinLUT = new float[JBoxSettings.SINCOS_LUT_LENGTH];

    static {
        for (int i = 0; i < JBoxSettings.SINCOS_LUT_LENGTH; i++) {
            sinLUT[i] = (float) Math.sin(i * JBoxSettings.SINCOS_LUT_PRECISION);
        }
    }

    public static final float sin(float x) {
        if (JBoxSettings.SINCOS_LUT_ENABLED) {
            return sinLUT(x);
        } else {
            return (float) StrictMath.sin(x);
        }
    }

    public static final float sinLUT(float x) {
        x %= TWOPI;

        if (x < 0) {
            x += TWOPI;
        }

        if (JBoxSettings.SINCOS_LUT_LERP) {

            x /= JBoxSettings.SINCOS_LUT_PRECISION;

            final int index = (int) x;

            if (index != 0) {
                x %= index;
            }

            // the next index is 0
            if (index == JBoxSettings.SINCOS_LUT_LENGTH - 1) {
                return ((1 - x) * sinLUT[index] + x * sinLUT[0]);
            } else {
                return ((1 - x) * sinLUT[index] + x * sinLUT[index + 1]);
            }

        } else {
            return sinLUT[JBoxUtils.round(x / JBoxSettings.SINCOS_LUT_PRECISION) % JBoxSettings.SINCOS_LUT_LENGTH];
        }
    }

    public static final float cos(float x) {
        if (JBoxSettings.SINCOS_LUT_ENABLED) {
            return sinLUT(HALF_PI - x);
        } else {
            return (float) StrictMath.cos(x);
        }
    }

    public static final float abs(final float x) {
        if (JBoxSettings.FAST_ABS) {
            return x > 0 ? x : -x;
        } else {
            return StrictMath.abs(x);
        }
    }

    public static final int abs(int x) {
        int y = x >> 31;
        return (x ^ y) - y;
    }

    public static final int floor(final float x) {
        if (JBoxSettings.FAST_FLOOR) {
            return fastFloor(x);
        } else {
            return (int) StrictMath.floor(x);
        }
    }

    public static final int fastFloor(final float x) {
        int y = (int) x;
        if (x < y) {
            return y - 1;
        }
        return y;
    }

    public static final int round(final float x) {
        if (JBoxSettings.FAST_ROUND) {
            return floor(x + .5f);
        } else {
            return StrictMath.round(x);
        }
    }

    public final static float max(final float a, final float b) {
        return a > b ? a : b;
    }

    public final static int max(final int a, final int b) {
        return a > b ? a : b;
    }

    public final static float min(final float a, final float b) {
        return a < b ? a : b;
    }

    public final static int min(final int a, final int b) {
        return a < b ? a : b;
    }

    public final static float map(final float val, final float fromMin, final float fromMax,
                                  final float toMin, final float toMax) {
        final float mult = (val - fromMin) / (fromMax - fromMin);
        return toMin + mult * (toMax - toMin);
    }

    /** Returns the closest value to 'a' that is in between 'low' and 'high' */
    public final static float clamp(final float a, final float low, final float high) {
        return max(low, min(a, high));
    }

    public final static Vec2 clamp(final Vec2 a, final Vec2 low, final Vec2 high) {
        final Vec2 min = new Vec2();
        min.x = a.x < high.x ? a.x : high.x;
        min.y = a.y < high.y ? a.y : high.y;
        min.x = low.x > min.x ? low.x : min.x;
        min.y = low.y > min.y ? low.y : min.y;
        return min;
    }

    public static final float atan2(final float y, final float x) {
        if (JBoxSettings.FAST_ATAN2) {
            return fastAtan2(y, x);
        } else {
            return (float) StrictMath.atan2(y, x);
        }
    }

    public static final float fastAtan2(float y, float x) {
        if (x == 0.0f) {
            if (y > 0.0f) return HALF_PI;
            if (y == 0.0f) return 0.0f;
            return -HALF_PI;
        }
        float atan;
        final float z = y / x;
        if (abs(z) < 1.0f) {
            atan = z / (1.0f + 0.28f * z * z);
            if (x < 0.0f) {
                if (y < 0.0f) return atan - PI;
                return atan + PI;
            }
        } else {
            atan = HALF_PI - z / (z * z + 0.28f);
            if (y < 0.0f) return atan - PI;
        }
        return atan;
    }

    public static final float sqrt(float x) {
        return (float) StrictMath.sqrt(x);
    }

    public final static float distanceSquared(Vec2 v1, Vec2 v2) {
        float dx = (v1.x - v2.x);
        float dy = (v1.y - v2.y);
        return dx * dx + dy * dy;
    }

    public final static float distance(Vec2 v1, Vec2 v2) {
        return sqrt(distanceSquared(v1, v2));
    }
}