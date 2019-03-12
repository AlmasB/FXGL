/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core.math;

import com.almasb.fxgl.core.util.Optional;
import javafx.animation.Interpolator;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.Random;

/**
 * Utility and fast math functions.
 * Thanks to Riven on JavaGaming.org for the basis of sin/cos/floor/ceil.
 * Some functions also come from jbox2d.
 *
 * @author Nathan Sweet
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public final class FXGLMath {

    private FXGLMath() {}

    /**
     * A "close to zero" double epsilon value for use.
     */
    public static final double EPSILON = 1.1920928955078125E-7;

    public static final double PI = Math.PI;
    public static final double PI2 = PI * 2;
    public static final double HALF_PI = PI / 2;

    public static final double E = Math.E;

    private static final int SIN_BITS = 14; // 16KB. Adjust for accuracy.
    private static final int SIN_MASK = ~(-1 << SIN_BITS);
    private static final int SIN_COUNT = SIN_MASK + 1;

    private static final double radFull = PI * 2;
    private static final double degFull = 360;
    private static final double radToIndex = SIN_COUNT / radFull;
    private static final double degToIndex = SIN_COUNT / degFull;

    private static class Sin {
        static final double[] table = new double[SIN_COUNT];

        static {
            for (int i = 0; i < SIN_COUNT; i++)
                table[i] = Math.sin((i + 0.5f) / SIN_COUNT * radFull);

            for (int i = 0; i < 360; i += 90)
                table[(int) (i * degToIndex) & SIN_MASK] = Math.sin(toRadians(i));
        }
    }

    /**
     * @param radians angle in radians
     * @return the sine in radians from a lookup table
     */
    public static double sin(double radians) {
        return Sin.table[(int) (radians * radToIndex) & SIN_MASK];
    }

    /**
     * @param radians angle in radians
     * @return the cosine in radians from a lookup table
     */
    public static double cos(double radians) {
        return Sin.table[(int) ((radians + PI / 2) * radToIndex) & SIN_MASK];
    }

    /**
     * @param degrees angle in degrees
     * @return the sine in radians from a lookup table
     */
    public static double sinDeg(double degrees) {
        return Sin.table[(int) (degrees * degToIndex) & SIN_MASK];
    }

    /**
     * @param degrees angle in degrees
     * @return the cosine in radians from a lookup table
     */
    public static double cosDeg(double degrees) {
        return Sin.table[(int) ((degrees + 90) * degToIndex) & SIN_MASK];
    }

    private static final double radiansToDegrees = 180 / PI;

    private static final double degreesToRadians = PI / 180;

    public static double toDegrees(double radians) {
        return radiansToDegrees * radians;
    }

    public static double toRadians(double degrees) {
        return degreesToRadians * degrees;
    }

    /**
     * Average error of 0.00231 radians (0.1323 degrees),
     * largest error of 0.00488 radians (0.2796 degrees).
     *
     * @param y y component
     * @param x x component
     * @return atan2 in radians, faster but less accurate than Math.atan2
     */
    public static double atan2(double y, double x) {
        if (x == 0.0) {
            if (y > 0) return HALF_PI;
            if (y == 0.0) return 0.0;
            return -HALF_PI;
        }

        final double atan;
        final double z = y / x;

        if (Math.abs(z) < 1) {
            atan = z / (1 + 0.28 * z * z);
            if (x < 0) return atan + (y < 0 ? -PI : PI);
            return atan;
        }

        atan = HALF_PI - z / (z * z + 0.28);
        return y < 0 ? atan - PI : atan;
    }

    /**
     * Average error of 0.00231 radians (0.1323 degrees),
     * largest error of 0.00488 radians (0.2796 degrees).
     *
     * @param y y component
     * @param x x component
     * @return atan2 in degrees, faster but less accurate than Math.atan2
     */
    public static double atan2Deg(double y, double x) {
        return toDegrees(atan2(y, x));
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
     * @return object used to generate random sequences using given seed
     */
    public static Random getRandom(long seed) {
        return new RandomXS128(seed);
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
    public static boolean randomBoolean(double chance) {
        return random() < chance;
    }

    /**
     * @return random number between 0.0 (inclusive) and 1.0 (exclusive)
     */
    public static double random() {
        return random.nextDouble();
    }

    /**
     * @return random number between 0.0 (inclusive) and 1.0 (exclusive)
     */
    public static float randomFloat() {
        return random.nextFloat();
    }

    /**
     * @param range end exclusive value
     * @return a random number between 0 (inclusive) and the specified value (exclusive)
     */
    public static double random(double range) {
        return random.nextDouble() * range;
    }

    /**
     * @param start start inclusive value
     * @param end end exclusive value
     * @return a random number between start (inclusive) and end (exclusive)
     */
    public static double random(double start, double end) {
        return start + random.nextDouble() * (end - start);
    }

    /**
     * @return random sign, either -1 or 1
     */
    public static int randomSign() {
        return 1 | (random.nextInt() >> 31);
    }

    /**
     * @param min the lower limit
     * @param max the upper limit
     * @param mode the point around which the values are more likely
     * @return a triangularly distributed random number between {@code min} (inclusive) and {@code max} (exclusive),
     * where values around {@code mode} are more likely
     */
    public static double randomTriangular(double min, double max, double mode) {
        double u = random.nextDouble();
        double d = max - min;

        if (u <= (mode - min) / d)
            return min + Math.sqrt(u * d * (mode - min));

        return max - Math.sqrt((1 - u) * d * (max - mode));
    }

    /**
     * @return random point within given bounds (minX <= x < maxX, minY <= y < maxY)
     */
    public static Point2D randomPoint(Rectangle2D bounds) {
        return new Point2D(
                random(bounds.getMinX(), bounds.getMaxX()),
                random(bounds.getMinY(), bounds.getMaxY())
        );
    }

    /**
     * @return new random vector of unit length as Vec2
     */
    public static Vec2 randomVec2() {
        return new Vec2(random(-1.0, 1.0), random(-1.0, 1.0)).normalizeLocal();
    }

    /**
     * @return new random vector of unit length as Point2D
     */
    public static Point2D randomPoint2D() {
        double x = random(-1.0, 1.0);
        double y = random(-1.0, 1.0);

        double length = Math.sqrt(x * x + y * y);
        if (length < EPSILON)
            return Point2D.ZERO;

        return new Point2D(x / length, y / length);
    }

    public static Color randomColor() {
        return Color.color(random(), random(), random());
    }

    /**
     * @return random element of the given array or Optional.empty() if empty
     */
    public static <T> Optional<T> random(T[] array) {
        if (array.length == 0)
            return Optional.empty();

        return Optional.of(array[random(0, array.length - 1)]);
    }

    /**
     * @return random element of the given list or Optional.empty() if empty
     */
    public static <T> Optional<T> random(List<T> list) {
        if (list.isEmpty())
            return Optional.empty();

        return Optional.of(list.get(random(0, list.size() - 1)));
    }

    /* RANDOM END */

    public static double sqrt(double x) {
        return Math.sqrt(x);
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
     * Map value of a given range to a target range.
     *
     * @param value the value to map
     * @return mapped value
     */
    public static double map(double value, double currentRangeStart, double currentRangeStop, double targetRangeStart, double targetRangeStop) {
        return targetRangeStart + (targetRangeStop - targetRangeStart) * ((value - currentRangeStart) / (currentRangeStop - currentRangeStart));
    }

    public static double interpolate(double fromValue, double toValue, double progress, Interpolator interpolator) {
        return interpolator.interpolate(fromValue, toValue, progress);
    }

    public static Point2D interpolate(Point2D fromValue, Point2D toValue, double progress, Interpolator interpolator) {
        double x = interpolate(fromValue.getX(), toValue.getX(), progress, interpolator);
        double y = interpolate(fromValue.getY(), toValue.getY(), progress, interpolator);

        return new Point2D(x, y);
    }

    private static final int BIG_ENOUGH_INT = 16 * 1024;
    private static final double BIG_ENOUGH_FLOOR = BIG_ENOUGH_INT;
    private static final double CEIL = 0.9999999;
    private static final double BIG_ENOUGH_CEIL = 16384.999999999996;
    private static final double BIG_ENOUGH_ROUND = BIG_ENOUGH_INT + 0.5f;

    public static float abs(float value) {
        return value > 0 ? value : -value;
    }

    public static double abs(double value) {
        return value > 0 ? value : -value;
    }

    public static double min(double a, double b) {
        return (a <= b) ? a : b;
    }

    public static double max(double a, double b) {
        return (a >= b) ? a : b;
    }

    /**
     * @param value from -(2^14) to (double.MAX_VALUE - 2^14)
     * @return the largest integer less than or equal to the specified double
     */
    public static int floor(double value) {
        return (int) (value + BIG_ENOUGH_FLOOR) - BIG_ENOUGH_INT;
    }

    /**
     * Note: this method simply casts the double to int.
     *
     * @param value a positive value
     * @return the largest integer less than or equal to the specified double
     */
    public static int floorPositive(double value) {
        return (int) value;
    }

    /**
     * @param value from -(2^14) to (double.MAX_VALUE - 2^14)
     * @return the smallest integer greater than or equal to the specified double
     */
    public static int ceil(double value) {
        return (int) (value + BIG_ENOUGH_CEIL) - BIG_ENOUGH_INT;
    }

    /**
     * @param value a positive double
     * @return the smallest integer greater than or equal to the specified double
     */
    public static int ceilPositive(double value) {
        return (int) (value + CEIL);
    }

    /**
     * @param value from -(2^14) to (double.MAX_VALUE - 2^14)
     * @return the closest integer to the specified double
     */
    public static int round(double value) {
        return (int) (value + BIG_ENOUGH_ROUND) - BIG_ENOUGH_INT;
    }

    /**
     * @param value a positive double
     * @return the closest integer to the specified double
     */
    public static int roundPositive(double value) {
        return (int) (value + 0.5);
    }

    /**
     * Note: prone to floating point errors if the arguments are not doubles.
     *
     * @param value the value to check
     * @param tolerance represent an upper bound below which the value is considered zero
     * @return true if the value is close to zero, i.e. true if difference between value and 0 is less than or equal to tolerance
     */
    public static boolean isCloseToZero(double value, double tolerance) {
        return Math.abs(value) <= tolerance;
    }

    public static Point2D bezier(Point2D p1, Point2D p2, Point2D p3, double t) {
        double x = (1 - t) * (1 - t) * p1.getX() + 2 * (1 - t) * t * p2.getX() + t * t * p3.getX();
        double y = (1 - t) * (1 - t) * p1.getY() + 2 * (1 - t) * t * p2.getY() + t * t * p3.getY();

        return new Point2D(x, y);
    }

    public static Point2D bezier(Point2D p1, Point2D p2, Point2D p3, Point2D p4, double t) {
        double x = Math.pow(1 - t, 3) * p1.getX() + 3 * t * Math.pow(1 - t, 2) * p2.getX() + 3 * t*t * (1 - t) * p3.getX() + t*t*t*p4.getX();
        double y = Math.pow(1 - t, 3) * p1.getY() + 3 * t * Math.pow(1 - t, 2) * p2.getY() + 3 * t*t * (1 - t) * p3.getY() + t*t*t*p4.getY();

        return new Point2D(x, y);
    }

    /**
     * @param t current time * frequency (lower frequency -> smoother output)
     * @return perlin noise in 1D quality in [0..1)
     */
    public static double noise1D(double t) {
        return PerlinNoiseGenerator.INSTANCE.noise1D(t) + 0.5;
    }

    /**
     * A typical usage would be to pass 2d coordinates multiplied by a frequency (lower frequency -> smoother output) value, like:
     *
     * double noise = noise2D(x * freq, y * freq)
     *
     * @return perlin noise in 2D quality in [0..1)
     * @implNote twice slower than noise1D
     */
    public static double noise2D(double x, double y) {

        double noise = PerlinNoiseGenerator.INSTANCE.noise2D(x, y) + 0.5;

        // https://github.com/AlmasB/FXGL/issues/479
        if (noise < 0)
            return 0;

        if (noise >= 1)
            return 0.99999999;

        return noise;
    }

    /**
     * Simplex noise 3d.
     * @return a value in [-1,1]
     */
    public static double noise3D(double x, double y, double z) {
        return SimplexNoise.noise(x, y, z);
    }
}
