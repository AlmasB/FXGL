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
 * A chain shape is a free form sequence of line segments.
 * The chain has two-sided collision, so you can use inside and outside collision.
 * Therefore, you may use any winding order.
 * Connectivity information is used to create smooth collisions.
 *
 * WARNING: The chain will not collide properly if there are self-intersections.
 *
 * @author Daniel
 */
public final class ChainShape extends Shape {

    public Vec2[] m_vertices = null;
    public int m_count = 0;

    private final Vec2 m_prevVertex = new Vec2(), m_nextVertex = new Vec2();
    private boolean m_hasPrevVertex = false, m_hasNextVertex = false;

    private final EdgeShape pool0 = new EdgeShape();

    public ChainShape() {
        super(ShapeType.CHAIN, JBoxSettings.polygonRadius);
    }

    @Override
    public Shape clone() {
        ChainShape clone = new ChainShape();
        clone.createChain(m_vertices, m_count);
        clone.m_prevVertex.set(m_prevVertex);
        clone.m_nextVertex.set(m_nextVertex);
        clone.m_hasPrevVertex = m_hasPrevVertex;
        clone.m_hasNextVertex = m_hasNextVertex;
        return clone;
    }

    @Override
    public int getChildCount() {
        return m_count - 1;
    }

    @Override
    public float computeDistanceToOut(Transform xf, Vec2 p, int childIndex, Vec2 normalOut) {
        final EdgeShape edge = pool0;
        getChildEdge(edge, childIndex);
        return edge.computeDistanceToOut(xf, p, 0, normalOut);
    }

    @Override
    public boolean testPoint(Transform xf, Vec2 p) {
        return false;
    }

    @Override
    public boolean raycast(RayCastOutput output, RayCastInput input, Transform xf, int childIndex) {
        assert childIndex < m_count;

        final EdgeShape edgeShape = pool0;

        int i1 = childIndex;
        int i2 = childIndex + 1;
        if (i2 == m_count) {
            i2 = 0;
        }
        Vec2 v = m_vertices[i1];
        edgeShape.m_vertex1.x = v.x;
        edgeShape.m_vertex1.y = v.y;
        Vec2 v1 = m_vertices[i2];
        edgeShape.m_vertex2.x = v1.x;
        edgeShape.m_vertex2.y = v1.y;

        return edgeShape.raycast(output, input, xf, 0);
    }

    @Override
    @SuppressWarnings("PMD.UselessParentheses")
    public void computeAABB(AABB aabb, Transform xf, int childIndex) {
        assert childIndex < m_count;
        final Vec2 lower = aabb.lowerBound;
        final Vec2 upper = aabb.upperBound;

        int i1 = childIndex;
        int i2 = childIndex + 1;
        if (i2 == m_count) {
            i2 = 0;
        }

        final Vec2 vi1 = m_vertices[i1];
        final Vec2 vi2 = m_vertices[i2];
        final Rotation xfq = xf.q;
        final Vec2 xfp = xf.p;
        float v1x = (xfq.c * vi1.x - xfq.s * vi1.y) + xfp.x;
        float v1y = (xfq.s * vi1.x + xfq.c * vi1.y) + xfp.y;
        float v2x = (xfq.c * vi2.x - xfq.s * vi2.y) + xfp.x;
        float v2y = (xfq.s * vi2.x + xfq.c * vi2.y) + xfp.y;

        lower.x = v1x < v2x ? v1x : v2x;
        lower.y = v1y < v2y ? v1y : v2y;
        upper.x = v1x > v2x ? v1x : v2x;
        upper.y = v1y > v2y ? v1y : v2y;
    }

    @Override
    public void computeMass(MassData massData, float density) {
        massData.mass = 0.0f;
        massData.center.setZero();
        massData.I = 0.0f;
    }

    /**
     * Get a child edge.
     */
    public void getChildEdge(EdgeShape edge, int index) {
        assert 0 <= index && index < m_count - 1;

        edge.setRadius(getRadius());

        final Vec2 v0 = m_vertices[index + 0];
        final Vec2 v1 = m_vertices[index + 1];
        edge.m_vertex1.x = v0.x;
        edge.m_vertex1.y = v0.y;
        edge.m_vertex2.x = v1.x;
        edge.m_vertex2.y = v1.y;

        if (index > 0) {
            Vec2 v = m_vertices[index - 1];
            edge.m_vertex0.x = v.x;
            edge.m_vertex0.y = v.y;
            edge.m_hasVertex0 = true;
        } else {
            edge.m_vertex0.x = m_prevVertex.x;
            edge.m_vertex0.y = m_prevVertex.y;
            edge.m_hasVertex0 = m_hasPrevVertex;
        }

        if (index < m_count - 2) {
            Vec2 v = m_vertices[index + 2];
            edge.m_vertex3.x = v.x;
            edge.m_vertex3.y = v.y;
            edge.m_hasVertex3 = true;
        } else {
            edge.m_vertex3.x = m_nextVertex.x;
            edge.m_vertex3.y = m_nextVertex.y;
            edge.m_hasVertex3 = m_hasNextVertex;
        }
    }

    /**
     * Create a loop. This automatically adjusts connectivity.
     *
     * @param vertices an array of vertices, these are copied
     * @param count the vertex count
     */
    public void createLoop(final Vec2[] vertices, int count) {
        assert m_vertices == null && m_count == 0;
        assert count >= 3;
        m_count = count + 1;
        m_vertices = new Vec2[m_count];
        for (int i = 1; i < count; i++) {
            Vec2 v1 = vertices[i - 1];
            Vec2 v2 = vertices[i];
            // If the code crashes here, it means your vertices are too close together.
            if (JBoxUtils.distanceSquared(v1, v2) < JBoxSettings.linearSlop * JBoxSettings.linearSlop) {
                throw new RuntimeException("Vertices of chain shape are too close together");
            }
        }
        for (int i = 0; i < count; i++) {
            m_vertices[i] = new Vec2(vertices[i]);
        }
        m_vertices[count] = new Vec2(m_vertices[0]);
        m_prevVertex.set(m_vertices[m_count - 2]);
        m_nextVertex.set(m_vertices[1]);
        m_hasPrevVertex = true;
        m_hasNextVertex = true;
    }

    /**
     * Create a chain with isolated end vertices.
     *
     * @param vertices an array of vertices, these are copied
     * @param count the vertex count
     */
    public void createChain(final Vec2 vertices[], int count) {
        assert m_vertices == null && m_count == 0;
        assert count >= 2;
        m_count = count;
        m_vertices = new Vec2[m_count];
        for (int i = 1; i < m_count; i++) {
            Vec2 v1 = vertices[i - 1];
            Vec2 v2 = vertices[i];
            // If the code crashes here, it means your vertices are too close together.
            if (JBoxUtils.distanceSquared(v1, v2) < JBoxSettings.linearSlop * JBoxSettings.linearSlop) {
                throw new RuntimeException("Vertices of chain shape are too close together");
            }
        }
        for (int i = 0; i < m_count; i++) {
            m_vertices[i] = new Vec2(vertices[i]);
        }
        m_hasPrevVertex = false;
        m_hasNextVertex = false;

        m_prevVertex.setZero();
        m_nextVertex.setZero();
    }

    /**
     * Establish connectivity to a vertex that precedes the first vertex. Don't call this for loops.
     *
     * @param prevVertex
     */
    public void setPrevVertex(final Vec2 prevVertex) {
        m_prevVertex.set(prevVertex);
        m_hasPrevVertex = true;
    }

    /**
     * Establish connectivity to a vertex that follows the last vertex. Don't call this for loops.
     *
     * @param nextVertex
     */
    public void setNextVertex(final Vec2 nextVertex) {
        m_nextVertex.set(nextVertex);
        m_hasNextVertex = true;
    }
}
