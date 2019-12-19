/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package com.almasb.fxgl.physics.box2d.collision;

import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.physics.box2d.common.JBoxSettings;
import com.almasb.fxgl.physics.box2d.pooling.IWorldPool;

import static com.almasb.fxgl.core.math.FXGLMath.abs;

/**
 * An axis-aligned bounding box.
 */
public final class AABB {

    /**
     * Bottom left vertex of bounding box.
     */
    public final Vec2 lowerBound;

    /**
     * Top right vertex of bounding box.
     */
    public final Vec2 upperBound;

    /**
     * Creates the default object, with vertices at 0,0 and 0,0.
     */
    public AABB() {
        lowerBound = new Vec2();
        upperBound = new Vec2();
    }

    /**
     * Copies from the given object.
     *
     * @param copy the object to copy from
     */
    public AABB(AABB copy) {
        this(copy.lowerBound, copy.upperBound);
    }

    /**
     * Creates an AABB object using the given bounding vertices.
     *
     * @param lowerVertex the bottom left vertex of the bounding box
     * @param upperVertex the top right vertex of the bounding box
     */
    public AABB(Vec2 lowerVertex, Vec2 upperVertex) {
        this.lowerBound = lowerVertex.clone(); // clone to be safe
        this.upperBound = upperVertex.clone();
    }

    /**
     * Sets this object from the given object.
     *
     * @param aabb the object to copy from
     */
    public void set(AABB aabb) {
        Vec2 v = aabb.lowerBound;
        lowerBound.x = v.x;
        lowerBound.y = v.y;
        Vec2 v1 = aabb.upperBound;
        upperBound.x = v1.x;
        upperBound.y = v1.y;
    }

    /**
     * @return the center of the AABB
     */
    public Vec2 getCenter() {
        return new Vec2(lowerBound)
                .addLocal(upperBound)
                .mulLocal(0.5);
    }

    /**
     * @return the extents of the AABB (half-widths)
     */
    public Vec2 getExtents() {
        return new Vec2(upperBound)
                .subLocal(lowerBound)
                .mulLocal(0.5);
    }

    /**
     * @return the perimeter length
     */
    public float getPerimeter() {
        return 2.0f * (upperBound.x - lowerBound.x + upperBound.y - lowerBound.y);
    }

    /**
     * Combine two AABBs into this one.
     */
    public void combine(AABB aabb1, AABB aabb2) {
        lowerBound.x = aabb1.lowerBound.x < aabb2.lowerBound.x ? aabb1.lowerBound.x : aabb2.lowerBound.x;
        lowerBound.y = aabb1.lowerBound.y < aabb2.lowerBound.y ? aabb1.lowerBound.y : aabb2.lowerBound.y;
        upperBound.x = aabb1.upperBound.x > aabb2.upperBound.x ? aabb1.upperBound.x : aabb2.upperBound.x;
        upperBound.y = aabb1.upperBound.y > aabb2.upperBound.y ? aabb1.upperBound.y : aabb2.upperBound.y;
    }

    /**
     * Combines another aabb with this one.
     */
    public void combine(AABB aabb) {
        lowerBound.x = lowerBound.x < aabb.lowerBound.x ? lowerBound.x : aabb.lowerBound.x;
        lowerBound.y = lowerBound.y < aabb.lowerBound.y ? lowerBound.y : aabb.lowerBound.y;
        upperBound.x = upperBound.x > aabb.upperBound.x ? upperBound.x : aabb.upperBound.x;
        upperBound.y = upperBound.y > aabb.upperBound.y ? upperBound.y : aabb.upperBound.y;
    }

    /**
     * @return true if this aabb contain the provided AABB
     */
    public boolean contains(AABB aabb) {
        return lowerBound.x <= aabb.lowerBound.x && lowerBound.y <= aabb.lowerBound.y
                && aabb.upperBound.x <= upperBound.x && aabb.upperBound.y <= upperBound.y;
    }

    /**
     * From Real-time Collision Detection, p179.
     */
    public boolean raycast(final RayCastOutput output, final RayCastInput input, IWorldPool argPool) {
        float tmin = -Float.MAX_VALUE;
        float tmax = Float.MAX_VALUE;

        final Vec2 p = argPool.popVec2();
        final Vec2 d = argPool.popVec2();
        final Vec2 absD = argPool.popVec2();
        final Vec2 normal = argPool.popVec2();

        p.set(input.p1);
        d.set(input.p2).subLocal(input.p1);

        absD.x = abs(d.x);
        absD.y = abs(d.y);

        // x then y
        if (absD.x < JBoxSettings.EPSILON) {
            // Parallel.
            if (p.x < lowerBound.x || upperBound.x < p.x) {
                argPool.pushVec2(4);
                return false;
            }
        } else {
            final float inv_d = 1.0f / d.x;
            float t1 = (lowerBound.x - p.x) * inv_d;
            float t2 = (upperBound.x - p.x) * inv_d;

            // Sign of the normal vector.
            float s = -1.0f;

            if (t1 > t2) {
                final float temp = t1;
                t1 = t2;
                t2 = temp;
                s = 1.0f;
            }

            // Push the min up
            if (t1 > tmin) {
                normal.setZero();
                normal.x = s;
                tmin = t1;
            }

            // Pull the max down
            tmax = Math.min(tmax, t2);

            if (tmin > tmax) {
                argPool.pushVec2(4);
                return false;
            }
        }

        if (absD.y < JBoxSettings.EPSILON) {
            // Parallel.
            if (p.y < lowerBound.y || upperBound.y < p.y) {
                argPool.pushVec2(4);
                return false;
            }
        } else {
            final float inv_d = 1.0f / d.y;
            float t1 = (lowerBound.y - p.y) * inv_d;
            float t2 = (upperBound.y - p.y) * inv_d;

            // Sign of the normal vector.
            float s = -1.0f;

            if (t1 > t2) {
                final float temp = t1;
                t1 = t2;
                t2 = temp;
                s = 1.0f;
            }

            // Push the min up
            if (t1 > tmin) {
                normal.setZero();
                normal.y = s;
                tmin = t1;
            }

            // Pull the max down
            tmax = Math.min(tmax, t2);

            if (tmin > tmax) {
                argPool.pushVec2(4);
                return false;
            }
        }

        // Does the ray start inside the box?
        // Does the ray intersect beyond the max fraction?
        if (tmin < 0.0f || input.maxFraction < tmin) {
            argPool.pushVec2(4);
            return false;
        }

        // Intersection.
        output.fraction = tmin;
        output.normal.x = normal.x;
        output.normal.y = normal.y;
        argPool.pushVec2(4);
        return true;
    }

    public static boolean testOverlap(AABB a, AABB b) {
        if (b.lowerBound.x - a.upperBound.x > 0 || b.lowerBound.y - a.upperBound.y > 0
                || a.lowerBound.x - b.upperBound.x > 0 || a.lowerBound.y - b.upperBound.y > 0) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "AABB[" + lowerBound + " . " + upperBound + "]";
    }
}
