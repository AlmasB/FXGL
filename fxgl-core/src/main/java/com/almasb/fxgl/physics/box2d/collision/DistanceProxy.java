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
 * A distance proxy is used by the GJK algorithm.
 * It encapsulates any shape.
 *
 * @author daniel
 */
final class DistanceProxy {
    private final Vec2[] vertices = new Vec2[JBoxSettings.maxPolygonVertices];
    private final Vec2[] buffer = new Vec2[2];
    private int count = 0;
    private float radius = 0f;

    DistanceProxy() {
        for (int i = 0; i < vertices.length; i++) {
            vertices[i] = new Vec2();
        }
    }

    float getRadius() {
        return radius;
    }

    /**
     * Initialize the proxy using the given shape.
     * The shape must remain in scope while the proxy is in use.
     */
    void set(Shape shape, int index) {
        switch (shape.getType()) {
            case CIRCLE -> {
                CircleShape circle = (CircleShape) shape;
                vertices[0].set(circle.center);
                count = 1;
                radius = circle.getRadius();
            }

            case POLYGON -> {
                PolygonShape poly = (PolygonShape) shape;
                count = poly.getVertexCount();
                radius = poly.getRadius();
                for (int i = 0; i < count; i++) {
                    vertices[i].set(poly.m_vertices[i]);
                }
            }

            case CHAIN -> {
                ChainShape chain = (ChainShape) shape;
                assert 0 <= index && index < chain.getCount();
                buffer[0] = chain.getVertex(index);
                if (index + 1 < chain.getCount()) {
                    buffer[1] = chain.getVertex(index + 1);
                } else {
                    buffer[1] = chain.getVertex(0);
                }
                vertices[0].set(buffer[0]);
                vertices[1].set(buffer[1]);
                count = 2;
                radius = chain.getRadius();
            }

            case EDGE -> {
                EdgeShape edge = (EdgeShape) shape;
                vertices[0].set(edge.m_vertex1);
                vertices[1].set(edge.m_vertex2);
                count = 2;
                radius = edge.getRadius();
            }
        }
    }

    /**
     * @return the supporting vertex index in the given direction
     */
    int getSupport(Vec2 d) {
        int bestIndex = 0;
        float bestValue = Vec2.dot(vertices[0], d);

        for (int i = 1; i < count; i++) {
            float value = Vec2.dot(vertices[i], d);
            if (value > bestValue) {
                bestIndex = i;
                bestValue = value;
            }
        }

        return bestIndex;
    }

    /**
     * @return a vertex by index
     */
    Vec2 getVertex(int index) {
        assert 0 <= index && index < count;
        return vertices[index];
    }
}
