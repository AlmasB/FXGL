/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package com.almasb.fxgl.physics.box2d.common;

import java.io.Serializable;

/**
 * @author Daniel Murphy
 */
public final class Vec3 implements Serializable {
    private static final long serialVersionUID = 1L;

    public float x, y, z;

    public Vec3() {
        x = y = z = 0f;
    }

    public Vec3(float argX, float argY, float argZ) {
        x = argX;
        y = argY;
        z = argZ;
    }

    public Vec3(Vec3 copy) {
        x = copy.x;
        y = copy.y;
        z = copy.z;
    }

    public Vec3 set(Vec3 vec) {
        x = vec.x;
        y = vec.y;
        z = vec.z;
        return this;
    }

    public Vec3 set(float argX, float argY, float argZ) {
        x = argX;
        y = argY;
        z = argZ;
        return this;
    }

    public Vec3 addLocal(Vec3 argVec) {
        x += argVec.x;
        y += argVec.y;
        z += argVec.z;
        return this;
    }

    public Vec3 subLocal(Vec3 argVec) {
        x -= argVec.x;
        y -= argVec.y;
        z -= argVec.z;
        return this;
    }

    public Vec3 mulLocal(float argScalar) {
        x *= argScalar;
        y *= argScalar;
        z *= argScalar;
        return this;
    }

    public Vec3 negateLocal() {
        x = -x;
        y = -y;
        z = -z;
        return this;
    }

    public void setZero() {
        x = 0;
        y = 0;
        z = 0;
    }

    @Override
    public Vec3 clone() {
        return new Vec3(this);
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + "," + z + ")";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Float.floatToIntBits(x);
        result = prime * result + Float.floatToIntBits(y);
        result = prime * result + Float.floatToIntBits(z);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Vec3 other = (Vec3) obj;
        if (Float.floatToIntBits(x) != Float.floatToIntBits(other.x)) return false;
        if (Float.floatToIntBits(y) != Float.floatToIntBits(other.y)) return false;
        if (Float.floatToIntBits(z) != Float.floatToIntBits(other.z)) return false;
        return true;
    }

    public static float dot(Vec3 a, Vec3 b) {
        return a.x * b.x + a.y * b.y + a.z * b.z;
    }

    public static Vec3 cross(Vec3 a, Vec3 b) {
        return new Vec3(a.y * b.z - a.z * b.y, a.z * b.x - a.x * b.z, a.x * b.y - a.y * b.x);
    }

    public static void crossToOutUnsafe(Vec3 a, Vec3 b, Vec3 out) {
        assert out != b;
        assert out != a;
        out.x = a.y * b.z - a.z * b.y;
        out.y = a.z * b.x - a.x * b.z;
        out.z = a.x * b.y - a.y * b.x;
    }
}
