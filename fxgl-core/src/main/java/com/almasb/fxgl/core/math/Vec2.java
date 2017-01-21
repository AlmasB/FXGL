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
 * Origin: jbox2d
 */

package com.almasb.fxgl.core.math;

import com.almasb.fxgl.core.pool.Poolable;

import java.io.Serializable;

/**
 * A 2D column vector with float precision.
 * Can be used to represent a point in 2D space.
 * Can be used instead of JavaFX Point2D to avoid object allocations.
 * This is also preferred for private fields.
 */
public final class Vec2 implements Serializable, Poolable {
    private static final long serialVersionUID = 1L;

    public float x, y;

    public Vec2() {
        this(0, 0);
    }

    public Vec2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vec2(Vec2 toCopy) {
        this(toCopy.x, toCopy.y);
    }

    /**
     * Zero out this vector.
     */
    public void setZero() {
        x = 0.0f;
        y = 0.0f;
    }

    /**
     * Set this vector component-wise.
     *
     * @param x x component
     * @param y y component
     * @return this vector
     */
    public Vec2 set(float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }

    /**
     * Set this vector to another vector.
     *
     * @return this vector
     */
    public Vec2 set(Vec2 v) {
        this.x = v.x;
        this.y = v.y;
        return this;
    }

    /**
     * Return the sum of this vector and another; does not alter either one.
     *
     * @return new vector
     */
    public Vec2 add(Vec2 v) {
        return new Vec2(x + v.x, y + v.y);
    }

    /**
     * Return the difference of this vector and another; does not alter either one.
     *
     * @return new vector
     */
    public Vec2 sub(Vec2 v) {
        return new Vec2(x - v.x, y - v.y);
    }

    /**
     * Return this vector multiplied by a scalar; does not alter this vector.
     *
     * @return new vector
     */
    public Vec2 mul(float a) {
        return new Vec2(x * a, y * a);
    }

    /**
     * Return the negation of this vector; does not alter this vector.
     *
     * @return new vector
     */
    public Vec2 negate() {
        return new Vec2(-x, -y);
    }

    /**
     * Flip the vector and return it - alters this vector.
     *
     * @return this vector
     */
    public Vec2 negateLocal() {
        x = -x;
        y = -y;
        return this;
    }

    /**
     * Add another vector to this one and returns result - alters this vector.
     *
     * @return this vector
     */
    public Vec2 addLocal(Vec2 v) {
        x += v.x;
        y += v.y;
        return this;
    }

    /**
     * Adds values to this vector and returns result - alters this vector.
     *
     * @return this vector
     */
    public Vec2 addLocal(float x, float y) {
        this.x += x;
        this.y += y;
        return this;
    }

    /**
     * Subtract another vector from this one and return result - alters this vector.
     *
     * @return this vector
     */
    public Vec2 subLocal(Vec2 v) {
        x -= v.x;
        y -= v.y;
        return this;
    }

    /**
     * Multiply this vector by a number and return result - alters this vector.
     *
     * @return this vector
     */
    public Vec2 mulLocal(float a) {
        x *= a;
        y *= a;
        return this;
    }

    /**
     * Get the skew vector such that dot(skew_vec, other) == cross(vec, other).
     *
     * @return new vector
     */
    public Vec2 skew() {
        return new Vec2(-y, x);
    }

    /**
     * Get the skew vector such that dot(skew_vec, other) == cross(vec, other);
     * does not alter this vector.
     *
     * @param out the out vector to alter
     */
    public void skew(Vec2 out) {
        out.x = -y;
        out.y = x;
    }

    /**
     * @return the length of this vector
     */
    public float length() {
        return FXGLMath.sqrt(x * x + y * y);
    }

    /**
     * @return the squared length of this vector
     */
    public float lengthSquared() {
        return (x * x + y * y);
    }

    public float distance(float otherX, float otherY) {
        float dx = otherX - x;
        float dy = otherY - y;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    public float distanceSquared(float otherX, float otherY) {
        float dx = otherX - x;
        float dy = otherY - y;
        return dx * dx + dy * dy;
    }

    public boolean distanceLessThanOrEqual(float otherX, float otherY, float distance) {
        return distanceSquared(otherX, otherY) <= distance * distance;
    }

    public boolean distanceGreaterThanOrEqual(float otherX, float otherY, float distance) {
        return distanceSquared(otherX, otherY) >= distance * distance;
    }

    /**
     * Normalize this vector and return the length before normalization. Alters this vector.
     */
    public float normalize() {
        float length = length();
        if (length < FXGLMath.EPSILON) {
            return 0f;
        }

        float invLength = 1.0f / length;
        x *= invLength;
        y *= invLength;
        return length;
    }

    /**
     * Normalizes and returns this vector. Alters this vector.
     *
     * @return this vector
     */
    public Vec2 normalizeLocal() {
        normalize();
        return this;
    }

    /**
     * True if the vector represents a pair of valid, non-infinite floating point numbers.
     */
    public boolean isValid() {
        return !Float.isNaN(x) && !Float.isInfinite(x) && !Float.isNaN(y) && !Float.isInfinite(y);
    }

    /**
     * Return a new vector that has positive components.
     *
     * @return new vector
     */
    public Vec2 abs() {
        return new Vec2(FXGLMath.abs(x), FXGLMath.abs(y));
    }

    /**
     * Modify this vector to have only positive components.
     *
     * @return this vector
     */
    public Vec2 absLocal() {
        x = FXGLMath.abs(x);
        y = FXGLMath.abs(y);
        return this;
    }

    /**
     * @return angle in degrees (-180, 180] between this vector and X axis (1, 0)
     */
    public float angle() {
        return angle(1, 0);
    }

    /**
     * @param other other vector
     * @return angle in degrees (-180, 180] between this vector and other
     */
    public float angle(Vec2 other) {
        return angle(other.x, other.y);
    }

    /**
     * @param otherX x component of other vector
     * @param otherY y component of other vector
     * @return angle in degrees (-180, 180] between this vector and other
     */
    public float angle(float otherX, float otherY) {
        double angle1 = Math.toDegrees(Math.atan2(y, x));
        double angle2 = Math.toDegrees(Math.atan2(otherY, otherX));

        return (float) (angle1 - angle2);

//        final float ax = otherX;
//        final float ay = otherY;
//
//        final float delta = (ax * x + ay * y) /
//                (float)Math.sqrt((ax * ax + ay * ay) * (x * x + y * y));
//
//        if (delta > 1.0) {
//            return 0;
//        }
//
//        if (delta < -1.0) {
//            return 180;
//        }
//
//        return (float) Math.toDegrees(Math.acos(delta));
    }

    /**
     * @return a copy of this vector
     */
    @SuppressWarnings("CloneDoesntCallSuperClone")
    @Override
    public Vec2 clone() {
        return new Vec2(x, y);
    }

    /**
     * @return a copy of this vector (new instance)
     */
    public Vec2 copy() {
        return clone();
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }

    @Override
    public void reset() {
        setZero();
    }

    /* STATIC */

    public static Vec2 abs(Vec2 a) {
        return new Vec2(FXGLMath.abs(a.x), FXGLMath.abs(a.y));
    }

    public static void absToOut(Vec2 a, Vec2 out) {
        out.x = FXGLMath.abs(a.x);
        out.y = FXGLMath.abs(a.y);
    }

    public static float dot(final Vec2 a, final Vec2 b) {
        return a.x * b.x + a.y * b.y;
    }

    public static float cross(final Vec2 a, final Vec2 b) {
        return a.x * b.y - a.y * b.x;
    }

    public static Vec2 cross(Vec2 a, float s) {
        return new Vec2(s * a.y, -s * a.x);
    }

    public static void crossToOut(Vec2 a, float s, Vec2 out) {
        final float tempy = -s * a.x;
        out.x = s * a.y;
        out.y = tempy;
    }

    public static void crossToOutUnsafe(Vec2 a, float s, Vec2 out) {
        assert (out != a);
        out.x = s * a.y;
        out.y = -s * a.x;
    }

    public static Vec2 cross(float s, Vec2 a) {
        return new Vec2(-s * a.y, s * a.x);
    }

    public static void crossToOut(float s, Vec2 a, Vec2 out) {
        final float tempY = s * a.x;
        out.x = -s * a.y;
        out.y = tempY;
    }

    public static void crossToOutUnsafe(float s, Vec2 a, Vec2 out) {
        assert (out != a);
        out.x = -s * a.y;
        out.y = s * a.x;
    }

    public static void negateToOut(Vec2 a, Vec2 out) {
        out.x = -a.x;
        out.y = -a.y;
    }

    public static Vec2 min(Vec2 a, Vec2 b) {
        return new Vec2(a.x < b.x ? a.x : b.x, a.y < b.y ? a.y : b.y);
    }

    public static Vec2 max(Vec2 a, Vec2 b) {
        return new Vec2(a.x > b.x ? a.x : b.x, a.y > b.y ? a.y : b.y);
    }

    public static void minToOut(Vec2 a, Vec2 b, Vec2 out) {
        out.x = a.x < b.x ? a.x : b.x;
        out.y = a.y < b.y ? a.y : b.y;
    }

    public static void maxToOut(Vec2 a, Vec2 b, Vec2 out) {
        out.x = a.x > b.x ? a.x : b.x;
        out.y = a.y > b.y ? a.y : b.y;
    }

    /**
     * @see Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Float.floatToIntBits(x);
        result = prime * result + Float.floatToIntBits(y);
        return result;
    }

    /**
     * @see Object#equals(Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Vec2 other = (Vec2) obj;
        if (Float.floatToIntBits(x) != Float.floatToIntBits(other.x)) return false;
        if (Float.floatToIntBits(y) != Float.floatToIntBits(other.y)) return false;
        return true;
    }
}
