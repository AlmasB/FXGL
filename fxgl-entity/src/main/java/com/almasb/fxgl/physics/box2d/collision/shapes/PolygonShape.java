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
 * A convex polygon shape.
 * Polygons have a maximum number of vertices equal to JBoxSettings.maxPolygonVertices.
 * In most cases you should not need many vertices for a convex polygon.
 */
public final class PolygonShape extends Shape {

    /**
     * Local position of the shape centroid in parent body frame.
     */
    public final Vec2 m_centroid = new Vec2();

    /**
     * The vertices of the shape. Note: use getVertexCount(), not m_vertices.length, to get number of
     * active vertices.
     */
    public final Vec2[] m_vertices = new Vec2[JBoxSettings.maxPolygonVertices];

    /**
     * The normals of the shape. Note: use getVertexCount(), not m_normals.length, to get number of
     * active normals.
     */
    public final Vec2[] m_normals = new Vec2[JBoxSettings.maxPolygonVertices];

    /**
     * Number of active vertices in the shape.
     */
    private int vertexCount = 0;

    // pooling
    private final Vec2 pool1 = new Vec2();
    private final Vec2 pool2 = new Vec2();
    private final Vec2 pool3 = new Vec2();
    private final Vec2 pool4 = new Vec2();
    private Transform poolt1 = new Transform();

    public PolygonShape() {
        super(ShapeType.POLYGON, JBoxSettings.polygonRadius);

        for (int i = 0; i < m_vertices.length; i++) {
            m_vertices[i] = new Vec2();
        }

        for (int i = 0; i < m_normals.length; i++) {
            m_normals[i] = new Vec2();
        }

        m_centroid.setZero();
    }

    @Override
    public Shape clone() {
        PolygonShape shape = new PolygonShape();
        shape.m_centroid.set(this.m_centroid);
        for (int i = 0; i < shape.m_normals.length; i++) {
            shape.m_normals[i].set(m_normals[i]);
            shape.m_vertices[i].set(m_vertices[i]);
        }
        shape.setRadius(this.getRadius());
        shape.vertexCount = this.vertexCount;
        return shape;
    }

    @Override
    public int getChildCount() {
        return 1;
    }

    @Override
    public boolean testPoint(final Transform xf, final Vec2 p) {
        float tempx, tempy;
        final Rotation xfq = xf.q;

        tempx = p.x - xf.p.x;
        tempy = p.y - xf.p.y;
        final float pLocalx = xfq.c * tempx + xfq.s * tempy;
        final float pLocaly = -xfq.s * tempx + xfq.c * tempy;

        for (int i = 0; i < vertexCount; ++i) {
            Vec2 vertex = m_vertices[i];
            Vec2 normal = m_normals[i];
            tempx = pLocalx - vertex.x;
            tempy = pLocaly - vertex.y;
            final float dot = normal.x * tempx + normal.y * tempy;
            if (dot > 0.0f) {
                return false;
            }
        }

        return true;
    }

    @Override
    @SuppressWarnings("PMD.UselessParentheses")
    public void computeAABB(final AABB aabb, final Transform xf, int childIndex) {
        final Vec2 lower = aabb.lowerBound;
        final Vec2 upper = aabb.upperBound;
        final Vec2 v1 = m_vertices[0];
        final float xfqc = xf.q.c;
        final float xfqs = xf.q.s;
        final float xfpx = xf.p.x;
        final float xfpy = xf.p.y;
        lower.x = (xfqc * v1.x - xfqs * v1.y) + xfpx;
        lower.y = (xfqs * v1.x + xfqc * v1.y) + xfpy;
        upper.x = lower.x;
        upper.y = lower.y;

        for (int i = 1; i < vertexCount; ++i) {
            Vec2 v2 = m_vertices[i];
            // Vec2 v = Mul(xf, m_vertices[i]);
            float vx = (xfqc * v2.x - xfqs * v2.y) + xfpx;
            float vy = (xfqs * v2.x + xfqc * v2.y) + xfpy;
            lower.x = lower.x < vx ? lower.x : vx;
            lower.y = lower.y < vy ? lower.y : vy;
            upper.x = upper.x > vx ? upper.x : vx;
            upper.y = upper.y > vy ? upper.y : vy;
        }

        lower.x -= getRadius();
        lower.y -= getRadius();
        upper.x += getRadius();
        upper.y += getRadius();
    }

    @Override
    public float computeDistanceToOut(Transform xf, Vec2 p, int childIndex, Vec2 normalOut) {
        float xfqc = xf.q.c;
        float xfqs = xf.q.s;
        float tx = p.x - xf.p.x;
        float ty = p.y - xf.p.y;
        float pLocalx = xfqc * tx + xfqs * ty;
        float pLocaly = -xfqs * tx + xfqc * ty;

        float maxDistance = -Float.MAX_VALUE;
        float normalForMaxDistanceX = pLocalx;
        float normalForMaxDistanceY = pLocaly;

        for (int i = 0; i < vertexCount; ++i) {
            Vec2 vertex = m_vertices[i];
            Vec2 normal = m_normals[i];
            tx = pLocalx - vertex.x;
            ty = pLocaly - vertex.y;
            float dot = normal.x * tx + normal.y * ty;
            if (dot > maxDistance) {
                maxDistance = dot;
                normalForMaxDistanceX = normal.x;
                normalForMaxDistanceY = normal.y;
            }
        }

        float distance;
        if (maxDistance > 0) {
            float minDistanceX = normalForMaxDistanceX;
            float minDistanceY = normalForMaxDistanceY;
            float minDistance2 = maxDistance * maxDistance;
            for (int i = 0; i < vertexCount; ++i) {
                Vec2 vertex = m_vertices[i];
                float distanceVecX = pLocalx - vertex.x;
                float distanceVecY = pLocaly - vertex.y;
                float distance2 = distanceVecX * distanceVecX + distanceVecY * distanceVecY;
                if (minDistance2 > distance2) {
                    minDistanceX = distanceVecX;
                    minDistanceY = distanceVecY;
                    minDistance2 = distance2;
                }
            }
            distance = JBoxUtils.sqrt(minDistance2);
            normalOut.x = xfqc * minDistanceX - xfqs * minDistanceY;
            normalOut.y = xfqs * minDistanceX + xfqc * minDistanceY;
            normalOut.getLengthAndNormalize();
        } else {
            distance = maxDistance;
            normalOut.x = xfqc * normalForMaxDistanceX - xfqs * normalForMaxDistanceY;
            normalOut.y = xfqs * normalForMaxDistanceX + xfqc * normalForMaxDistanceY;
        }

        return distance;
    }

    @Override
    public boolean raycast(RayCastOutput output, RayCastInput input, Transform xf, int childIndex) {
        final float xfqc = xf.q.c;
        final float xfqs = xf.q.s;
        final Vec2 xfp = xf.p;
        float tempx, tempy;
        // b2Vec2 p1 = b2MulT(xf.q, input.p1 - xf.p);
        // b2Vec2 p2 = b2MulT(xf.q, input.p2 - xf.p);
        tempx = input.p1.x - xfp.x;
        tempy = input.p1.y - xfp.y;
        final float p1x = xfqc * tempx + xfqs * tempy;
        final float p1y = -xfqs * tempx + xfqc * tempy;

        tempx = input.p2.x - xfp.x;
        tempy = input.p2.y - xfp.y;
        final float p2x = xfqc * tempx + xfqs * tempy;
        final float p2y = -xfqs * tempx + xfqc * tempy;

        final float dx = p2x - p1x;
        final float dy = p2y - p1y;

        float lower = 0, upper = input.maxFraction;

        int index = -1;

        for (int i = 0; i < vertexCount; ++i) {
            Vec2 normal = m_normals[i];
            Vec2 vertex = m_vertices[i];
            // p = p1 + a * d
            // dot(normal, p - v) = 0
            // dot(normal, p1 - v) + a * dot(normal, d) = 0
            float tempxn = vertex.x - p1x;
            float tempyn = vertex.y - p1y;
            final float numerator = normal.x * tempxn + normal.y * tempyn;
            final float denominator = normal.x * dx + normal.y * dy;

            if (denominator == 0.0f) {
                if (numerator < 0.0f) {
                    return false;
                }
            } else {
                // Note: we want this predicate without division:
                // lower < numerator / denominator, where denominator < 0
                // Since denominator < 0, we have to flip the inequality:
                // lower < numerator / denominator <==> denominator * lower >
                // numerator.
                if (denominator < 0.0f && numerator < lower * denominator) {
                    // Increase lower.
                    // The segment enters this half-space.
                    lower = numerator / denominator;
                    index = i;
                } else if (denominator > 0.0f && numerator < upper * denominator) {
                    // Decrease upper.
                    // The segment exits this half-space.
                    upper = numerator / denominator;
                }
            }

            if (upper < lower) {
                return false;
            }
        }

        assert 0.0f <= lower && lower <= input.maxFraction;

        if (index >= 0) {
            output.fraction = lower;
            // normal = Mul(xf.R, m_normals[index]);
            Vec2 normal = m_normals[index];
            Vec2 out = output.normal;
            out.x = xfqc * normal.x - xfqs * normal.y;
            out.y = xfqs * normal.x + xfqc * normal.y;
            return true;
        }
        return false;
    }

    @Override
    public void computeMass(final MassData massData, float density) {
        // Polygon mass, centroid, and inertia.
        // Let rho be the polygon density in mass per unit area.
        // Then:
        // mass = rho * int(dA)
        // centroid.x = (1/mass) * rho * int(x * dA)
        // centroid.y = (1/mass) * rho * int(y * dA)
        // I = rho * int((x*x + y*y) * dA)
        //
        // We can compute these integrals by summing all the integrals
        // for each triangle of the polygon. To evaluate the integral
        // for a single triangle, we make a change of variables to
        // the (u,v) coordinates of the triangle:
        // x = x0 + e1x * u + e2x * v
        // y = y0 + e1y * u + e2y * v
        // where 0 <= u && 0 <= v && u + v <= 1.
        //
        // We integrate u from [0,1-v] and then v from [0,1].
        // We also need to use the Jacobian of the transformation:
        // D = cross(e1, e2)
        //
        // Simplification: triangle centroid = (1/3) * (p1 + p2 + p3)
        //
        // The rest of the derivation is handled by computer algebra.

        assert vertexCount >= 3;

        final Vec2 center = pool1;
        center.setZero();
        float area = 0.0f;
        float I = 0.0f;

        // pRef is the reference point for forming triangles.
        // It's location doesn't change the result (except for rounding error).
        final Vec2 s = pool2;
        s.setZero();
        // This code would put the reference point inside the polygon.
        for (int i = 0; i < vertexCount; ++i) {
            s.addLocal(m_vertices[i]);
        }
        s.mulLocal(1.0f / vertexCount);

        final float k_inv3 = 1.0f / 3.0f;

        final Vec2 e1 = pool3;
        final Vec2 e2 = pool4;

        for (int i = 0; i < vertexCount; ++i) {
            // Triangle vertices.
            e1.set(m_vertices[i]).subLocal(s);
            e2.set(s).negateLocal().addLocal(i + 1 < vertexCount ? m_vertices[i + 1] : m_vertices[0]);

            final float D = Vec2.cross(e1, e2);

            final float triangleArea = 0.5f * D;
            area += triangleArea;

            // Area weighted centroid
            center.x += triangleArea * k_inv3 * (e1.x + e2.x);
            center.y += triangleArea * k_inv3 * (e1.y + e2.y);

            final float ex1 = e1.x, ey1 = e1.y;
            final float ex2 = e2.x, ey2 = e2.y;

            float intx2 = ex1 * ex1 + ex2 * ex1 + ex2 * ex2;
            float inty2 = ey1 * ey1 + ey2 * ey1 + ey2 * ey2;

            I += (0.25f * k_inv3 * D) * (intx2 + inty2);
        }

        // Total mass
        massData.mass = density * area;

        // Center of mass
        assert area > JBoxSettings.EPSILON;
        center.mulLocal(1.0f / area);
        massData.center.set(center).addLocal(s);

        // Inertia tensor relative to the local origin (point s)
        massData.I = I * density;

        // Shift to center of mass then to original body origin.
        massData.I += massData.mass * (Vec2.dot(massData.center, massData.center));
    }

    public void set(Vec2[] vertices) {
        setImpl(vertices, vertices.length);
    }

    /**
     * Create a convex hull from the given array of points. The count must be in the range [3,
     * JBoxSettings.maxPolygonVertices].
     *
     * @warning the points may be re-ordered, even if they form a convex polygon.
     * @warning collinear points are removed.
     */
    public void set(final Vec2[] vertices, final int count) {
        setImpl(vertices, count);
    }

    private void setImpl(final Vec2[] verts, final int num) {
        assert 3 <= num && num <= JBoxSettings.maxPolygonVertices;

        if (num < 3) {
            setAsBox(1.0f, 1.0f);
            return;
        }

        int n = Math.min(num, JBoxSettings.maxPolygonVertices);

        // Perform welding and copy vertices into local buffer.
        Vec2[] ps = new Vec2[JBoxSettings.maxPolygonVertices];

        int tempCount = 0;
        for (int i = 0; i < n; ++i) {
            Vec2 v = verts[i];
            boolean unique = true;
            for (int j = 0; j < tempCount; ++j) {
                if (JBoxUtils.distanceSquared(v, ps[j]) < 0.5f * JBoxSettings.linearSlop) {
                    unique = false;
                    break;
                }
            }

            if (unique) {
                ps[tempCount++] = v;
            }
        }

        n = tempCount;
        if (n < 3) {
            // Polygon is degenerate.
            assert false;
            setAsBox(1.0f, 1.0f);
            return;
        }

        // Create the convex hull using the Gift wrapping algorithm
        // http://en.wikipedia.org/wiki/Gift_wrapping_algorithm

        // Find the right most point on the hull
        int i0 = 0;
        float x0 = ps[0].x;
        for (int i = 1; i < n; ++i) {
            float x = ps[i].x;
            if (x > x0 || x == x0 && ps[i].y < ps[i0].y) {
                i0 = i;
                x0 = x;
            }
        }

        int[] hull = new int[JBoxSettings.maxPolygonVertices];
        int m = 0;
        int ih = i0;

        while (true) {
            hull[m] = ih;

            int ie = 0;
            for (int j = 1; j < n; ++j) {
                if (ie == ih) {
                    ie = j;
                    continue;
                }

                Vec2 r = pool1.set(ps[ie]).subLocal(ps[hull[m]]);
                Vec2 v = pool2.set(ps[j]).subLocal(ps[hull[m]]);
                float c = Vec2.cross(r, v);
                if (c < 0.0f) {
                    ie = j;
                }

                // Collinearity check
                if (c == 0.0f && v.lengthSquared() > r.lengthSquared()) {
                    ie = j;
                }
            }

            ++m;
            ih = ie;

            if (ie == i0) {
                break;
            }
        }

        this.vertexCount = m;

        // Copy vertices.
        for (int i = 0; i < vertexCount; ++i) {
            if (m_vertices[i] == null) {
                m_vertices[i] = new Vec2();
            }
            m_vertices[i].set(ps[hull[i]]);
        }

        final Vec2 edge = pool1;

        // Compute normals. Ensure the edges have non-zero length.
        for (int i = 0; i < vertexCount; ++i) {
            final int i1 = i;
            final int i2 = i + 1 < vertexCount ? i + 1 : 0;
            edge.set(m_vertices[i2]).subLocal(m_vertices[i1]);

            assert edge.lengthSquared() > JBoxSettings.EPSILON * JBoxSettings.EPSILON;
            Vec2.crossToOutUnsafe(edge, 1f, m_normals[i]);
            m_normals[i].getLengthAndNormalize();
        }

        // Compute the polygon centroid.
        computeCentroidToOut(m_vertices, vertexCount, m_centroid);
    }

    /**
     * Build vertices to represent an axis-aligned box.
     *
     * @param hx the half-width.
     * @param hy the half-height.
     */
    public void setAsBox(final float hx, final float hy) {
        vertexCount = 4;
        m_vertices[0].set(-hx, -hy);
        m_vertices[1].set(hx, -hy);
        m_vertices[2].set(hx, hy);
        m_vertices[3].set(-hx, hy);
        m_normals[0].set(0.0f, -1.0f);
        m_normals[1].set(1.0f, 0.0f);
        m_normals[2].set(0.0f, 1.0f);
        m_normals[3].set(-1.0f, 0.0f);
        m_centroid.setZero();
    }

    /**
     * Build vertices to represent an oriented box.
     *
     * @param hx the half-width.
     * @param hy the half-height.
     * @param center the center of the box in local coordinates.
     * @param angle the rotation of the box in local coordinates.
     */
    public void setAsBox(final float hx, final float hy, final Vec2 center, final float angle) {
        vertexCount = 4;
        m_vertices[0].set(-hx, -hy);
        m_vertices[1].set(hx, -hy);
        m_vertices[2].set(hx, hy);
        m_vertices[3].set(-hx, hy);
        m_normals[0].set(0.0f, -1.0f);
        m_normals[1].set(1.0f, 0.0f);
        m_normals[2].set(0.0f, 1.0f);
        m_normals[3].set(-1.0f, 0.0f);
        m_centroid.set(center);

        final Transform xf = poolt1;
        xf.p.set(center);
        xf.q.set(angle);

        // Transform vertices and normals.
        for (int i = 0; i < vertexCount; ++i) {
            Transform.mulToOut(xf, m_vertices[i], m_vertices[i]);
            Rotation.mulToOut(xf.q, m_normals[i], m_normals[i]);
        }
    }

    public int getVertexCount() {
        return vertexCount;
    }

    /**
     * Get a vertex by index.
     *
     * @param index
     * @return
     */
    public Vec2 getVertex(final int index) {
        assert 0 <= index && index < vertexCount;
        return m_vertices[index];
    }

    public void computeCentroidToOut(final Vec2[] vs, final int count, final Vec2 out) {
        assert count >= 3;

        out.set(0.0f, 0.0f);
        float area = 0.0f;

        // pRef is the reference point for forming triangles.
        // It's location doesn't change the result (except for rounding error).
        final Vec2 pRef = pool1;
        pRef.setZero();

        final Vec2 e1 = pool2;
        final Vec2 e2 = pool3;

        final float inv3 = 1.0f / 3.0f;

        for (int i = 0; i < count; ++i) {
            // Triangle vertices.
            final Vec2 p1 = pRef;
            final Vec2 p2 = vs[i];
            final Vec2 p3 = i + 1 < count ? vs[i + 1] : vs[0];

            e1.set(p2).subLocal(p1);
            e2.set(p3).subLocal(p1);

            final float D = Vec2.cross(e1, e2);

            final float triangleArea = 0.5f * D;
            area += triangleArea;

            // Area weighted centroid
            e1.set(p1).addLocal(p2).addLocal(p3).mulLocal(triangleArea * inv3);
            out.addLocal(e1);
        }

        // Centroid
        assert area > JBoxSettings.EPSILON;
        out.mulLocal(1.0f / area);
    }

    /**
     * Validate convexity. This is a very time consuming operation.
     *
     * @return
     */
    public boolean validate() {
        for (int i = 0; i < vertexCount; ++i) {
            int i1 = i;
            int i2 = i < vertexCount - 1 ? i1 + 1 : 0;
            Vec2 p = m_vertices[i1];
            Vec2 e = pool1.set(m_vertices[i2]).subLocal(p);

            for (int j = 0; j < vertexCount; ++j) {
                if (j == i1 || j == i2) {
                    continue;
                }

                Vec2 v = pool2.set(m_vertices[j]).subLocal(p);
                float c = Vec2.cross(e, v);
                if (c < 0.0f) {
                    return false;
                }
            }
        }

        return true;
    }

    /** Get the vertices in local coordinates. */
    public Vec2[] getVertices() {
        return m_vertices;
    }

    /** Get the edge normal vectors. There is one for each vertex. */
    public Vec2[] getNormals() {
        return m_normals;
    }

    /** Get the centroid and apply the supplied transform. */
    public Vec2 centroid(final Transform xf) {
        return Transform.mul(xf, m_centroid);
    }

    /** Get the centroid and apply the supplied transform. */
    public Vec2 centroidToOut(final Transform xf, final Vec2 out) {
        Transform.mulToOutUnsafe(xf, m_centroid, out);
        return out;
    }
}
