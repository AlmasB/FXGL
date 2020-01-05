/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics.box2d.common;

import com.almasb.fxgl.core.math.Vec2;

import java.io.Serializable;

/**
 * A 2-by-2 matrix. Stored in column-major order.
 */
public final class Mat22 implements Serializable {
    private static final long serialVersionUID = 2L;

    public final Vec2 ex = new Vec2();
    public final Vec2 ey = new Vec2();

    /**
     * Return a clone of this matrix.
     */
    @Override
    public Mat22 clone() {
        Mat22 mat = new Mat22();
        mat.ex.set(ex);
        mat.ey.set(ey);
        return mat;
    }

    /**
     * Set as the zero matrix.
     */
    public void setZero() {
        ex.x = 0.0f;
        ey.x = 0.0f;
        ex.y = 0.0f;
        ey.y = 0.0f;
    }

    public void invertToOut(Mat22 out) {
        float a = ex.x, b = ey.x, c = ex.y, d = ey.y;
        float det = a * d - b * c;

        det = 1.0f / det;
        out.ex.x = det * d;
        out.ey.x = -det * b;
        out.ex.y = -det * c;
        out.ey.y = det * a;
    }

    public void solveToOut(Vec2 b, Vec2 out) {
        float a11 = ex.x, a12 = ey.x, a21 = ex.y, a22 = ey.y;
        float det = a11 * a22 - a12 * a21;
        if (det != 0.0f) {
            det = 1.0f / det;
        }
        float tempy = det * (a11 * b.y - a21 * b.x);
        out.x = det * (a22 * b.x - a12 * b.y);
        out.y = tempy;
    }

    public static void mulToOutUnsafe(Mat22 R, Vec2 v, Vec2 out) {
        assert v != out;
        out.x = R.ex.x * v.x + R.ey.x * v.y;
        out.y = R.ex.y * v.x + R.ey.y * v.y;
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = prime * result + ex.hashCode();
        result = prime * result + ey.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Mat22 other = (Mat22) obj;
        if (ex == null) {
            if (other.ex != null) return false;
        } else if (!ex.equals(other.ex)) return false;
        if (ey == null) {
            if (other.ey != null) return false;
        } else if (!ey.equals(other.ey)) return false;
        return true;
    }

    @Override
    public String toString() {
        return "[" + ex.x + "," + ey.x + "]\n"
                +"[" + ex.y + "," + ey.y + "]";
    }
}
