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
package org.jbox2d.collision;

import com.almasb.fxgl.core.math.Vec2;
import org.jbox2d.common.JBoxSettings;

/**
 * A manifold for two touching convex shapes. Box2D supports multiple types of contact:
 * <ul>
 * <li>clip point versus plane with radius</li>
 * <li>point versus point with radius (circles)</li>
 * </ul>
 * The local point usage depends on the manifold type:
 * <ul>
 * <li>e_circles: the local center of circleA</li>
 * <li>e_faceA: the center of faceA</li>
 * <li>e_faceB: the center of faceB</li>
 * </ul>
 * Similarly the local normal usage:
 * <ul>
 * <li>e_circles: not used</li>
 * <li>e_faceA: the normal on polygonA</li>
 * <li>e_faceB: the normal on polygonB</li>
 * </ul>
 * We store contacts in this way so that position correction can account for movement, which is
 * critical for continuous physics. All contact scenarios must be expressed in one of these types.
 * This structure is stored across time steps, so we keep it small.
 */
public class Manifold {

    public static enum ManifoldType {
        CIRCLES, FACE_A, FACE_B
    }

    /** The points of contact. */
    public final ManifoldPoint[] points;

    /** not use for Type::e_points */
    public final Vec2 localNormal;

    /** usage depends on manifold type */
    public final Vec2 localPoint;

    public ManifoldType type;

    /** The number of manifold points. */
    public int pointCount;

    /**
     * creates a manifold with 0 points, with it's points array full of instantiated ManifoldPoints.
     */
    public Manifold() {
        points = new ManifoldPoint[JBoxSettings.maxManifoldPoints];
        for (int i = 0; i < JBoxSettings.maxManifoldPoints; i++) {
            points[i] = new ManifoldPoint();
        }
        localNormal = new Vec2();
        localPoint = new Vec2();
        pointCount = 0;
    }

    /**
     * Creates this manifold as a copy of the other
     *
     * @param other
     */
    public Manifold(Manifold other) {
        points = new ManifoldPoint[JBoxSettings.maxManifoldPoints];
        localNormal = other.localNormal.clone();
        localPoint = other.localPoint.clone();
        pointCount = other.pointCount;
        type = other.type;
        // djm: this is correct now
        for (int i = 0; i < JBoxSettings.maxManifoldPoints; i++) {
            points[i] = new ManifoldPoint(other.points[i]);
        }
    }

    /**
     * copies this manifold from the given one
     *
     * @param cp manifold to copy from
     */
    public void set(Manifold cp) {
        for (int i = 0; i < cp.pointCount; i++) {
            points[i].set(cp.points[i]);
        }

        type = cp.type;
        localNormal.set(cp.localNormal);
        localPoint.set(cp.localPoint);
        pointCount = cp.pointCount;
    }
}
