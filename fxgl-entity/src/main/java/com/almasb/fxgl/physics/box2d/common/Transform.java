/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package com.almasb.fxgl.physics.box2d.common;

import com.almasb.fxgl.core.math.Vec2;

import java.io.Serializable;

/**
 * A transform contains translation and rotation. It is used to represent the position and
 * orientation of rigid frames.
 */
public final class Transform implements Serializable {
    private static final long serialVersionUID = 1L;

    /** The translation caused by the transform */
    public final Vec2 p = new Vec2();

    /** A matrix representing a rotation */
    public final Rotation q = new Rotation();

    /** Set this to equal another transform. */
    public Transform set(final Transform xf) {
        p.set(xf.p);
        q.set(xf.q);
        return this;
    }

    /**
     * Set this based on the position and angle.
     */
    public void set(Vec2 p, float angle) {
        this.p.set(p);
        q.set(angle);
    }

    /** Set this to the identity transform. */
    public void setIdentity() {
        p.setZero();
        q.setIdentity();
    }

    public static Vec2 mul(Transform T, Vec2 v) {
        return new Vec2(T.q.c * v.x - T.q.s * v.y + T.p.x, T.q.s * v.x + T.q.c * v.y + T.p.y);
    }

    public static void mulToOut(Transform T, Vec2 v, Vec2 out) {
        float tempy = T.q.s * v.x + T.q.c * v.y + T.p.y;
        out.x = (T.q.c * v.x - T.q.s * v.y) + T.p.x;
        out.y = tempy;
    }

    public static void mulToOutUnsafe(Transform T, Vec2 v, Vec2 out) {
        assert v != out;
        out.x = T.q.c * v.x - T.q.s * v.y + T.p.x;
        out.y = T.q.s * v.x + T.q.c * v.y + T.p.y;
    }

    public static void mulTransToOut(Transform T, Vec2 v, Vec2 out) {
        float px = v.x - T.p.x;
        float py = v.y - T.p.y;
        float tempy = -T.q.s * px + T.q.c * py;
        out.x = T.q.c * px + T.q.s * py;
        out.y = tempy;
    }

    public static void mulTransToOutUnsafe(Transform T, Vec2 v, Vec2 out) {
        assert v != out;
        float px = v.x - T.p.x;
        float py = v.y - T.p.y;
        out.x = T.q.c * px + T.q.s * py;
        out.y = -T.q.s * px + T.q.c * py;
    }

    public static void mulToOut(Transform A, Transform B, Transform out) {
        assert out != A;
        Rotation.mul(A.q, B.q, out.q);
        Rotation.mulToOut(A.q, B.p, out.p);
        out.p.addLocal(A.p);
    }

    private static Vec2 pool = new Vec2();

    public static void mulTransToOutUnsafe(Transform A, Transform B,
                                                 Transform out) {
        assert out != A;
        assert out != B;
        Rotation.mulTransUnsafe(A.q, B.q, out.q);
        pool.set(B.p).subLocal(A.p);
        Rotation.mulTransUnsafe(A.q, pool, out.p);
    }

    @Override
    public String toString() {
        String s = "XForm:\n";
        s += "Position: " + p + "\n";
        s += "R: \n" + q + "\n";
        return s;
    }
}
