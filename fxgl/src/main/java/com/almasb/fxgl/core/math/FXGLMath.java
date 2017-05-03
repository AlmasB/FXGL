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

/**
 * Origin: libGDX.
 */

package com.almasb.fxgl.core.math;

import javafx.animation.Interpolatable;
import javafx.animation.Interpolator;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

import java.util.Random;

/**
 * Utility and fast math functions.
 * Thanks to Riven on JavaGaming.org for the basis of sin/cos/floor/ceil.
 *
 * @author Nathan Sweet
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public final class FXGLMath {

    private FXGLMath() {}

    public static final float nanoToSec = 1 / 1000000000f;

    /**
     * A "close to zero" float epsilon value for use.
     */
    public static final float EPSILON = 1.1920928955078125E-7f;

    public static final float FLOAT_ROUNDING_ERROR = 0.000001f; // 32 bits
    public static final float PI = 3.1415927f;
    public static final float PI2 = PI * 2;

    public static final float E = 2.7182818f;

    private static final int SIN_BITS = 14; // 16KB. Adjust for accuracy.
    private static final int SIN_MASK = ~(-1 << SIN_BITS);
    private static final int SIN_COUNT = SIN_MASK + 1;

    private static final float radFull = PI * 2;
    private static final float degFull = 360;
    private static final float radToIndex = SIN_COUNT / radFull;
    private static final float degToIndex = SIN_COUNT / degFull;

    /** multiply by this to convert from radians to degrees */
    public static final float radiansToDegrees = 180f / PI;
    public static final float radDeg = radiansToDegrees;

    /** multiply by this to convert from degrees to radians */
    public static final float degreesToRadians = PI / 180;
    public static final float degRad = degreesToRadians;

    private static class Sin {
        static final float[] table = new float[SIN_COUNT];

        static {
            for (int i = 0; i < SIN_COUNT; i++)
                table[i] = (float) Math.sin((i + 0.5f) / SIN_COUNT * radFull);
            for (int i = 0; i < 360; i += 90)
                table[(int) (i * degToIndex) & SIN_MASK] = (float) Math.sin(i * degreesToRadians);
        }
    }

    /**
     * @param radians angle in radians
     * @return the sine in radians from a lookup table
     */
    public static float sin(float radians) {
        return Sin.table[(int) (radians * radToIndex) & SIN_MASK];
    }

    /**
     * @param radians angle in radians
     * @return the cosine in radians from a lookup table
     */
    public static float cos(float radians) {
        return Sin.table[(int) ((radians + PI / 2) * radToIndex) & SIN_MASK];
    }

    /**
     * @param degrees angle in degrees
     * @return the sine in radians from a lookup table
     */
    public static float sinDeg(float degrees) {
        return Sin.table[(int) (degrees * degToIndex) & SIN_MASK];
    }

    /**
     * @param degrees angle in degrees
     * @return the cosine in radians from a lookup table
     */
    public static float cosDeg(float degrees) {
        return Sin.table[(int) ((degrees + 90) * degToIndex) & SIN_MASK];
    }

    /**
     * Average error of 0.00231 radians (0.1323 degrees),
     * largest error of 0.00488 radians (0.2796 degrees).
     *
     * @param y y component
     * @param x x component
     * @return atan2 in radians, faster but less accurate than Math.atan2.
     */
    public static float atan2(float y, float x) {
        if (x == 0f) {
            if (y > 0f) return PI / 2;
            if (y == 0f) return 0f;
            return -PI / 2;
        }
        final float atan, z = y / x;
        if (Math.abs(z) < 1f) {
            atan = z / (1f + 0.28f * z * z);
            if (x < 0f) return atan + (y < 0f ? -PI : PI);
            return atan;
        }
        atan = PI / 2 - z / (z * z + 0.28f);
        return y < 0f ? atan - PI : atan;
    }

    /**
     * Average error of 0.00231 radians (0.1323 degrees),
     * largest error of 0.00488 radians (0.2796 degrees).
     *
     * @param y y component
     * @param x x component
     * @return atan2 in radians, faster but less accurate than Math.atan2.
     */
    public static double atan2(double y, double x) {
        return atan2((float) y, (float) x);
    }

    /* RANDOM BEGIN */

    private static final Random random = new RandomXS128();

    /**
     * @return random object used to generate random sequences
     */
    public static Random getRandom() {
        return random;
    }

    /**
     * @param range the end inclusive value
     * @return a random number between 0 (inclusive) and the specified value (inclusive)
     */
    public static int random(int range) {
        return random.nextInt(range + 1);
    }

    /**
     * @param start start value
     * @param end end value
     * @return a random number between start (inclusive) and end (inclusive)
     */
    public static int random(int start, int end) {
        return start + random.nextInt(end - start + 1);
    }

    /**
     * @param range the end inclusive value
     * @return a random number between 0 (inclusive) and the specified value (inclusive)
     */
    public static long random(long range) {
        return (long) (random.nextDouble() * range);
    }

    /**
     * @param start start value
     * @param end end value
     * @return a random number between start (inclusive) and end (inclusive)
     */
    public static long random(long start, long end) {
        return start + (long) (random.nextDouble() * (end - start));
    }

    /**
     * @return random boolean value
     */
    public static boolean randomBoolean() {
        return random.nextBoolean();
    }

    /**
     * @param chance chance to check
     * @return true if a random value between 0 and 1 is less than the specified value
     */
    public static boolean randomBoolean(float chance) {
        return random() < chance;
    }

    /**
     * @return random number between 0.0 (inclusive) and 1.0 (exclusive)
     */
    public static float random() {
        return random.nextFloat();
    }

    /**
     * @param range end exclusive value
     * @return a random number between 0 (inclusive) and the specified value (exclusive)
     */
    public static float random(float range) {
        return random.nextFloat() * range;
    }

    /**
     * @param start start inclusive value
     * @param end end exclusive value
     * @return a random number between start (inclusive) and end (exclusive)
     */
    public static float random(float start, float end) {
        return start + random.nextFloat() * (end - start);
    }

    /**
     * @return random sign, either -1 or 1
     */
    public static int randomSign() {
        return 1 | (random.nextInt() >> 31);
    }

    /**
     * This is an optimized version of {@link #randomTriangular(float, float, float) randomTriangular(-1, 1, 0)}.
     *
     * @return a triangularly distributed random number between -1.0 (exclusive) and 1.0 (exclusive),
     * where values around zero are more likely
     */
    public static float randomTriangular() {
        return random.nextFloat() - random.nextFloat();
    }

    /**
     * This is an optimized version of {@link #randomTriangular(float, float, float) randomTriangular(-max, max, 0)}.
     *
     * @param max the upper limit
     * @return a triangularly distributed random number between {@code -max} (exclusive) and {@code max} (exclusive),
     * where values around zero are more likely
     */
    public static float randomTriangular(float max) {
        return (random.nextFloat() - random.nextFloat()) * max;
    }

    /**
     * This method is equivalent of {@link #randomTriangular(float, float, float) randomTriangular(min, max, (min + max) * .5f)}.
     *
     * @param min the lower limit
     * @param max the upper limit
     * @return a triangularly distributed random number between {@code min} (inclusive) and {@code max} (exclusive),
     * where the {@code mode} argument defaults to the midpoint between the bounds, giving a symmetric distribution
     */
    public static float randomTriangular(float min, float max) {
        return randomTriangular(min, max, (min + max) * 0.5f);
    }

    /**
     * @param min the lower limit
     * @param max the upper limit
     * @param mode the point around which the values are more likely
     * @return a triangularly distributed random number between {@code min} (inclusive) and {@code max} (exclusive),
     * where values around {@code mode} are more likely
     */
    public static float randomTriangular(float min, float max, float mode) {
        float u = random.nextFloat();
        float d = max - min;

        if (u <= (mode - min) / d)
            return min + (float) Math.sqrt(u * d * (mode - min));

        return max - (float) Math.sqrt((1 - u) * d * (max - mode));
    }

    /**
     * @return new random vector of unit length as Vec2
     */
    public static Vec2 randomVec2() {
        return new Vec2(random(-1f, 1f), random(-1f, 1f)).normalizeLocal();
    }

    /**
     * @return new random vector of unit length as Point2D
     */
    public static Point2D randomPoint2D() {
        double x = random(-1f, 1f);
        double y = random(-1f, 1f);

        double length = Math.sqrt(x * x + y * y);
        if (length < EPSILON)
            return Point2D.ZERO;

        return new Point2D(x / length, y / length);
    }

    public static Color randomColor() {
        return Color.color(random(), random(), random());
    }

    /* RANDOM END */

    public static float sqrt(float x) {
        return (float) StrictMath.sqrt(x);
    }

    /**
     * @param value the value
     * @return the next power of two, or the specified value if the value is already a power of two
     */
    public static int nextPowerOfTwo(int value) {
        if (value == 0)
            return 1;

        value--;
        value |= value >> 1;
        value |= value >> 2;
        value |= value >> 4;
        value |= value >> 8;
        value |= value >> 16;
        return value + 1;
    }

    /**
     * @param value the value to check
     * @return true if the value is a power of two
     */
    public static boolean isPowerOfTwo(int value) {
        return value != 0 && (value & value - 1) == 0;
    }

    public static short clamp(short value, short min, short max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    public static int clamp(int value, int min, int max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    public static long clamp(long value, long min, long max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    public static float clamp(float value, float min, float max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    public static double clamp(double value, double min, double max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    /**
     * Map value of a given range to a target range.
     *
     * @param value the value to map
     * @return mapped value
     */
    public static double map(double value, double currentRangeStart, double currentRangeStop, double targetRangeStart, double targetRangeStop) {
        return targetRangeStart + (targetRangeStop - targetRangeStart) * ((value - currentRangeStart) / (currentRangeStop - currentRangeStart));
    }

    /**
     * @param fromValue start value
     * @param toValue end value
     * @param progress the interpolation progress [0..1]
     * @return linearly interpolated value between fromValue to toValue based on progress position
     */
    public static double lerp(double fromValue, double toValue, double progress) {
        return Interpolator.LINEAR.interpolate(fromValue, toValue, progress);
    }

    public static Point2D lerp(double fromX, double fromY, double toX, double toY, double progress) {
        return new Point2D(lerp(fromX, toX, progress), lerp(fromY, toY, progress));
    }

    public static double interpolate(double fromValue, double toValue, double progress, Interpolator interpolator) {
        return interpolator.interpolate(fromValue, toValue, progress);
    }

    public static Point2D interpolate(Point2D fromValue, Point2D toValue, double progress, Interpolator interpolator) {
        double x = interpolate(fromValue.getX(), toValue.getX(), progress, interpolator);
        double y = interpolate(fromValue.getY(), toValue.getY(), progress, interpolator);

        return new Point2D(x, y);
    }

    /**
     * Linearly interpolates between two angles in radians.
     * Takes into account that angles wrap at two pi and always takes the
     * direction with the smallest delta angle.
     *
     * @param fromRadians start angle in radians
     * @param toRadians target angle in radians
     * @param progress interpolation value in the range [0, 1]
     * @return the interpolated angle in the range [0, PI2[
     */
    public static float lerpAngle(float fromRadians, float toRadians, float progress) {
        float delta = ((toRadians - fromRadians + PI2 + PI) % PI2) - PI;
        return (fromRadians + delta * progress + PI2) % PI2;
    }

    /**
     * Linearly interpolates between two angles in degrees.
     * Takes into account that angles wrap at 360 degrees and always takes
     * the direction with the smallest delta angle.
     *
     * @param fromDegrees start angle in degrees
     * @param toDegrees target angle in degrees
     * @param progress interpolation value in the range [0, 1]
     * @return the interpolated angle in the range [0, 360[
     */
    public static float lerpAngleDeg(float fromDegrees, float toDegrees, float progress) {
        float delta = ((toDegrees - fromDegrees + 360 + 180) % 360) - 180;
        return (fromDegrees + delta * progress + 360) % 360;
    }

    private static final int BIG_ENOUGH_INT = 16 * 1024;
    private static final double BIG_ENOUGH_FLOOR = BIG_ENOUGH_INT;
    private static final double CEIL = 0.9999999;
    private static final double BIG_ENOUGH_CEIL = 16384.999999999996;
    private static final double BIG_ENOUGH_ROUND = BIG_ENOUGH_INT + 0.5f;

    public static float abs(final float value) {
        return value > 0 ? value : -value;
    }

    /**
     * @param value from -(2^14) to (Float.MAX_VALUE - 2^14)
     * @return the largest integer less than or equal to the specified float
     */
    public static int floor(float value) {
        return (int) (value + BIG_ENOUGH_FLOOR) - BIG_ENOUGH_INT;
    }

    /**
     * Note: this method simply casts the float to int.
     *
     * @param value a positive value
     * @return the largest integer less than or equal to the specified float
     */
    public static int floorPositive(float value) {
        return (int) value;
    }

    /**
     * @param value from -(2^14) to (Float.MAX_VALUE - 2^14)
     * @return the smallest integer greater than or equal to the specified float
     */
    public static int ceil(float value) {
        return (int) (value + BIG_ENOUGH_CEIL) - BIG_ENOUGH_INT;
    }

    /**
     * @param value a positive float
     * @return the smallest integer greater than or equal to the specified float
     */
    public static int ceilPositive(float value) {
        return (int) (value + CEIL);
    }

    /**
     * @param value from -(2^14) to (Float.MAX_VALUE - 2^14)
     * @return the closest integer to the specified float
     */
    public static int round(float value) {
        return (int) (value + BIG_ENOUGH_ROUND) - BIG_ENOUGH_INT;
    }

    /**
     * @param value a positive float
     * @return the closest integer to the specified float
     */
    public static int roundPositive(float value) {
        return (int) (value + 0.5f);
    }

    /**
     * @param value the value to check
     * @return true if the value is zero (using the default tolerance as upper bound)
     */
    public static boolean isZero(float value) {
        return Math.abs(value) <= FLOAT_ROUNDING_ERROR;
    }

    /**
     * @param value the value to check
     * @param tolerance represent an upper bound below which the value is considered zero
     * @return true if the value is zero
     */
    public static boolean isZero(float value, float tolerance) {
        return Math.abs(value) <= tolerance;
    }

    /**
     * @param a the first value
     * @param b the second value
     * @return true if a is nearly equal to b (using the default error tolerance)
     */
    public static boolean isEqual(float a, float b) {
        return Math.abs(a - b) <= FLOAT_ROUNDING_ERROR;
    }

    /**
     * @param a the first value
     * @param b the second value
     * @param tolerance represent an upper bound below which the two values are considered equal
     *
     * @return true if a is nearly equal to b
     */
    public static boolean isEqual(float a, float b, float tolerance) {
        return Math.abs(a - b) <= tolerance;
    }

    /**
     * @param base the base
     * @param value the value
     * @return the logarithm of value with given base
     */
    public static float log(float base, float value) {
        return (float) (Math.log(value) / Math.log(base));
    }

    /**
     * @param value the value
     * @return the logarithm of value with base 2
     */
    public static float log2(float value) {
        return log(2, value);
    }

    /**
     * @param points the spline passes through these points
     * @return a closed bezier spline
     */
    public static BezierSpline closedBezierSpline(Vec2[] points) {
        return ClosedBezierSplineFactory.newBezierSpline(points);
    }

    /**
     * @param t current time * frequency (lower frequency -> smoother output)
     * @return perlin noise in 1D quality in [0..1)
     */
    public static float noise1D(double t) {
        return PerlinNoiseGenerator.INSTANCE.noise1D((float) t) + 0.5f;
    }
}
