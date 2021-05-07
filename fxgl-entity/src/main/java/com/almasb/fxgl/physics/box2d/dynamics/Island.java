/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics.box2d.dynamics;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.physics.box2d.callbacks.ContactImpulse;
import com.almasb.fxgl.physics.box2d.callbacks.ContactListener;
import com.almasb.fxgl.physics.box2d.common.JBoxSettings;
import com.almasb.fxgl.physics.box2d.common.Sweep;
import com.almasb.fxgl.physics.box2d.dynamics.contacts.*;
import com.almasb.fxgl.physics.box2d.dynamics.contacts.ContactSolver.ContactSolverDef;
import com.almasb.fxgl.physics.box2d.dynamics.joints.Joint;

/*
 Position Correction Notes
 =========================
 I tried the several algorithms for position correction of the 2D revolute joint.
 I looked at these systems:
 - simple pendulum (1m diameter sphere on massless 5m stick) with initial angular velocity of 100 rad/s.
 - suspension bridge with 30 1m long planks of length 1m.
 - multi-link chain with 30 1m long links.

 Here are the algorithms:

 Baumgarte - A fraction of the position error is added to the velocity error. There is no
 separate position solver.

 Pseudo Velocities - After the velocity solver and position integration,
 the position error, Jacobian, and effective mass are recomputed. Then
 the velocity constraints are solved with pseudo velocities and a fraction
 of the position error is added to the pseudo velocity error. The pseudo
 velocities are initialized to zero and there is no warm-starting. After
 the position solver, the pseudo velocities are added to the positions.
 This is also called the First Order World method or the Position LCP method.

 Modified Nonlinear Gauss-Seidel (NGS) - Like Pseudo Velocities except the
 position error is re-computed for each raint and the positions are updated
 after the raint is solved. The radius vectors (aka Jacobians) are
 re-computed too (otherwise the algorithm has horrible instability). The pseudo
 velocity states are not needed because they are effectively zero at the beginning
 of each iteration. Since we have the current position error, we allow the
 iterations to terminate early if the error becomes smaller than JBoxSettings.linearSlop.

 Full NGS or just NGS - Like Modified NGS except the effective mass are re-computed
 each time a raint is solved.

 Here are the results:
 Baumgarte - this is the cheapest algorithm but it has some stability problems,
 especially with the bridge. The chain links separate easily close to the root
 and they jitter as they struggle to pull together. This is one of the most common
 methods in the field. The big drawback is that the position correction artificially
 affects the momentum, thus leading to instabilities and false bounce. I used a
 bias factor of 0.2. A larger bias factor makes the bridge less stable, a smaller
 factor makes joints and contacts more spongy.

 Pseudo Velocities - the is more stable than the Baumgarte method. The bridge is
 stable. However, joints still separate with large angular velocities. Drag the
 simple pendulum in a circle quickly and the joint will separate. The chain separates
 easily and does not recover. I used a bias factor of 0.2. A larger value lead to
 the bridge collapsing when a heavy cube drops on it.

 Modified NGS - this algorithm is better in some ways than Baumgarte and Pseudo
 Velocities, but in other ways it is worse. The bridge and chain are much more
 stable, but the simple pendulum goes unstable at high angular velocities.

 Full NGS - stable in all tests. The joints display good stiffness. The bridge
 still sags, but this is better than infinite forces.

 Recommendations
 Pseudo Velocities are not really worthwhile because the bridge and chain cannot
 recover from joint separation. In other cases the benefit over Baumgarte is small.

 Modified NGS is not a robust method for the revolute joint due to the violent
 instability seen in the simple pendulum. Perhaps it is viable with other raint
 types, especially scalar constraints where the effective mass is a scalar.

 This leaves Baumgarte and Full NGS. Baumgarte has small, but manageable instabilities
 and is very fast. I don't think we can escape Baumgarte, especially in highly
 demanding cases where high raint fidelity is not needed.

 Full NGS is robust and easy on the eyes. I recommend this as an option for
 higher fidelity simulation and certainly for suspension bridges and long chains.
 Full NGS might be a good choice for ragdolls, especially motorized ragdolls where
 joint separation can be problematic. The number of NGS iterations can be reduced
 for better performance without harming robustness much.

 Each joint in a can be handled differently in the position solver. So I recommend
 a system where the user can select the algorithm on a per joint basis. I would
 probably default to the slower Full NGS and let the user select the faster
 Baumgarte method in performance critical scenarios.
 */

/*
 Cache Performance

 The Box2D solvers are dominated by cache misses. Data structures are designed
 to increase the number of cache hits. Much of misses are due to random access
 to body data. The raint structures are iterated over linearly, which leads
 to few cache misses.

 The bodies are not accessed during iteration. Instead read only data, such as
 the mass values are stored with the constraints. The mutable data are the raint
 impulses and the bodies velocities/positions. The impulses are held inside the
 raint structures. The body velocities/positions are held in compact, temporary
 arrays to increase the number of cache hits. Linear and angular velocity are
 stored in a single array since multiple arrays lead to multiple misses.
 */

/*
 2D Rotation

 R = [cos(theta) -sin(theta)]
 [sin(theta) cos(theta) ]

 thetaDot = omega

 Let q1 = cos(theta), q2 = sin(theta).
 R = [q1 -q2]
 [q2  q1]

 q1Dot = -thetaDot * q2
 q2Dot = thetaDot * q1

 q1_new = q1_old - dt * w * q2
 q2_new = q2_old + dt * w * q1
 then normalize.

 This might be faster than computing sin+cos.
 However, we can compute sin+cos of the same angle fast.
 */

/**
 * This is an internal class.
 *
 * @author Daniel Murphy
 */
class Island {

    private ContactListener listener;

    private Body[] bodies;
    private Contact[] m_contacts;
    private Joint[] m_joints;

    private Position[] m_positions;
    private Velocity[] m_velocities;

    private int bodyCount;
    private int jointCount;
    private int contactCount;

    private int bodyCapacity;
    private int m_contactCapacity;
    private int m_jointCapacity;

    void init(int bodyCapacity, int contactCapacity, int jointCapacity, ContactListener listener) {
        this.bodyCapacity = bodyCapacity;
        m_contactCapacity = contactCapacity;
        m_jointCapacity = jointCapacity;
        bodyCount = 0;
        contactCount = 0;
        jointCount = 0;

        this.listener = listener;

        if (bodies == null || bodyCapacity > bodies.length) {
            bodies = new Body[bodyCapacity];
        }
        if (m_joints == null || m_jointCapacity > m_joints.length) {
            m_joints = new Joint[m_jointCapacity];
        }
        if (m_contacts == null || m_contactCapacity > m_contacts.length) {
            m_contacts = new Contact[m_contactCapacity];
        }

        // dynamic array
        if (m_velocities == null || bodyCapacity > m_velocities.length) {
            final Velocity[] old = m_velocities == null ? new Velocity[0] : m_velocities;
            m_velocities = new Velocity[bodyCapacity];
            System.arraycopy(old, 0, m_velocities, 0, old.length);
            for (int i = old.length; i < m_velocities.length; i++) {
                m_velocities[i] = new Velocity();
            }
        }

        // dynamic array
        if (m_positions == null || bodyCapacity > m_positions.length) {
            final Position[] old = m_positions == null ? new Position[0] : m_positions;
            m_positions = new Position[bodyCapacity];
            System.arraycopy(old, 0, m_positions, 0, old.length);
            for (int i = old.length; i < m_positions.length; i++) {
                m_positions[i] = new Position();
            }
        }
    }

    void clear() {
        bodyCount = 0;
        contactCount = 0;
        jointCount = 0;
    }

    private final ContactSolver contactSolver = new ContactSolver();
    private final SolverData solverData = new SolverData();
    private final ContactSolverDef solverDef = new ContactSolverDef();

    void solve(TimeStep step, Vec2 gravity, boolean allowSleep) {
        float h = step.dt;

        // Integrate velocities and apply damping. Initialize the body state.
        for (int i = 0; i < bodyCount; ++i) {
            final Body b = bodies[i];
            final Sweep bm_sweep = b.m_sweep;
            final Vec2 c = bm_sweep.c;
            float a = bm_sweep.a;
            final Vec2 v = b.m_linearVelocity;
            float w = b.getAngularVelocity();

            // Store positions for continuous collision.
            bm_sweep.c0.set(bm_sweep.c);
            bm_sweep.a0 = bm_sweep.a;

            if (b.getType() == BodyType.DYNAMIC) {
                // Integrate velocities.
                // v += h * (b.m_gravityScale * gravity + b.m_invMass * b.m_force);
                v.x += h * (b.getGravityScale() * gravity.x + b.m_invMass * b.m_force.x);
                v.y += h * (b.getGravityScale() * gravity.y + b.m_invMass * b.m_force.y);
                w += h * b.m_invI * b.getTorque();

                // Apply damping.
                // ODE: dv/dt + c * v = 0
                // Solution: v(t) = v0 * exp(-c * t)
                // Time step: v(t + dt) = v0 * exp(-c * (t + dt)) = v0 * exp(-c * t) * exp(-c * dt) = v *
                // exp(-c * dt)
                // v2 = exp(-c * dt) * v1
                // Pade approximation:
                // v2 = v1 * 1 / (1 + c * dt)
                v.x *= 1.0f / (1.0f + h * b.getLinearDamping());
                v.y *= 1.0f / (1.0f + h * b.getLinearDamping());
                w *= 1.0f / (1.0f + h * b.getAngularDamping());
            }

            m_positions[i].c.x = c.x;
            m_positions[i].c.y = c.y;
            m_positions[i].a = a;
            m_velocities[i].v.x = v.x;
            m_velocities[i].v.y = v.y;
            m_velocities[i].w = w;
        }

        // Solver data
        solverData.step = step;
        solverData.positions = m_positions;
        solverData.velocities = m_velocities;

        // Initialize velocity constraints.
        solverDef.step = step;
        solverDef.contacts = m_contacts;
        solverDef.count = contactCount;
        solverDef.positions = m_positions;
        solverDef.velocities = m_velocities;

        contactSolver.init(solverDef);
        contactSolver.initializeVelocityConstraints();

        if (step.warmStarting) {
            contactSolver.warmStart();
        }

        for (int i = 0; i < jointCount; ++i) {
            m_joints[i].initVelocityConstraints(solverData);
        }

        // Solve velocity constraints
        for (int i = 0; i < step.velocityIterations; ++i) {
            for (int j = 0; j < jointCount; ++j) {
                m_joints[j].solveVelocityConstraints(solverData);
            }

            contactSolver.solveVelocityConstraints();
        }

        // Store impulses for warm starting
        contactSolver.storeImpulses();

        // Integrate positions
        for (int i = 0; i < bodyCount; ++i) {
            final Vec2 c = m_positions[i].c;
            float a = m_positions[i].a;
            final Vec2 v = m_velocities[i].v;
            float w = m_velocities[i].w;

            // Check for large velocities
            float translationx = v.x * h;
            float translationy = v.y * h;

            if (translationx * translationx + translationy * translationy > JBoxSettings.maxTranslationSquared) {
                float ratio = JBoxSettings.maxTranslation
                        / FXGLMath.sqrtF(translationx * translationx + translationy * translationy);
                v.x *= ratio;
                v.y *= ratio;
            }

            float rotation = h * w;
            if (rotation * rotation > JBoxSettings.maxRotationSquared) {
                float ratio = JBoxSettings.maxRotation / FXGLMath.abs(rotation);
                w *= ratio;
            }

            // Integrate
            c.x += h * v.x;
            c.y += h * v.y;
            a += h * w;

            m_positions[i].a = a;
            m_velocities[i].w = w;
        }

        // Solve position constraints
        boolean positionSolved = false;
        for (int i = 0; i < step.positionIterations; ++i) {
            boolean contactsOkay = contactSolver.solvePositionConstraints();

            boolean jointsOkay = true;
            for (int j = 0; j < jointCount; ++j) {
                boolean jointOkay = m_joints[j].solvePositionConstraints(solverData);
                jointsOkay = jointsOkay && jointOkay;
            }

            if (contactsOkay && jointsOkay) {
                // Exit early if the position errors are small.
                positionSolved = true;
                break;
            }
        }

        // Copy state buffers back to the bodies
        for (int i = 0; i < bodyCount; ++i) {
            Body body = bodies[i];
            body.m_sweep.c.x = m_positions[i].c.x;
            body.m_sweep.c.y = m_positions[i].c.y;
            body.m_sweep.a = m_positions[i].a;
            body.m_linearVelocity.x = m_velocities[i].v.x;
            body.m_linearVelocity.y = m_velocities[i].v.y;
            body.setAngularVelocityDirectly(m_velocities[i].w);
            body.synchronizeTransform();
        }

        report(contactSolver.m_velocityConstraints);

        if (allowSleep) {
            float minSleepTime = Float.MAX_VALUE;

            final float linTolSqr = JBoxSettings.linearSleepTolerance * JBoxSettings.linearSleepTolerance;
            final float angTolSqr = JBoxSettings.angularSleepTolerance * JBoxSettings.angularSleepTolerance;

            for (int i = 0; i < bodyCount; ++i) {
                Body b = bodies[i];
                if (b.getType() == BodyType.STATIC) {
                    continue;
                }

                if (!b.isSleepingAllowed()
                        || b.getAngularVelocity() * b.getAngularVelocity() > angTolSqr
                        || Vec2.dot(b.m_linearVelocity, b.m_linearVelocity) > linTolSqr) {
                    b.setSleepTime(0);
                    minSleepTime = 0.0f;
                } else {
                    b.setSleepTime(b.getSleepTime() + h);
                    minSleepTime = Math.min(minSleepTime, b.getSleepTime());
                }
            }

            if (minSleepTime >= JBoxSettings.timeToSleep && positionSolved) {
                for (int i = 0; i < bodyCount; ++i) {
                    Body b = bodies[i];
                    b.setAwake(false);
                }
            }
        }
    }

    private final ContactSolver toiContactSolver = new ContactSolver();
    private final ContactSolverDef toiSolverDef = new ContactSolverDef();

    void solveTOI(TimeStep subStep, int toiIndexA, int toiIndexB) {
        assert toiIndexA < bodyCount;
        assert toiIndexB < bodyCount;

        // Initialize the body state.
        for (int i = 0; i < bodyCount; ++i) {
            m_positions[i].c.x = bodies[i].m_sweep.c.x;
            m_positions[i].c.y = bodies[i].m_sweep.c.y;
            m_positions[i].a = bodies[i].m_sweep.a;
            m_velocities[i].v.x = bodies[i].m_linearVelocity.x;
            m_velocities[i].v.y = bodies[i].m_linearVelocity.y;
            m_velocities[i].w = bodies[i].getAngularVelocity();
        }

        toiSolverDef.contacts = m_contacts;
        toiSolverDef.count = contactCount;
        toiSolverDef.step = subStep;
        toiSolverDef.positions = m_positions;
        toiSolverDef.velocities = m_velocities;
        toiContactSolver.init(toiSolverDef);

        // Solve position constraints.
        for (int i = 0; i < subStep.positionIterations; ++i) {
            boolean contactsOkay = toiContactSolver.solveTOIPositionConstraints(toiIndexA, toiIndexB);
            if (contactsOkay) {
                break;
            }
        }

        // Leap of faith to new safe state.
        bodies[toiIndexA].m_sweep.c0.x = m_positions[toiIndexA].c.x;
        bodies[toiIndexA].m_sweep.c0.y = m_positions[toiIndexA].c.y;
        bodies[toiIndexA].m_sweep.a0 = m_positions[toiIndexA].a;
        bodies[toiIndexB].m_sweep.c0.set(m_positions[toiIndexB].c);
        bodies[toiIndexB].m_sweep.a0 = m_positions[toiIndexB].a;

        // No warm starting is needed for TOI events because warm
        // starting impulses were applied in the discrete solver.
        toiContactSolver.initializeVelocityConstraints();

        // Solve velocity constraints.
        for (int i = 0; i < subStep.velocityIterations; ++i) {
            toiContactSolver.solveVelocityConstraints();
        }

        // Don't store the TOI contact forces for warm starting
        // because they can be quite large.

        float h = subStep.dt;

        // Integrate positions
        for (int i = 0; i < bodyCount; ++i) {
            Vec2 c = m_positions[i].c;
            float a = m_positions[i].a;
            Vec2 v = m_velocities[i].v;
            float w = m_velocities[i].w;

            // Check for large velocities
            float translationx = v.x * h;
            float translationy = v.y * h;
            if (translationx * translationx + translationy * translationy > JBoxSettings.maxTranslationSquared) {
                float ratio =
                        JBoxSettings.maxTranslation
                                / FXGLMath.sqrtF(translationx * translationx + translationy * translationy);
                v.mulLocal(ratio);
            }

            float rotation = h * w;
            if (rotation * rotation > JBoxSettings.maxRotationSquared) {
                float ratio = JBoxSettings.maxRotation / FXGLMath.abs(rotation);
                w *= ratio;
            }

            // Integrate
            c.x += v.x * h;
            c.y += v.y * h;
            a += h * w;

            m_positions[i].c.x = c.x;
            m_positions[i].c.y = c.y;
            m_positions[i].a = a;
            m_velocities[i].v.x = v.x;
            m_velocities[i].v.y = v.y;
            m_velocities[i].w = w;

            // Sync bodies
            Body body = bodies[i];
            body.m_sweep.c.x = c.x;
            body.m_sweep.c.y = c.y;
            body.m_sweep.a = a;
            body.m_linearVelocity.x = v.x;
            body.m_linearVelocity.y = v.y;
            body.setAngularVelocityDirectly(w);
            body.synchronizeTransform();
        }

        report(toiContactSolver.m_velocityConstraints);
    }

    void add(Body body) {
        body.m_islandIndex = bodyCount;
        bodies[bodyCount] = body;
        ++bodyCount;
    }

    void add(Contact contact) {
        m_contacts[contactCount++] = contact;
    }

    void add(Joint joint) {
        m_joints[jointCount++] = joint;
    }

    boolean isBodyCountEqualToCapacity() {
        return bodyCount == bodyCapacity;
    }

    boolean isContactCountEqualToCapacity() {
        return contactCount == m_contactCapacity;
    }

    private final ContactImpulse impulse = new ContactImpulse();

    private void report(ContactVelocityConstraint[] constraints) {
        if (listener == null) {
            return;
        }

        for (int i = 0; i < contactCount; ++i) {
            Contact c = m_contacts[i];

            ContactVelocityConstraint vc = constraints[i];
            impulse.count = vc.pointCount;
            for (int j = 0; j < vc.pointCount; ++j) {
                impulse.normalImpulses[j] = vc.points[j].normalImpulse;
                impulse.tangentImpulses[j] = vc.points[j].tangentImpulse;
            }

            listener.postSolve(c, impulse);
        }
    }

    void resetFlagsAndSynchronizeBroadphaseProxies() {
        for (int i = 0; i < bodyCount; ++i) {
            Body body = bodies[i];
            body.m_flags &= ~Body.e_islandFlag;

            if (body.getType() != BodyType.DYNAMIC) {
                continue;
            }

            body.synchronizeFixtures();

            // Invalidate all contact TOIs on this displaced body.
            for (ContactEdge ce = body.m_contactList; ce != null; ce = ce.next) {
                ce.contact.m_flags &= ~(Contact.TOI_FLAG | Contact.ISLAND_FLAG);
            }
        }
    }

    void postSolveCleanup() {
        for (int i = 0; i < bodyCount; ++i) {
            // Allow static bodies to participate in other islands.
            Body b = bodies[i];
            if (b.getType() == BodyType.STATIC) {
                b.m_flags &= ~Body.e_islandFlag;
            }
        }
    }
}
