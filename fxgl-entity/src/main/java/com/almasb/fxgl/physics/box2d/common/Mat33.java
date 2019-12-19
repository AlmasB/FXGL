/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package com.almasb.fxgl.physics.box2d.common;

import com.almasb.fxgl.core.math.Vec2;

import java.io.Serializable;

/**
 * A 3-by-3 matrix. Stored in column-major order.
 *
 * @author Daniel Murphy
 */
public final class Mat33 implements Serializable {
    private static final long serialVersionUID = 2L;

    public final Vec3 ex, ey, ez;

    public Mat33() {
        ex = new Vec3();
        ey = new Vec3();
        ez = new Vec3();
    }

    public static void mul22ToOutUnsafe(Mat33 A, Vec2 v, Vec2 out) {
        assert v != out;
        out.y = A.ex.y * v.x + A.ey.y * v.y;
        out.x = A.ex.x * v.x + A.ey.x * v.y;
    }

    public static void mulToOutUnsafe(Mat33 A, Vec3 v, Vec3 out) {
        assert out != v;
        out.x = v.x * A.ex.x + v.y * A.ey.x + v.z * A.ez.x;
        out.y = v.x * A.ex.y + v.y * A.ey.y + v.z * A.ez.y;
        out.z = v.x * A.ex.z + v.y * A.ey.z + v.z * A.ez.z;
    }

    /**
     * Solve A * x = b, where b is a column vector. This is more efficient than computing the inverse
     * in one-shot cases.
     */
    public void solve22ToOut(Vec2 b, Vec2 out) {
        final float a11 = ex.x, a12 = ey.x, a21 = ex.y, a22 = ey.y;
        float det = a11 * a22 - a12 * a21;
        if (det != 0.0f) {
            det = 1.0f / det;
        }
        out.x = det * (a22 * b.x - a12 * b.y);
        out.y = det * (a11 * b.y - a21 * b.x);
    }

    /**
     * Solve A * x = b, where b is a column vector. This is more efficient than computing the inverse
     * in one-shot cases.
     *
     * @param out the result
     */
    public void solve33ToOut(Vec3 b, Vec3 out) {
        assert b != out;
        Vec3.crossToOutUnsafe(ey, ez, out);
        float det = Vec3.dot(ex, out);
        if (det != 0.0f) {
            det = 1.0f / det;
        }
        Vec3.crossToOutUnsafe(ey, ez, out);
        final float x = det * Vec3.dot(b, out);
        Vec3.crossToOutUnsafe(b, ez, out);
        final float y = det * Vec3.dot(ex, out);
        Vec3.crossToOutUnsafe(ey, b, out);
        float z = det * Vec3.dot(ex, out);
        out.x = x;
        out.y = y;
        out.z = z;
    }

    public void getInverse22(Mat33 M) {
        float a = ex.x, b = ey.x, c = ex.y, d = ey.y;
        float det = a * d - b * c;
        if (det != 0.0f) {
            det = 1.0f / det;
        }

        M.ex.x = det * d;
        M.ey.x = -det * b;
        M.ex.z = 0.0f;
        M.ex.y = -det * c;
        M.ey.y = det * a;
        M.ey.z = 0.0f;
        M.ez.x = 0.0f;
        M.ez.y = 0.0f;
        M.ez.z = 0.0f;
    }

    // / Returns the zero matrix if singular.
    public void getSymInverse33(Mat33 M) {
        float bx = ey.y * ez.z - ey.z * ez.y;
        float by = ey.z * ez.x - ey.x * ez.z;
        float bz = ey.x * ez.y - ey.y * ez.x;
        float det = ex.x * bx + ex.y * by + ex.z * bz;
        if (det != 0.0f) {
            det = 1.0f / det;
        }

        float a11 = ex.x, a12 = ey.x, a13 = ez.x;
        float a22 = ey.y, a23 = ez.y;
        float a33 = ez.z;

        M.ex.x = det * (a22 * a33 - a23 * a23);
        M.ex.y = det * (a13 * a23 - a12 * a33);
        M.ex.z = det * (a12 * a23 - a13 * a22);

        M.ey.x = M.ex.y;
        M.ey.y = det * (a11 * a33 - a13 * a13);
        M.ey.z = det * (a13 * a12 - a11 * a23);

        M.ez.x = M.ex.z;
        M.ez.y = M.ey.z;
        M.ez.z = det * (a11 * a22 - a12 * a12);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ex.hashCode();
        result = prime * result + ey.hashCode();
        result = prime * result + ez.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Mat33 other = (Mat33) obj;

        if (!ex.equals(other.ex))
            return false;

        if (!ey.equals(other.ey))
            return false;

        if (!ez.equals(other.ez))
            return false;

        return true;
    }
}
