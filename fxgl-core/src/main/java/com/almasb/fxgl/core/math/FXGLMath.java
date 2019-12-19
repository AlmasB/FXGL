/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core.math;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.Optional;
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

    public static final float PI_F = (float) Math.PI;
    public static final float PI2_F = (float) (Math.PI * 2);
    public static final float HALF_PI_F = PI_F / 2;

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

    private static Random random = new Random();

    public static void setRandom(Random random) {
        FXGLMath.random = random;
    }

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
        return new Random(seed);
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
     * @param start start value
     * @param end end value
     * @return a random number between start (inclusive) and end (inclusive)
     */
    public static long random(long start, long end) {
        return start + (long) (random.nextDouble() * (end - start));
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
     * @return random number between 0.0 (inclusive) and 1.0 (exclusive)
     */
    public static double randomDouble() {
        return random.nextDouble();
    }

    /**
     * @return random number between 0.0 (inclusive) and 1.0 (exclusive)
     */
    public static float randomFloat() {
        return random.nextFloat();
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
        return randomDouble() < chance;
    }

    /**
     * @return random sign, either -1 or 1
     */
    public static int randomSign() {
        return 1 | (random.nextInt() >> 31);
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
     * @return new random vector of unit length as Point2D
     */
    public static Point2D randomPoint2D() {
        return randomVec2().toPoint2D();
    }

    /**
     * @return new random vector of unit length as Vec2
     */
    public static Vec2 randomVec2() {
        return new Vec2(random(-1.0, 1.0), random(-1.0, 1.0)).normalizeLocal();
    }

    public static Color randomColor() {
        return Color.color(randomDouble(), randomDouble(), randomDouble());
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
     * Map value of a given range to a target range.
     *
     * @param value the value to map
     * @return mapped value
     */
    public static double map(double value, double currentRangeStart, double currentRangeStop, double targetRangeStart, double targetRangeStop) {
        return targetRangeStart + (targetRangeStop - targetRangeStart) * ((value - currentRangeStart) / (currentRangeStop - currentRangeStart));
    }

    public static float abs(float value) {
        return value > 0 ? value : -value;
    }

    public static double abs(double value) {
        return value > 0 ? value : -value;
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
     * @return a value in [-1,1]
     */
    public static double noise2D(double x, double y) {
        return SimplexNoise.noise2D(x, y);
    }

    /**
     * Simplex noise 3d.
     * @return a value in [-1,1]
     */
    public static double noise3D(double x, double y, double z) {
        return SimplexNoise.noise3D(x, y, z);
    }
}
