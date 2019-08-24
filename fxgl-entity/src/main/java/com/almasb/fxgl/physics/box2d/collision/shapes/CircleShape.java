/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
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
public final class CircleShape extends Shape {

    public final Vec2 center = new Vec2();

    public CircleShape() {
        super(ShapeType.CIRCLE, 0);
    }

    @Override
    public Shape clone() {
        CircleShape shape = new CircleShape();
        shape.center.x = center.x;
        shape.center.y = center.y;
        shape.setRadius(this.getRadius());
        return shape;
    }

    @Override
    public int getChildCount() {
        return 1;
    }

    @Override
    public boolean testPoint(final Transform transform, final Vec2 p) {
        // Rot.mulToOutUnsafe(transform.q, m_p, center);
        // center.addLocal(transform.p);
        //
        // final Vec2 d = center.subLocal(p).negateLocal();
        // return Vec2.dot(d, d) <= radius * radius;
        final Rotation q = transform.q;
        final Vec2 tp = transform.p;
        float centerx = -(q.c * center.x - q.s * center.y + tp.x - p.x);
        float centery = -(q.s * center.x + q.c * center.y + tp.y - p.y);

        return centerx * centerx + centery * centery <= getRadius() * getRadius();
    }

    @Override
    public float computeDistanceToOut(Transform xf, Vec2 p, int childIndex, Vec2 normalOut) {
        final Rotation xfq = xf.q;
        float centerx = xfq.c * center.x - xfq.s * center.y + xf.p.x;
        float centery = xfq.s * center.x + xfq.c * center.y + xf.p.y;
        float dx = p.x - centerx;
        float dy = p.y - centery;
        float d1 = JBoxUtils.sqrt(dx * dx + dy * dy);
        normalOut.x = dx * 1 / d1;
        normalOut.y = dy * 1 / d1;
        return d1 - getRadius();
    }

    // Collision Detection in Interactive 3D Environments by Gino van den Bergen
    // From Section 3.1.2
    // x = s + a * r
    // norm(x) = radius
    @Override
    public boolean raycast(RayCastOutput output, RayCastInput input, Transform transform,
                                 int childIndex) {

        final Vec2 inputp1 = input.p1;
        final Vec2 inputp2 = input.p2;
        final Rotation tq = transform.q;
        final Vec2 tp = transform.p;

        // Rot.mulToOutUnsafe(transform.q, m_p, position);
        // position.addLocal(transform.p);
        final float positionx = tq.c * center.x - tq.s * center.y + tp.x;
        final float positiony = tq.s * center.x + tq.c * center.y + tp.y;

        final float sx = inputp1.x - positionx;
        final float sy = inputp1.y - positiony;
        // final float b = Vec2.dot(s, s) - radius * radius;
        final float b = sx * sx + sy * sy - getRadius() * getRadius();

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
            output.normal.getLengthAndNormalize();
            return true;
        }

        return false;
    }

    @Override
    public void computeAABB(final AABB aabb, final Transform transform, int childIndex) {
        final Rotation tq = transform.q;
        final Vec2 tp = transform.p;
        final float px = tq.c * center.x - tq.s * center.y + tp.x;
        final float py = tq.s * center.x + tq.c * center.y + tp.y;

        aabb.lowerBound.x = px - getRadius();
        aabb.lowerBound.y = py - getRadius();
        aabb.upperBound.x = px + getRadius();
        aabb.upperBound.y = py + getRadius();
    }

    @Override
    public void computeMass(final MassData massData, final float density) {
        massData.mass = density * JBoxSettings.PI * getRadius() * getRadius();
        massData.center.x = center.x;
        massData.center.y = center.y;

        // inertia about the local origin
        // massData.I = massData.mass * (0.5f * radius * radius + Vec2.dot(m_p, m_p));
        massData.I = massData.mass * (0.5f * getRadius() * getRadius() + center.x * center.x + center.y * center.y);
    }
}
