/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics.box2d.collision;

import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.physics.box2d.collision.shapes.*;
import com.almasb.fxgl.physics.box2d.common.JBoxSettings;

/**
 * A distance proxy is used by the GJK algorithm. It encapsulates any shape. jbox2dTODO: see if we can
 * just do assignments with m_vertices, instead of copying stuff over
 *
 * @author daniel
 */
class DistanceProxy {
    public final Vec2[] m_vertices = new Vec2[JBoxSettings.maxPolygonVertices];
    public int m_count = 0;
    public float m_radius = 0f;
    public final Vec2[] m_buffer = new Vec2[2];

    public DistanceProxy() {
        for (int i = 0; i < m_vertices.length; i++) {
            m_vertices[i] = new Vec2();
        }
    }

    /**
     * Initialize the proxy using the given shape. The shape must remain in scope while the proxy is
     * in use.
     */
    public final void set(final Shape shape, int index) {
        switch (shape.getType()) {
            case CIRCLE:
                final CircleShape circle = (CircleShape) shape;
                m_vertices[0].set(circle.center);
                m_count = 1;
                m_radius = circle.getRadius();

                break;
            case POLYGON:
                final PolygonShape poly = (PolygonShape) shape;
                m_count = poly.getVertexCount();
                m_radius = poly.getRadius();
                for (int i = 0; i < m_count; i++) {
                    m_vertices[i].set(poly.m_vertices[i]);
                }
                break;
            case CHAIN:
                final ChainShape chain = (ChainShape) shape;
                assert 0 <= index && index < chain.getCount();

                m_buffer[0] = chain.getVertex(index);
                if (index + 1 < chain.getCount()) {
                    m_buffer[1] = chain.getVertex(index + 1);
                } else {
                    m_buffer[1] = chain.getVertex(0);
                }

                m_vertices[0].set(m_buffer[0]);
                m_vertices[1].set(m_buffer[1]);
                m_count = 2;
                m_radius = chain.getRadius();
                break;
            case EDGE:
                EdgeShape edge = (EdgeShape) shape;
                m_vertices[0].set(edge.m_vertex1);
                m_vertices[1].set(edge.m_vertex2);
                m_count = 2;
                m_radius = edge.getRadius();
                break;
            default:
                assert false;
        }
    }

    /**
     * Get the supporting vertex index in the given direction.
     *
     * @param d
     * @return
     */
    public final int getSupport(final Vec2 d) {
        int bestIndex = 0;
        float bestValue = Vec2.dot(m_vertices[0], d);
        for (int i = 1; i < m_count; i++) {
            float value = Vec2.dot(m_vertices[i], d);
            if (value > bestValue) {
                bestIndex = i;
                bestValue = value;
            }
        }

        return bestIndex;
    }

    /**
     * Used by Distance.
     *
     * @return a vertex by index
     */
    public final Vec2 getVertex(int index) {
        assert 0 <= index && index < m_count;
        return m_vertices[index];
    }
}
