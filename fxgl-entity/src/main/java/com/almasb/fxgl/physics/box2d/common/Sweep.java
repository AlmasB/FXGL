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
 * This describes the motion of a body/shape for TOI computation.
 * Shapes are defined with respect to the body origin, which may not coincide with the center of mass.
 * However, to support dynamics we must interpolate the center of mass position.
 */
public final class Sweep implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Local center of mass position */
    public final Vec2 localCenter = new Vec2();

    /** Center world positions */
    public final Vec2 c0 = new Vec2();
    public final Vec2 c = new Vec2();

    /** World angles */
    public float a0;
    public float a;

    /** Fraction of the current time step in the range [0,1] c0 and a0 are the positions at alpha0. */
    public float alpha0;

    public void normalize() {
        float d = FXGLMath.PI2_F * JBoxUtils.floor(a0 / FXGLMath.PI2_F);
        a0 -= d;
        a -= d;
    }

    public Sweep set(Sweep other) {
        localCenter.set(other.localCenter);
        c0.set(other.c0);
        c.set(other.c);
        a0 = other.a0;
        a = other.a;
        alpha0 = other.alpha0;
        return this;
    }

    /**
     * Get the interpolated transform at a specific time.
     *
     * @param xf the result is placed here - must not be null
     * @param beta the normalized time in [0,1]
     */
    public void getTransform(final Transform xf, final float beta) {
        assert xf != null;
        // xf->p = (1.0f - beta) * c0 + beta * c;
        // float32 angle = (1.0f - beta) * a0 + beta * a;
        // xf->q.Set(angle);
        xf.p.x = (1.0f - beta) * c0.x + beta * c.x;
        xf.p.y = (1.0f - beta) * c0.y + beta * c.y;

        float angle = (1.0f - beta) * a0 + beta * a;
        xf.q.set(angle);

        // Shift to origin
        // xf->p -= b2Mul(xf->q, localCenter);
        final Rotation q = xf.q;
        xf.p.x -= q.c * localCenter.x - q.s * localCenter.y;
        xf.p.y -= q.s * localCenter.x + q.c * localCenter.y;
    }

    /**
     * Advance the sweep forward, yielding a new initial state.
     *
     * @param alpha the new initial time
     */
    public void advance(final float alpha) {
        assert alpha0 < 1.0f;

        float beta = (alpha - alpha0) / (1.0f - alpha0);

        c0.x += beta * (c.x - c0.x);
        c0.y += beta * (c.y - c0.y);

        a0 += beta * (a - a0);

        alpha0 = alpha;
    }

    @Override
    public String toString() {
        String s = "Sweep:\nlocalCenter: " + localCenter + "\n";
        s += "c0: " + c0 + ", c: " + c + "\n";
        s += "a0: " + a0 + ", a: " + a + "\n";
        s += "alpha0: " + alpha0;
        return s;
    }
}
