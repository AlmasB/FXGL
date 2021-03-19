/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics.box2d.common;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.core.math.Vec2;

import java.io.Serializable;

/**
 * Represents a rotation.
 *
 * @author Daniel
 */
public final class Rotation implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final float[] sinLUT = new float[JBoxSettings.SINCOS_LUT_LENGTH];

    static {
        for (int i = 0; i < JBoxSettings.SINCOS_LUT_LENGTH; i++) {
            sinLUT[i] = (float) Math.sin(i * JBoxSettings.SINCOS_LUT_PRECISION);
        }
    }

    public float s, c; // sin and cos

    public Rotation() {
        setIdentity();
    }

    public Rotation(float angle) {
        set(angle);
    }

    public Rotation set(float angle) {
        s = sin(angle);
        c = cos(angle);
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
        return (float) FXGLMath.atan2(s, c);
    }

    @Override
    public String toString() {
        return "Rot(s:" + s + ", c:" + c + ")";
    }

    public static void mul(Rotation q, Rotation r, Rotation out) {
        float tempc = q.c * r.c - q.s * r.s;
        out.s = q.s * r.c + q.c * r.s;
        out.c = tempc;
    }

    public static void mulUnsafe(Rotation q, Rotation r, Rotation out) {
        assert r != out;
        assert q != out;
        // [qc -qs] * [rc -rs] = [qc*rc-qs*rs -qc*rs-qs*rc]
        // [qs qc] [rs rc] [qs*rc+qc*rs -qs*rs+qc*rc]
        // s = qs * rc + qc * rs
        // c = qc * rc - qs * rs
        out.s = q.s * r.c + q.c * r.s;
        out.c = q.c * r.c - q.s * r.s;
    }

    public static void mulTrans(Rotation q, Rotation r, Rotation out) {
        final float tempc = q.c * r.c + q.s * r.s;
        out.s = q.c * r.s - q.s * r.c;
        out.c = tempc;
    }

    public static void mulTransUnsafe(Rotation q, Rotation r, Rotation out) {
        // [ qc qs] * [rc -rs] = [qc*rc+qs*rs -qc*rs+qs*rc]
        // [-qs qc] [rs rc] [-qs*rc+qc*rs qs*rs+qc*rc]
        // s = qc * rs - qs * rc
        // c = qc * rc + qs * rs
        out.s = q.c * r.s - q.s * r.c;
        out.c = q.c * r.c + q.s * r.s;
    }

    public static void mulToOut(Rotation q, Vec2 v, Vec2 out) {
        float tempy = q.s * v.x + q.c * v.y;
        out.x = q.c * v.x - q.s * v.y;
        out.y = tempy;
    }

    public static void mulToOutUnsafe(Rotation q, Vec2 v, Vec2 out) {
        out.x = q.c * v.x - q.s * v.y;
        out.y = q.s * v.x + q.c * v.y;
    }

    public static void mulTrans(Rotation q, Vec2 v, Vec2 out) {
        final float tempy = -q.s * v.x + q.c * v.y;
        out.x = q.c * v.x + q.s * v.y;
        out.y = tempy;
    }

    public static void mulTransUnsafe(Rotation q, Vec2 v, Vec2 out) {
        out.x = q.c * v.x + q.s * v.y;
        out.y = -q.s * v.x + q.c * v.y;
    }

    private static float sin(float x) {
        return sinLUT(x);
    }

    private static float cos(float x) {
        return sinLUT(FXGLMath.HALF_PI_F - x);
    }

    private static float sinLUT(float x) {
        x %= FXGLMath.PI2_F;

        if (x < 0) {
            x += FXGLMath.PI2_F;
        }

        return sinLUT[FXGLMath.floor(x / JBoxSettings.SINCOS_LUT_PRECISION + .5f) % JBoxSettings.SINCOS_LUT_LENGTH];
    }
}
