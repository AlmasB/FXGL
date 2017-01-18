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

package org.jbox2d.dynamics;

import com.almasb.fxgl.core.math.Vec2;
import javafx.scene.paint.Color;
import org.jbox2d.callbacks.DebugDraw;
import org.jbox2d.collision.AABB;
import org.jbox2d.collision.shapes.ChainShape;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.EdgeShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.JBoxSettings;
import org.jbox2d.common.Transform;
import org.jbox2d.dynamics.contacts.Contact;
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.joints.PulleyJoint;
import org.jbox2d.particle.ParticleColor;
import org.jbox2d.particle.ParticleSystem;
import org.jbox2d.pooling.arrays.Vec2Array;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class WorldView {

    private DebugDraw debugDraw;
    private World world;

    private final Transform xf = new Transform();
    private final Vec2 cA = new Vec2();
    private final Vec2 cB = new Vec2();
    private final Vec2Array avs = new Vec2Array();

    public WorldView(World world, DebugDraw debugDraw) {
        this.world = world;
        this.debugDraw = debugDraw;
    }

    /**
     * Call this to draw shapes and other debug draw data.
     */
    public void drawDebugData() {
        Color color = Color.color(0, 0, 0);

        int flags = debugDraw.getFlags();
        boolean wireframe = (flags & DebugDraw.e_wireframeDrawingBit) != 0;

        if ((flags & DebugDraw.e_shapeBit) != 0) {
            for (Body b = world.getBodyList(); b != null; b = b.getNext()) {
                xf.set(b.getTransform());
                for (Fixture f : b.getFixtures()) {
                    if (!b.isActive()) {
                        color = Color.color(0.5f, 0.5f, 0.3f);
                        drawShape(f, xf, color, wireframe);
                    } else if (b.getType() == BodyType.STATIC) {
                        color = Color.color(0.5f, 0.9f, 0.3f);
                        drawShape(f, xf, color, wireframe);
                    } else if (b.getType() == BodyType.KINEMATIC) {
                        color = Color.color(0.5f, 0.5f, 0.9f);
                        drawShape(f, xf, color, wireframe);
                    } else if (!b.isAwake()) {
                        color = Color.color(0.5f, 0.5f, 0.5f);
                        drawShape(f, xf, color, wireframe);
                    } else {
                        color = Color.color(0.9f, 0.7f, 0.7f);
                        drawShape(f, xf, color, wireframe);
                    }
                }
            }
            drawParticleSystem(world.getParticleSystem());
        }

        if ((flags & DebugDraw.e_jointBit) != 0) {
            for (Joint j = world.getJointList(); j != null; j = j.getNext()) {
                drawJoint(j);
            }
        }

        if ((flags & DebugDraw.e_pairBit) != 0) {
            color = Color.color(0.3f, 0.9f, 0.9f);
            for (Contact c = world.getContactManager().m_contactList; c != null; c = c.getNext()) {
                Fixture fixtureA = c.getFixtureA();
                Fixture fixtureB = c.getFixtureB();
                fixtureA.getAABB(c.getChildIndexA()).getCenterToOut(cA);
                fixtureB.getAABB(c.getChildIndexB()).getCenterToOut(cB);
                debugDraw.drawSegment(cA, cB, color);
            }
        }

        if ((flags & DebugDraw.e_aabbBit) != 0) {
            color = Color.color(0.9f, 0.3f, 0.9f);

            for (Body b = world.getBodyList(); b != null; b = b.getNext()) {
                if (!b.isActive()) {
                    continue;
                }

                for (Fixture f : b.getFixtures()) {
                    for (int i = 0; i < f.getProxyCount(); ++i) {
                        FixtureProxy proxy = f.m_proxies[i];
                        AABB aabb = world.getContactManager().m_broadPhase.getFatAABB(proxy.proxyId);
                        if (aabb != null) {
                            Vec2[] vs = avs.get(4);
                            vs[0].set(aabb.lowerBound.x, aabb.lowerBound.y);
                            vs[1].set(aabb.upperBound.x, aabb.lowerBound.y);
                            vs[2].set(aabb.upperBound.x, aabb.upperBound.y);
                            vs[3].set(aabb.lowerBound.x, aabb.upperBound.y);
                            debugDraw.drawPolygon(vs, 4, color);
                        }
                    }
                }
            }
        }

        if ((flags & DebugDraw.e_centerOfMassBit) != 0) {
            for (Body b = world.getBodyList(); b != null; b = b.getNext()) {
                xf.set(b.getTransform());
                xf.p.set(b.getWorldCenter());
                debugDraw.drawTransform(xf);
            }
        }

        if ((flags & DebugDraw.e_dynamicTreeBit) != 0) {
            world.getContactManager().m_broadPhase.drawTree(debugDraw);
        }

        debugDraw.flush();
    }

    private void drawJoint(Joint joint) {
        Body bodyA = joint.getBodyA();
        Body bodyB = joint.getBodyB();
        Transform xf1 = bodyA.getTransform();
        Transform xf2 = bodyB.getTransform();
        Vec2 x1 = xf1.p;
        Vec2 x2 = xf2.p;
        Vec2 p1 = world.getPool().popVec2();
        Vec2 p2 = world.getPool().popVec2();
        joint.getAnchorA(p1);
        joint.getAnchorB(p2);

        Color color = Color.color(0.5f, 0.8f, 0.8f);

        switch (joint.getType()) {
            // jbox2dTODO djm write after writing joints
            case DISTANCE:
                debugDraw.drawSegment(p1, p2, color);
                break;

            case PULLEY: {
                PulleyJoint pulley = (PulleyJoint) joint;
                Vec2 s1 = pulley.getGroundAnchorA();
                Vec2 s2 = pulley.getGroundAnchorB();
                debugDraw.drawSegment(s1, p1, color);
                debugDraw.drawSegment(s2, p2, color);
                debugDraw.drawSegment(s1, s2, color);
            }
            break;
            case CONSTANT_VOLUME:
            case MOUSE:
                // don't draw this
                break;
            default:
                debugDraw.drawSegment(x1, p1, color);
                debugDraw.drawSegment(p1, p2, color);
                debugDraw.drawSegment(x2, p2, color);
        }

        world.getPool().pushVec2(2);
    }

    // NOTE this corresponds to the liquid test, so the debugdraw can draw
    // the liquid particles correctly. They should be the same.
    private static Integer LIQUID_INT = new Integer(1234598372);
    private float liquidLength = .12f;
    private float averageLinearVel = -1;
    private final Vec2 liquidOffset = new Vec2();
    private final Vec2 circCenterMoved = new Vec2();
    private final Color liquidColor = Color.color(.4, .4, 1);

    private final Vec2 center = new Vec2();
    private final Vec2 axis = new Vec2();
    private final Vec2 v1 = new Vec2();
    private final Vec2 v2 = new Vec2();
    private final Vec2Array tlvertices = new Vec2Array();

    private void drawShape(Fixture fixture, Transform xf, Color color, boolean wireframe) {
        switch (fixture.getType()) {
            case CIRCLE: {
                CircleShape circle = (CircleShape) fixture.getShape();

                // Vec2 center = Mul(xf, circle.m_p);
                Transform.mulToOutUnsafe(xf, circle.m_p, center);
                float radius = circle.getRadius();
                xf.q.getXAxis(axis);

                if (fixture.getUserData() != null && fixture.getUserData().equals(LIQUID_INT)) {
                    Body b = fixture.getBody();
                    liquidOffset.set(b.m_linearVelocity);
                    float linVelLength = b.m_linearVelocity.length();
                    if (averageLinearVel == -1) {
                        averageLinearVel = linVelLength;
                    } else {
                        averageLinearVel = .98f * averageLinearVel + .02f * linVelLength;
                    }
                    liquidOffset.mulLocal(liquidLength / averageLinearVel / 2);
                    circCenterMoved.set(center).addLocal(liquidOffset);
                    center.subLocal(liquidOffset);
                    debugDraw.drawSegment(center, circCenterMoved, liquidColor);
                    return;
                }
                if (wireframe) {
                    debugDraw.drawCircle(center, radius, axis, color);
                } else {
                    debugDraw.drawSolidCircle(center, radius, axis, color);
                }
            }
            break;

            case POLYGON: {
                PolygonShape poly = (PolygonShape) fixture.getShape();
                int vertexCount = poly.getVertexCount();
                assert (vertexCount <= JBoxSettings.maxPolygonVertices);
                Vec2[] vertices = tlvertices.get(JBoxSettings.maxPolygonVertices);

                for (int i = 0; i < vertexCount; ++i) {
                    // vertices[i] = Mul(xf, poly.m_vertices[i]);
                    Transform.mulToOutUnsafe(xf, poly.m_vertices[i], vertices[i]);
                }
                if (wireframe) {
                    debugDraw.drawPolygon(vertices, vertexCount, color);
                } else {
                    debugDraw.drawSolidPolygon(vertices, vertexCount, color);
                }
            }
            break;
            case EDGE: {
                EdgeShape edge = (EdgeShape) fixture.getShape();
                Transform.mulToOutUnsafe(xf, edge.m_vertex1, v1);
                Transform.mulToOutUnsafe(xf, edge.m_vertex2, v2);
                debugDraw.drawSegment(v1, v2, color);
            }
            break;
            case CHAIN: {
                ChainShape chain = (ChainShape) fixture.getShape();
                int count = chain.m_count;
                Vec2[] vertices = chain.m_vertices;

                Transform.mulToOutUnsafe(xf, vertices[0], v1);
                for (int i = 1; i < count; ++i) {
                    Transform.mulToOutUnsafe(xf, vertices[i], v2);
                    debugDraw.drawSegment(v1, v2, color);
                    debugDraw.drawCircle(v1, 0.05f, color);
                    v1.set(v2);
                }
            }
            break;
            default:
                break;
        }
    }

    private void drawParticleSystem(ParticleSystem system) {
        boolean wireframe = (debugDraw.getFlags() & DebugDraw.e_wireframeDrawingBit) != 0;
        int particleCount = system.getParticleCount();
        if (particleCount != 0) {
            float particleRadius = system.getParticleRadius();
            Vec2[] positionBuffer = system.getParticlePositionBuffer();
            ParticleColor[] colorBuffer = null;
            if (system.m_colorBuffer.data != null) {
                colorBuffer = system.getParticleColorBuffer();
            }
            if (wireframe) {
                debugDraw.drawParticlesWireframe(positionBuffer, particleRadius, colorBuffer,
                        particleCount);
            } else {
                debugDraw.drawParticles(positionBuffer, particleRadius, colorBuffer, particleCount);
            }
        }
    }
}
