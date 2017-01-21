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

package sandbox;

import javafx.geometry.Point3D;

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

    public Quaternion multiply(Point3D vector) {
        double _x = (w * vector.getX()) + (y * vector.getZ()) - (z * vector.getY());
        double _y = (w * vector.getY()) + (z * vector.getX()) - (x * vector.getZ());
        double _z = (w * vector.getZ()) + (x * vector.getY()) - (y * vector.getX());
        double _w = -(x * vector.getX()) - (y * vector.getY()) - (z * vector.getZ());

        return new Quaternion(_x, _y, _z, _w);
    }
}
