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

    public final Vec2 ex, ey;

    /**
     * Construct zero matrix. Note: this is NOT an identity matrix!
     */
    public Mat22() {
        ex = new Vec2();
        ey = new Vec2();
    }

    /**
     * Create a matrix with given vectors as columns.
     *
     * @param c1 Column 1 of matrix
     * @param c2 Column 2 of matrix
     */
    public Mat22(Vec2 c1, Vec2 c2) {
        ex = c1.clone();
        ey = c2.clone();
    }

    /**
     * Create a matrix from four floats.
     */
    public Mat22(float exx, float col2x, float exy, float col2y) {
        ex = new Vec2(exx, exy);
        ey = new Vec2(col2x, col2y);
    }

    /**
     * Set as a copy of another matrix.
     *
     * @param m Matrix to copy
     */
    public Mat22 set(Mat22 m) {
        ex.x = m.ex.x;
        ex.y = m.ex.y;
        ey.x = m.ey.x;
        ey.y = m.ey.y;
        return this;
    }

    public Mat22 set(float exx, float col2x, float exy, float col2y) {
        ex.x = exx;
        ex.y = exy;
        ey.x = col2x;
        ey.y = col2y;
        return this;
    }

    /**
     * Return a clone of this matrix.
     */
    @Override
    public Mat22 clone() {
        return new Mat22(ex, ey);
    }

    /**
     * Set as a matrix representing a rotation.
     *
     * @param angle Rotation (in radians) that matrix represents.
     */
    public void set(float angle) {
        float c = JBoxUtils.cos(angle);
        float s = JBoxUtils.sin(angle);
        ex.x = c;
        ey.x = -s;
        ex.y = s;
        ey.y = c;
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

    /**
     * Extract the angle from this matrix (assumed to be a rotation matrix).
     *
     * @return angle
     */
    public float getAngle() {
        return JBoxUtils.atan2(ex.y, ex.x);
    }

    /**
     * Set by column vectors.
     *
     * @param c1 Column 1
     * @param c2 Column 2
     */
    public void set(Vec2 c1, Vec2 c2) {
        ex.x = c1.x;
        ey.x = c2.x;
        ex.y = c1.y;
        ey.y = c2.y;
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

    /**
     * Multiply a vector by this matrix.
     *
     * @param v Vector to multiply by matrix.
     * @return Resulting vector
     */
    public Vec2 mul(Vec2 v) {
        return new Vec2(ex.x * v.x + ey.x * v.y, ex.y * v.x + ey.y * v.y);
    }

    public void mulToOutUnsafe(Vec2 v, Vec2 out) {
        assert v != out;
        out.x = ex.x * v.x + ey.x * v.y;
        out.y = ex.y * v.x + ey.y * v.y;
    }

    /**
     * Multiply another matrix by this one (this one on left).
     */
    public Mat22 mul(Mat22 R) {
    /*
     * Mat22 C = new Mat22();C.set(this.mul(R.ex), this.mul(R.ey));return C;
     */
        Mat22 C = new Mat22();
        C.ex.x = ex.x * R.ex.x + ey.x * R.ex.y;
        C.ex.y = ex.y * R.ex.x + ey.y * R.ex.y;
        C.ey.x = ex.x * R.ey.x + ey.x * R.ey.y;
        C.ey.y = ex.y * R.ey.x + ey.y * R.ey.y;
        // C.set(ex,col2);
        return C;
    }

    public Mat22 mulLocal(Mat22 R) {
        mulToOut(R, this);
        return this;
    }

    public void mulToOut(Mat22 R, Mat22 out) {
        float tempy1 = this.ex.y * R.ex.x + this.ey.y * R.ex.y;
        float tempx1 = this.ex.x * R.ex.x + this.ey.x * R.ex.y;
        out.ex.x = tempx1;
        out.ex.y = tempy1;
        float tempy2 = this.ex.y * R.ey.x + this.ey.y * R.ey.y;
        float tempx2 = this.ex.x * R.ey.x + this.ey.x * R.ey.y;
        out.ey.x = tempx2;
        out.ey.y = tempy2;
    }

    public void mulToOutUnsafe(Mat22 R, Mat22 out) {
        assert out != R;
        assert out != this;
        out.ex.x = this.ex.x * R.ex.x + this.ey.x * R.ex.y;
        out.ex.y = this.ex.y * R.ex.x + this.ey.y * R.ex.y;
        out.ey.x = this.ex.x * R.ey.x + this.ey.x * R.ey.y;
        out.ey.y = this.ex.y * R.ey.x + this.ey.y * R.ey.y;
    }

    /**
     * Add this matrix to B, return the result.
     */
    public Mat22 add(Mat22 B) {
        // return new Mat22(ex.add(B.ex), col2.add(B.ey));
        Mat22 m = new Mat22();
        m.ex.x = ex.x + B.ex.x;
        m.ex.y = ex.y + B.ex.y;
        m.ey.x = ey.x + B.ey.x;
        m.ey.y = ey.y + B.ey.y;
        return m;
    }

    /**
     * Add B to this matrix locally.
     */
    public Mat22 addLocal(Mat22 B) {
        // ex.addLocal(B.ex);
        // col2.addLocal(B.ey);
        ex.x += B.ex.x;
        ex.y += B.ex.y;
        ey.x += B.ey.x;
        ey.y += B.ey.y;
        return this;
    }

    /**
     * Solve A * x = b where A = this matrix.
     *
     * @return The vector x that solves the above equation.
     */
    public Vec2 solve(Vec2 b) {
        float a11 = ex.x, a12 = ey.x, a21 = ex.y, a22 = ey.y;
        float det = a11 * a22 - a12 * a21;
        if (det != 0.0f) {
            det = 1.0f / det;
        }
        return new Vec2(det * (a22 * b.x - a12 * b.y), det * (a11 * b.y - a21 * b.x));
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

    public static Vec2 mul(Mat22 R, Vec2 v) {
        // return R.mul(v);
        return new Vec2(R.ex.x * v.x + R.ey.x * v.y, R.ex.y * v.x + R.ey.y * v.y);
    }

    public static void mulToOutUnsafe(Mat22 R, Vec2 v, Vec2 out) {
        assert v != out;
        out.x = R.ex.x * v.x + R.ey.x * v.y;
        out.y = R.ex.y * v.x + R.ey.y * v.y;
    }

    public static Mat22 mul(Mat22 A, Mat22 B) {
        // return A.mul(B);
        Mat22 C = new Mat22();
        C.ex.x = A.ex.x * B.ex.x + A.ey.x * B.ex.y;
        C.ex.y = A.ex.y * B.ex.x + A.ey.y * B.ex.y;
        C.ey.x = A.ex.x * B.ey.x + A.ey.x * B.ey.y;
        C.ey.y = A.ex.y * B.ey.x + A.ey.y * B.ey.y;
        return C;
    }

    public static void mulToOutUnsafe(Mat22 A, Mat22 B, Mat22 out) {
        assert out != A;
        assert out != B;
        out.ex.x = A.ex.x * B.ex.x + A.ey.x * B.ex.y;
        out.ex.y = A.ex.y * B.ex.x + A.ey.y * B.ex.y;
        out.ey.x = A.ex.x * B.ey.x + A.ey.x * B.ey.y;
        out.ey.y = A.ex.y * B.ey.x + A.ey.y * B.ey.y;
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = prime * result + ((ex == null) ? 0 : ex.hashCode());
        result = prime * result + ((ey == null) ? 0 : ey.hashCode());
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
