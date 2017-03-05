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

package org.jbox2d.common;

import com.almasb.fxgl.core.math.Vec2;

import java.io.Serializable;

/**
 * Represents a rotation.
 *
 * @author Daniel
 */
public class Rotation implements Serializable {
    private static final long serialVersionUID = 1L;

    public float s, c; // sin and cos

    public Rotation() {
        setIdentity();
    }

    public Rotation(float angle) {
        set(angle);
    }

    public float getSin() {
        return s;
    }

    public float getCos() {
        return c;
    }

    public Rotation set(float angle) {
        s = JBoxUtils.sin(angle);
        c = JBoxUtils.cos(angle);
        return this;
    }

    public Rotation set(Rotation other) {
        s = other.s;
        c = other.c;
        return this;
    }

    public Rotation setIdentity() {
        s = 0;
        c = 1;
        return this;
    }

    public float getAngle() {
        return JBoxUtils.atan2(s, c);
    }

    public void getXAxis(Vec2 xAxis) {
        xAxis.set(c, s);
    }

    public void getYAxis(Vec2 yAxis) {
        yAxis.set(-s, c);
    }

    @Override
    public Rotation clone() {
        Rotation copy = new Rotation();
        copy.s = s;
        copy.c = c;
        return copy;
    }

    @Override
    public String toString() {
        return "Rot(s:" + s + ", c:" + c + ")";
    }

    public static final void mul(Rotation q, Rotation r, Rotation out) {
        float tempc = q.c * r.c - q.s * r.s;
        out.s = q.s * r.c + q.c * r.s;
        out.c = tempc;
    }

    public static final void mulUnsafe(Rotation q, Rotation r, Rotation out) {
        assert (r != out);
        assert (q != out);
        // [qc -qs] * [rc -rs] = [qc*rc-qs*rs -qc*rs-qs*rc]
        // [qs qc] [rs rc] [qs*rc+qc*rs -qs*rs+qc*rc]
        // s = qs * rc + qc * rs
        // c = qc * rc - qs * rs
        out.s = q.s * r.c + q.c * r.s;
        out.c = q.c * r.c - q.s * r.s;
    }

    public static final void mulTrans(Rotation q, Rotation r, Rotation out) {
        final float tempc = q.c * r.c + q.s * r.s;
        out.s = q.c * r.s - q.s * r.c;
        out.c = tempc;
    }

    public static final void mulTransUnsafe(Rotation q, Rotation r, Rotation out) {
        // [ qc qs] * [rc -rs] = [qc*rc+qs*rs -qc*rs+qs*rc]
        // [-qs qc] [rs rc] [-qs*rc+qc*rs qs*rs+qc*rc]
        // s = qc * rs - qs * rc
        // c = qc * rc + qs * rs
        out.s = q.c * r.s - q.s * r.c;
        out.c = q.c * r.c + q.s * r.s;
    }

    public static final void mulToOut(Rotation q, Vec2 v, Vec2 out) {
        float tempy = q.s * v.x + q.c * v.y;
        out.x = q.c * v.x - q.s * v.y;
        out.y = tempy;
    }

    public static final void mulToOutUnsafe(Rotation q, Vec2 v, Vec2 out) {
        out.x = q.c * v.x - q.s * v.y;
        out.y = q.s * v.x + q.c * v.y;
    }

    public static final void mulTrans(Rotation q, Vec2 v, Vec2 out) {
        final float tempy = -q.s * v.x + q.c * v.y;
        out.x = q.c * v.x + q.s * v.y;
        out.y = tempy;
    }

    public static final void mulTransUnsafe(Rotation q, Vec2 v, Vec2 out) {
        out.x = q.c * v.x + q.s * v.y;
        out.y = -q.s * v.x + q.c * v.y;
    }
}
