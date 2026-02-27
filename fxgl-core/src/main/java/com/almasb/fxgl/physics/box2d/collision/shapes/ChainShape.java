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
import com.almasb.fxgl.physics.box2d.common.Transform;

import static com.almasb.fxgl.core.math.FXGLMath.max;
import static com.almasb.fxgl.core.math.FXGLMath.min;

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

    private Vec2[] m_vertices = null;
    private int m_count = 0;

    private final Vec2 prevVertex = new Vec2();
    private final Vec2 nextVertex = new Vec2();
    private boolean hasPrevVertex = false;
    private boolean hasNextVertex = false;

    private final EdgeShape pool0 = new EdgeShape();

    public ChainShape(ChainType type, Vec2[] initialVertices) {
        super(ShapeType.CHAIN, JBoxSettings.polygonRadius);

        if (type == ChainType.CLOSED) {
            createLoop(initialVertices, initialVertices.length);
        } else {
            createChain(initialVertices, initialVertices.length);
        }
    }

    @Override
    public Shape clone() {
        ChainShape clone = new ChainShape(ChainType.OPEN, m_vertices);
        clone.prevVertex.set(prevVertex);
        clone.nextVertex.set(nextVertex);
        clone.hasPrevVertex = hasPrevVertex;
        clone.hasNextVertex = hasNextVertex;
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
    public boolean containsPoint(Transform xf, Vec2 point) {
        return false;
    }

    @Override
    public boolean raycast(RayCastOutput output, RayCastInput input, Transform xf, int childIndex) {
        int i1 = childIndex;
        int i2 = childIndex + 1;
        if (i2 == m_count) {
            i2 = 0;
        }

        EdgeShape edgeShape = pool0;
        edgeShape.m_vertex1.set(m_vertices[i1]);
        edgeShape.m_vertex2.set(m_vertices[i2]);

        return edgeShape.raycast(output, input, xf, 0);
    }

    @Override
    public void computeAABB(AABB aabb, Transform transform, int childIndex) {
        final Vec2 lower = aabb.lowerBound;
        final Vec2 upper = aabb.upperBound;

        int i1 = childIndex;
        int i2 = childIndex + 1;
        if (i2 == m_count) {
            i2 = 0;
        }

        Vec2 vi1 = m_vertices[i1];
        Vec2 vi2 = m_vertices[i2];

        float v1x = transform.mulX(vi1);
        float v1y = transform.mulY(vi1);

        float v2x = transform.mulX(vi2);
        float v2y = transform.mulY(vi2);

        lower.x = min(v1x, v2x);
        lower.y = min(v1y, v2y);
        upper.x = max(v1x, v2x);
        upper.y = max(v1y, v2y);
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

        edge.m_vertex1.set(m_vertices[index + 0]);
        edge.m_vertex2.set(m_vertices[index + 1]);

        if (index > 0) {
            edge.m_vertex0.set(m_vertices[index - 1]);
            edge.m_hasVertex0 = true;
        } else {
            edge.m_vertex0.set(prevVertex);
            edge.m_hasVertex0 = hasPrevVertex;
        }

        if (index < m_count - 2) {
            edge.m_vertex3.set(m_vertices[index + 2]);
            edge.m_hasVertex3 = true;
        } else {
            edge.m_vertex3.set(nextVertex);
            edge.m_hasVertex3 = hasNextVertex;
        }
    }

    /**
     * Create a loop. This automatically adjusts connectivity.
     *
     * @param vertices an array of vertices, these are copied
     * @param count the vertex count
     */
    private void createLoop(final Vec2[] vertices, int count) {
        assert m_vertices == null && m_count == 0;
        assert count >= 3;
        m_count = count + 1;
        m_vertices = new Vec2[m_count];
        for (int i = 1; i < count; i++) {
            Vec2 v1 = vertices[i - 1];
            Vec2 v2 = vertices[i];
            // If the code crashes here, it means your vertices are too close together.
            if (v1.distanceSquared(v2) < JBoxSettings.linearSlop * JBoxSettings.linearSlop) {
                throw new RuntimeException("Vertices of chain shape are too close together");
            }
        }
        for (int i = 0; i < count; i++) {
            m_vertices[i] = new Vec2(vertices[i]);
        }
        m_vertices[count] = new Vec2(m_vertices[0]);
        prevVertex.set(m_vertices[m_count - 2]);
        nextVertex.set(m_vertices[1]);
        hasPrevVertex = true;
        hasNextVertex = true;
    }

    /**
     * Create a chain with isolated end vertices.
     *
     * @param vertices an array of vertices, these are copied
     * @param count the vertex count
     */
    private void createChain(final Vec2[] vertices, int count) {
        assert m_vertices == null && m_count == 0;
        assert count >= 2;
        m_count = count;
        m_vertices = new Vec2[m_count];
        for (int i = 1; i < m_count; i++) {
            Vec2 v1 = vertices[i - 1];
            Vec2 v2 = vertices[i];
            // If the code crashes here, it means your vertices are too close together.
            if (v1.distanceSquared(v2) < JBoxSettings.linearSlop * JBoxSettings.linearSlop) {
                throw new RuntimeException("Vertices of chain shape are too close together");
            }
        }
        for (int i = 0; i < m_count; i++) {
            m_vertices[i] = new Vec2(vertices[i]);
        }
        hasPrevVertex = false;
        hasNextVertex = false;

        prevVertex.setZero();
        nextVertex.setZero();
    }

    /**
     * Establish connectivity to a vertex that precedes the first vertex. Don't call this for loops.
     */
    public void setPrevVertex(final Vec2 prevVertex) {
        this.prevVertex.set(prevVertex);
        hasPrevVertex = true;
    }

    /**
     * Establish connectivity to a vertex that follows the last vertex. Don't call this for loops.
     */
    public void setNextVertex(final Vec2 nextVertex) {
        this.nextVertex.set(nextVertex);
        hasNextVertex = true;
    }

    public int getCount() {
        return m_count;
    }

    public Vec2 getVertex(int index) {
        return m_vertices[index];
    }
}
