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
package org.jbox2d.dynamics.joints;

import com.almasb.gameutils.math.Vec2;
import org.jbox2d.common.JBoxSettings;
import org.jbox2d.common.JBoxUtils;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.SolverData;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Position;
import org.jbox2d.dynamics.contacts.Velocity;

public class ConstantVolumeJoint extends Joint {

    private final Body[] bodies;
    private float[] targetLengths;
    private float targetVolume;

    private Vec2[] normals;
    private float m_impulse = 0.0f;

    private World world;

    private DistanceJoint[] distanceJoints;

    public Body[] getBodies() {
        return bodies;
    }

    public DistanceJoint[] getJoints() {
        return distanceJoints;
    }

    public void inflate(float factor) {
        targetVolume *= factor;
    }

    public ConstantVolumeJoint(World argWorld, ConstantVolumeJointDef def) {
        super(argWorld.getPool(), def);
        world = argWorld;
        if (def.bodies.size() <= 2) {
            throw new IllegalArgumentException(
                    "You cannot create a constant volume joint with less than three bodies.");
        }
        bodies = def.bodies.toArray(new Body[0]);

        targetLengths = new float[bodies.length];
        for (int i = 0; i < targetLengths.length; ++i) {
            final int next = (i == targetLengths.length - 1) ? 0 : i + 1;
            float dist = bodies[i].getWorldCenter().sub(bodies[next].getWorldCenter()).length();
            targetLengths[i] = dist;
        }
        targetVolume = getBodyArea();

        if (def.joints != null && def.joints.size() != def.bodies.size()) {
            throw new IllegalArgumentException(
                    "Incorrect joint definition.  Joints have to correspond to the bodies");
        }
        if (def.joints == null) {
            final DistanceJointDef djd = new DistanceJointDef();
            distanceJoints = new DistanceJoint[bodies.length];
            for (int i = 0; i < targetLengths.length; ++i) {
                final int next = (i == targetLengths.length - 1) ? 0 : i + 1;
                djd.frequencyHz = def.frequencyHz;// 20.0f;
                djd.dampingRatio = def.dampingRatio;// 50.0f;
                djd.collideConnected = def.collideConnected;
                djd.initialize(bodies[i], bodies[next], bodies[i].getWorldCenter(),
                        bodies[next].getWorldCenter());
                distanceJoints[i] = (DistanceJoint) world.createJoint(djd);
            }
        } else {
            distanceJoints = def.joints.toArray(new DistanceJoint[0]);
        }

        normals = new Vec2[bodies.length];
        for (int i = 0; i < normals.length; ++i) {
            normals[i] = new Vec2();
        }
    }

    @Override
    public void destructor() {
        for (int i = 0; i < distanceJoints.length; ++i) {
            world.destroyJoint(distanceJoints[i]);
        }
    }

    private float getBodyArea() {
        float area = 0.0f;
        for (int i = 0; i < bodies.length; ++i) {
            final int next = (i == bodies.length - 1) ? 0 : i + 1;
            area +=
                    bodies[i].getWorldCenter().x * bodies[next].getWorldCenter().y
                            - bodies[next].getWorldCenter().x * bodies[i].getWorldCenter().y;
        }
        area *= .5f;
        return area;
    }

    private float getSolverArea(Position[] positions) {
        float area = 0.0f;
        for (int i = 0; i < bodies.length; ++i) {
            final int next = (i == bodies.length - 1) ? 0 : i + 1;
            area +=
                    positions[bodies[i].m_islandIndex].c.x * positions[bodies[next].m_islandIndex].c.y
                            - positions[bodies[next].m_islandIndex].c.x * positions[bodies[i].m_islandIndex].c.y;
        }
        area *= .5f;
        return area;
    }

    private boolean constrainEdges(Position[] positions) {
        float perimeter = 0.0f;
        for (int i = 0; i < bodies.length; ++i) {
            final int next = (i == bodies.length - 1) ? 0 : i + 1;
            float dx = positions[bodies[next].m_islandIndex].c.x - positions[bodies[i].m_islandIndex].c.x;
            float dy = positions[bodies[next].m_islandIndex].c.y - positions[bodies[i].m_islandIndex].c.y;
            float dist = JBoxUtils.sqrt(dx * dx + dy * dy);
            if (dist < JBoxSettings.EPSILON) {
                dist = 1.0f;
            }
            normals[i].x = dy / dist;
            normals[i].y = -dx / dist;
            perimeter += dist;
        }

        final Vec2 delta = pool.popVec2();

        float deltaArea = targetVolume - getSolverArea(positions);
        float toExtrude = 0.5f * deltaArea / perimeter; // *relaxationFactor
        // float sumdeltax = 0.0f;
        boolean done = true;
        for (int i = 0; i < bodies.length; ++i) {
            final int next = (i == bodies.length - 1) ? 0 : i + 1;
            delta.set(toExtrude * (normals[i].x + normals[next].x), toExtrude
                    * (normals[i].y + normals[next].y));
            // sumdeltax += dx;
            float normSqrd = delta.lengthSquared();
            if (normSqrd > JBoxSettings.maxLinearCorrection * JBoxSettings.maxLinearCorrection) {
                delta.mulLocal(JBoxSettings.maxLinearCorrection / JBoxUtils.sqrt(normSqrd));
            }
            if (normSqrd > JBoxSettings.linearSlop * JBoxSettings.linearSlop) {
                done = false;
            }
            positions[bodies[next].m_islandIndex].c.x += delta.x;
            positions[bodies[next].m_islandIndex].c.y += delta.y;
            // bodies[next].m_linearVelocity.x += delta.x * step.inv_dt;
            // bodies[next].m_linearVelocity.y += delta.y * step.inv_dt;
        }

        pool.pushVec2(1);

        return done;
    }

    @Override
    public void initVelocityConstraints(final SolverData step) {
        Velocity[] velocities = step.velocities;
        Position[] positions = step.positions;
        final Vec2[] d = pool.getVec2Array(bodies.length);

        for (int i = 0; i < bodies.length; ++i) {
            final int prev = (i == 0) ? bodies.length - 1 : i - 1;
            final int next = (i == bodies.length - 1) ? 0 : i + 1;
            d[i].set(positions[bodies[next].m_islandIndex].c);
            d[i].subLocal(positions[bodies[prev].m_islandIndex].c);
        }

        if (step.step.warmStarting) {
            m_impulse *= step.step.dtRatio;
            // float lambda = -2.0f * crossMassSum / dotMassSum;

            // lambda = JBoxUtils.clamp(lambda, -JBoxSettings.maxLinearCorrection,
            // JBoxSettings.maxLinearCorrection);
            // m_impulse = lambda;
            for (int i = 0; i < bodies.length; ++i) {
                velocities[bodies[i].m_islandIndex].v.x += bodies[i].m_invMass * d[i].y * .5f * m_impulse;
                velocities[bodies[i].m_islandIndex].v.y += bodies[i].m_invMass * -d[i].x * .5f * m_impulse;
            }
        } else {
            m_impulse = 0.0f;
        }
    }

    @Override
    public boolean solvePositionConstraints(SolverData step) {
        return constrainEdges(step.positions);
    }

    @Override
    public void solveVelocityConstraints(final SolverData step) {
        float crossMassSum = 0.0f;
        float dotMassSum = 0.0f;

        Velocity[] velocities = step.velocities;
        Position[] positions = step.positions;
        final Vec2 d[] = pool.getVec2Array(bodies.length);

        for (int i = 0; i < bodies.length; ++i) {
            final int prev = (i == 0) ? bodies.length - 1 : i - 1;
            final int next = (i == bodies.length - 1) ? 0 : i + 1;
            d[i].set(positions[bodies[next].m_islandIndex].c);
            d[i].subLocal(positions[bodies[prev].m_islandIndex].c);
            dotMassSum += (d[i].lengthSquared()) / bodies[i].getMass();
            crossMassSum += Vec2.cross(velocities[bodies[i].m_islandIndex].v, d[i]);
        }
        float lambda = -2.0f * crossMassSum / dotMassSum;

        // lambda = JBoxUtils.clamp(lambda, -JBoxSettings.maxLinearCorrection,
        // JBoxSettings.maxLinearCorrection);
        m_impulse += lambda;

        for (int i = 0; i < bodies.length; ++i) {
            velocities[bodies[i].m_islandIndex].v.x += bodies[i].m_invMass * d[i].y * .5f * lambda;
            velocities[bodies[i].m_islandIndex].v.y += bodies[i].m_invMass * -d[i].x * .5f * lambda;
        }
    }

    @Override
    public void getAnchorA(Vec2 argOut) {
        // no default implementation
    }

    @Override
    public void getAnchorB(Vec2 argOut) {
        // no default implementation
    }

    @Override
    public void getReactionForce(float inv_dt, Vec2 argOut) {
        // no default implementation
    }

    @Override
    public float getReactionTorque(float inv_dt) {
        return 0;
    }
}
