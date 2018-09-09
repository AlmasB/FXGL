/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics.box2d.common;

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
