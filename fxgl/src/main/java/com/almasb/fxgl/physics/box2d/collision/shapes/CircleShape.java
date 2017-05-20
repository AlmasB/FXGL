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

package com.almasb.fxgl.physics.box2d.collision.shapes;

import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.physics.box2d.collision.AABB;
import com.almasb.fxgl.physics.box2d.collision.RayCastInput;
import com.almasb.fxgl.physics.box2d.collision.RayCastOutput;
import com.almasb.fxgl.physics.box2d.common.JBoxSettings;
import com.almasb.fxgl.physics.box2d.common.JBoxUtils;
import com.almasb.fxgl.physics.box2d.common.Rotation;
import com.almasb.fxgl.physics.box2d.common.Transform;

/**
 * A circle shape.
 */
public class CircleShape extends Shape {

    public final Vec2 m_p = new Vec2();

    public CircleShape() {
        super(ShapeType.CIRCLE);
        setRadius(0);
    }

    public final Shape clone() {
        CircleShape shape = new CircleShape();
        shape.m_p.x = m_p.x;
        shape.m_p.y = m_p.y;
        shape.setRadius(this.getRadius());
        return shape;
    }

    public final int getChildCount() {
        return 1;
    }

    /**
     * Get the supporting vertex index in the given direction.
     *
     * @param d
     * @return
     */
    public final int getSupport(final Vec2 d) {
        return 0;
    }

    /**
     * Get the supporting vertex in the given direction.
     *
     * @param d
     * @return
     */
    public final Vec2 getSupportVertex(final Vec2 d) {
        return m_p;
    }

    /**
     * Get the vertex count.
     *
     * @return
     */
    public final int getVertexCount() {
        return 1;
    }

    /**
     * Get a vertex by index.
     *
     * @param index
     * @return
     */
    public final Vec2 getVertex(final int index) {
        assert (index == 0);
        return m_p;
    }

    @Override
    public final boolean testPoint(final Transform transform, final Vec2 p) {
        // Rot.mulToOutUnsafe(transform.q, m_p, center);
        // center.addLocal(transform.p);
        //
        // final Vec2 d = center.subLocal(p).negateLocal();
        // return Vec2.dot(d, d) <= radius * radius;
        final Rotation q = transform.q;
        final Vec2 tp = transform.p;
        float centerx = -(q.c * m_p.x - q.s * m_p.y + tp.x - p.x);
        float centery = -(q.s * m_p.x + q.c * m_p.y + tp.y - p.y);

        return centerx * centerx + centery * centery <= radius * radius;
    }

    @Override
    public float computeDistanceToOut(Transform xf, Vec2 p, int childIndex, Vec2 normalOut) {
        final Rotation xfq = xf.q;
        float centerx = xfq.c * m_p.x - xfq.s * m_p.y + xf.p.x;
        float centery = xfq.s * m_p.x + xfq.c * m_p.y + xf.p.y;
        float dx = p.x - centerx;
        float dy = p.y - centery;
        float d1 = JBoxUtils.sqrt(dx * dx + dy * dy);
        normalOut.x = dx * 1 / d1;
        normalOut.y = dy * 1 / d1;
        return d1 - radius;
    }

    // Collision Detection in Interactive 3D Environments by Gino van den Bergen
    // From Section 3.1.2
    // x = s + a * r
    // norm(x) = radius
    @Override
    public final boolean raycast(RayCastOutput output, RayCastInput input, Transform transform,
                                 int childIndex) {

        final Vec2 inputp1 = input.p1;
        final Vec2 inputp2 = input.p2;
        final Rotation tq = transform.q;
        final Vec2 tp = transform.p;

        // Rot.mulToOutUnsafe(transform.q, m_p, position);
        // position.addLocal(transform.p);
        final float positionx = tq.c * m_p.x - tq.s * m_p.y + tp.x;
        final float positiony = tq.s * m_p.x + tq.c * m_p.y + tp.y;

        final float sx = inputp1.x - positionx;
        final float sy = inputp1.y - positiony;
        // final float b = Vec2.dot(s, s) - radius * radius;
        final float b = sx * sx + sy * sy - radius * radius;

        // Solve quadratic equation.
        final float rx = inputp2.x - inputp1.x;
        final float ry = inputp2.y - inputp1.y;
        // final float c = Vec2.dot(s, r);
        // final float rr = Vec2.dot(r, r);
        final float c = sx * rx + sy * ry;
        final float rr = rx * rx + ry * ry;
        final float sigma = c * c - rr * b;

        // Check for negative discriminant and short segment.
        if (sigma < 0.0f || rr < JBoxSettings.EPSILON) {
            return false;
        }

        // Find the point of intersection of the line with the circle.
        float a = -(c + JBoxUtils.sqrt(sigma));

        // Is the intersection point on the segment?
        if (0.0f <= a && a <= input.maxFraction * rr) {
            a /= rr;
            output.fraction = a;
            output.normal.x = rx * a + sx;
            output.normal.y = ry * a + sy;
            output.normal.normalize();
            return true;
        }

        return false;
    }

    @Override
    public final void computeAABB(final AABB aabb, final Transform transform, int childIndex) {
        final Rotation tq = transform.q;
        final Vec2 tp = transform.p;
        final float px = tq.c * m_p.x - tq.s * m_p.y + tp.x;
        final float py = tq.s * m_p.x + tq.c * m_p.y + tp.y;

        aabb.lowerBound.x = px - radius;
        aabb.lowerBound.y = py - radius;
        aabb.upperBound.x = px + radius;
        aabb.upperBound.y = py + radius;
    }

    @Override
    public final void computeMass(final MassData massData, final float density) {
        massData.mass = density * JBoxSettings.PI * radius * radius;
        massData.center.x = m_p.x;
        massData.center.y = m_p.y;

        // inertia about the local origin
        // massData.I = massData.mass * (0.5f * radius * radius + Vec2.dot(m_p, m_p));
        massData.I = massData.mass * (0.5f * radius * radius + (m_p.x * m_p.x + m_p.y * m_p.y));
    }
}
