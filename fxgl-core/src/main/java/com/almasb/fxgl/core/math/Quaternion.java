/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core.math;

/**
 * Immutable.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public final class Quaternion {
    private final double x, y, z, w;

    public Quaternion(double x, double y, double z, double w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public double getW() {
        return w;
    }

    public Quaternion normalize() {
        double length = Math.sqrt(x * x + y * y + z * z + w * w);
        return new Quaternion(x / length, y / length, z / length, w / length);
    }

    public Quaternion conjugate() {
        return new Quaternion(-x, -y, -z, w);
    }

    public Quaternion multiply(Quaternion other) {
        double _x = (x * other.w) + (w * other.x) + (y * other.z) - (z * other.y);
        double _y = (y * other.w) + (w * other.y) + (z * other.x) - (x * other.z);
        double _z = (z * other.w) + (w * other.z) + (x * other.y) - (y * other.x);
        double _w = (w * other.w) - (x * other.x) - (y * other.y) - (z * other.z);

        return new Quaternion(_x, _y, _z, _w);
    }
}







