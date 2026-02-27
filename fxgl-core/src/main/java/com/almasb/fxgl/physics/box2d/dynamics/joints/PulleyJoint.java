/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
/**
 * Created at 12:12:02 PM Jan 23, 2011
 */
package com.almasb.fxgl.physics.box2d.dynamics.joints;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.physics.box2d.common.JBoxSettings;
import com.almasb.fxgl.physics.box2d.common.Rotation;
import com.almasb.fxgl.physics.box2d.dynamics.SolverData;
import com.almasb.fxgl.physics.box2d.pooling.IWorldPool;

/**
 * The pulley joint is connected to two bodies and two fixed ground points. The pulley supports a
 * ratio such that: length1 + ratio * length2 <= constant Yes, the force transmitted is scaled by
 * the ratio. Warning: the pulley joint can get a bit squirrelly by itself. They often work better
 * when combined with prismatic joints. You should also cover the the anchor points with static
 * shapes to prevent one side from going to zero length.
 *
 * @author Daniel Murphy
 */
public class PulleyJoint extends Joint {

    public static final float MIN_PULLEY_LENGTH = 2.0f;

    private final Vec2 m_groundAnchorA = new Vec2();
    private final Vec2 m_groundAnchorB = new Vec2();
    private float m_lengthA;
    private float m_lengthB;

    // Solver shared
    private final Vec2 m_localAnchorA = new Vec2();
    private final Vec2 m_localAnchorB = new Vec2();
    private float m_constant;
    private float m_ratio;
    private float m_impulse;

    // Solver temp
    private int m_indexA;
    private int m_indexB;
    private final Vec2 m_uA = new Vec2();
    private final Vec2 m_uB = new Vec2();
    private final Vec2 m_rA = new Vec2();
    private final Vec2 m_rB = new Vec2();
    private final Vec2 m_localCenterA = new Vec2();
    private final Vec2 m_localCenterB = new Vec2();
    private float m_invMassA;
    private float m_invMassB;
    private float m_invIA;
    private float m_invIB;
    private float m_mass;

    protected PulleyJoint(IWorldPool argWorldPool, PulleyJointDef def) {
        super(argWorldPool, def);
        m_groundAnchorA.set(def.groundAnchorA);
        m_groundAnchorB.set(def.groundAnchorB);
        m_localAnchorA.set(def.localAnchorA);
        m_localAnchorB.set(def.localAnchorB);

        assert def.ratio != 0.0f;
        m_ratio = def.ratio;

        m_lengthA = def.lengthA;
        m_lengthB = def.lengthB;

        m_constant = def.lengthA + m_ratio * def.lengthB;
        m_impulse = 0.0f;
    }

    public float getLengthA() {
        return m_lengthA;
    }

    public float getLengthB() {
        return m_lengthB;
    }

    public float getCurrentLengthA() {
        final Vec2 p = pool.popVec2();
        m_bodyA.getWorldPointToOut(m_localAnchorA, p);
        p.subLocal(m_groundAnchorA);
        float length = p.length();
        pool.pushVec2(1);
        return length;
    }

    public float getCurrentLengthB() {
        final Vec2 p = pool.popVec2();
        m_bodyB.getWorldPointToOut(m_localAnchorB, p);
        p.subLocal(m_groundAnchorB);
        float length = p.length();
        pool.pushVec2(1);
        return length;
    }


    public Vec2 getLocalAnchorA() {
        return m_localAnchorA;
    }

    public Vec2 getLocalAnchorB() {
        return m_localAnchorB;
    }


    @Override
    public void getAnchorA(Vec2 argOut) {
        m_bodyA.getWorldPointToOut(m_localAnchorA, argOut);
    }

    @Override
    public void getAnchorB(Vec2 argOut) {
        m_bodyB.getWorldPointToOut(m_localAnchorB, argOut);
    }

    @Override
    public void getReactionForce(float inv_dt, Vec2 argOut) {
        argOut.set(m_uB).mulLocal(m_impulse).mulLocal(inv_dt);
    }

    @Override
    public float getReactionTorque(float inv_dt) {
        return 0f;
    }

    public Vec2 getGroundAnchorA() {
        return m_groundAnchorA;
    }

    public Vec2 getGroundAnchorB() {
        return m_groundAnchorB;
    }

    public float getLength1() {
        final Vec2 p = pool.popVec2();
        m_bodyA.getWorldPointToOut(m_localAnchorA, p);
        p.subLocal(m_groundAnchorA);

        float len = p.length();
        pool.pushVec2(1);
        return len;
    }

    public float getLength2() {
        final Vec2 p = pool.popVec2();
        m_bodyB.getWorldPointToOut(m_localAnchorB, p);
        p.subLocal(m_groundAnchorB);

        float len = p.length();
        pool.pushVec2(1);
        return len;
    }

    public float getRatio() {
        return m_ratio;
    }

    @Override
    public void initVelocityConstraints(final SolverData data) {
        m_indexA = m_bodyA.m_islandIndex;
        m_indexB = m_bodyB.m_islandIndex;
        m_localCenterA.set(m_bodyA.m_sweep.localCenter);
        m_localCenterB.set(m_bodyB.m_sweep.localCenter);
        m_invMassA = m_bodyA.m_invMass;
        m_invMassB = m_bodyB.m_invMass;
        m_invIA = m_bodyA.m_invI;
        m_invIB = m_bodyB.m_invI;

        Vec2 cA = data.positions[m_indexA].c;
        float aA = data.positions[m_indexA].a;
        Vec2 vA = data.velocities[m_indexA].v;
        float wA = data.velocities[m_indexA].w;

        Vec2 cB = data.positions[m_indexB].c;
        float aB = data.positions[m_indexB].a;
        Vec2 vB = data.velocities[m_indexB].v;
        float wB = data.velocities[m_indexB].w;

        final Rotation qA = pool.popRot();
        final Rotation qB = pool.popRot();
        final Vec2 temp = pool.popVec2();

        qA.set(aA);
        qB.set(aB);

        // Compute the effective masses.
        Rotation.mulToOutUnsafe(qA, temp.set(m_localAnchorA).subLocal(m_localCenterA), m_rA);
        Rotation.mulToOutUnsafe(qB, temp.set(m_localAnchorB).subLocal(m_localCenterB), m_rB);

        m_uA.set(cA).addLocal(m_rA).subLocal(m_groundAnchorA);
        m_uB.set(cB).addLocal(m_rB).subLocal(m_groundAnchorB);

        float lengthA = m_uA.length();
        float lengthB = m_uB.length();

        if (lengthA > 10f * JBoxSettings.linearSlop) {
            m_uA.mulLocal(1.0f / lengthA);
        } else {
            m_uA.setZero();
        }

        if (lengthB > 10f * JBoxSettings.linearSlop) {
            m_uB.mulLocal(1.0f / lengthB);
        } else {
            m_uB.setZero();
        }

        // Compute effective mass.
        float ruA = Vec2.cross(m_rA, m_uA);
        float ruB = Vec2.cross(m_rB, m_uB);

        float mA = m_invMassA + m_invIA * ruA * ruA;
        float mB = m_invMassB + m_invIB * ruB * ruB;

        m_mass = mA + m_ratio * m_ratio * mB;

        if (m_mass > 0.0f) {
            m_mass = 1.0f / m_mass;
        }

        if (data.step.warmStarting) {

            // Scale impulses to support variable time steps.
            m_impulse *= data.step.dtRatio;

            // Warm starting.
            final Vec2 PA = pool.popVec2();
            final Vec2 PB = pool.popVec2();

            PA.set(m_uA).mulLocal(-m_impulse);
            PB.set(m_uB).mulLocal(-m_ratio * m_impulse);

            vA.x += m_invMassA * PA.x;
            vA.y += m_invMassA * PA.y;
            wA += m_invIA * Vec2.cross(m_rA, PA);
            vB.x += m_invMassB * PB.x;
            vB.y += m_invMassB * PB.y;
            wB += m_invIB * Vec2.cross(m_rB, PB);

            pool.pushVec2(2);
        } else {
            m_impulse = 0.0f;
        }
//    data.velocities[m_indexA].v.set(vA);
        data.velocities[m_indexA].w = wA;
//    data.velocities[m_indexB].v.set(vB);
        data.velocities[m_indexB].w = wB;

        pool.pushVec2(1);
        pool.pushRot(2);
    }

    @Override
    public void solveVelocityConstraints(final SolverData data) {
        Vec2 vA = data.velocities[m_indexA].v;
        float wA = data.velocities[m_indexA].w;
        Vec2 vB = data.velocities[m_indexB].v;
        float wB = data.velocities[m_indexB].w;

        final Vec2 vpA = pool.popVec2();
        final Vec2 vpB = pool.popVec2();
        final Vec2 PA = pool.popVec2();
        final Vec2 PB = pool.popVec2();

        Vec2.crossToOutUnsafe(wA, m_rA, vpA);
        vpA.addLocal(vA);
        Vec2.crossToOutUnsafe(wB, m_rB, vpB);
        vpB.addLocal(vB);

        float Cdot = -Vec2.dot(m_uA, vpA) - m_ratio * Vec2.dot(m_uB, vpB);
        float impulse = -m_mass * Cdot;
        m_impulse += impulse;

        PA.set(m_uA).mulLocal(-impulse);
        PB.set(m_uB).mulLocal(-m_ratio * impulse);
        vA.x += m_invMassA * PA.x;
        vA.y += m_invMassA * PA.y;
        wA += m_invIA * Vec2.cross(m_rA, PA);
        vB.x += m_invMassB * PB.x;
        vB.y += m_invMassB * PB.y;
        wB += m_invIB * Vec2.cross(m_rB, PB);

//    data.velocities[m_indexA].v.set(vA);
        data.velocities[m_indexA].w = wA;
//    data.velocities[m_indexB].v.set(vB);
        data.velocities[m_indexB].w = wB;

        pool.pushVec2(4);
    }

    @Override
    public boolean solvePositionConstraints(final SolverData data) {
        final Rotation qA = pool.popRot();
        final Rotation qB = pool.popRot();
        final Vec2 rA = pool.popVec2();
        final Vec2 rB = pool.popVec2();
        final Vec2 uA = pool.popVec2();
        final Vec2 uB = pool.popVec2();
        final Vec2 temp = pool.popVec2();
        final Vec2 PA = pool.popVec2();
        final Vec2 PB = pool.popVec2();

        Vec2 cA = data.positions[m_indexA].c;
        float aA = data.positions[m_indexA].a;
        Vec2 cB = data.positions[m_indexB].c;
        float aB = data.positions[m_indexB].a;

        qA.set(aA);
        qB.set(aB);

        Rotation.mulToOutUnsafe(qA, temp.set(m_localAnchorA).subLocal(m_localCenterA), rA);
        Rotation.mulToOutUnsafe(qB, temp.set(m_localAnchorB).subLocal(m_localCenterB), rB);

        uA.set(cA).addLocal(rA).subLocal(m_groundAnchorA);
        uB.set(cB).addLocal(rB).subLocal(m_groundAnchorB);

        float lengthA = uA.length();
        float lengthB = uB.length();

        if (lengthA > 10.0f * JBoxSettings.linearSlop) {
            uA.mulLocal(1.0f / lengthA);
        } else {
            uA.setZero();
        }

        if (lengthB > 10.0f * JBoxSettings.linearSlop) {
            uB.mulLocal(1.0f / lengthB);
        } else {
            uB.setZero();
        }

        // Compute effective mass.
        float ruA = Vec2.cross(rA, uA);
        float ruB = Vec2.cross(rB, uB);

        float mA = m_invMassA + m_invIA * ruA * ruA;
        float mB = m_invMassB + m_invIB * ruB * ruB;

        float mass = mA + m_ratio * m_ratio * mB;

        if (mass > 0.0f) {
            mass = 1.0f / mass;
        }

        float C = m_constant - lengthA - m_ratio * lengthB;
        float linearError = FXGLMath.abs(C);

        float impulse = -mass * C;

        PA.set(uA).mulLocal(-impulse);
        PB.set(uB).mulLocal(-m_ratio * impulse);

        cA.x += m_invMassA * PA.x;
        cA.y += m_invMassA * PA.y;
        aA += m_invIA * Vec2.cross(rA, PA);
        cB.x += m_invMassB * PB.x;
        cB.y += m_invMassB * PB.y;
        aB += m_invIB * Vec2.cross(rB, PB);

//    data.positions[m_indexA].c.set(cA);
        data.positions[m_indexA].a = aA;
//    data.positions[m_indexB].c.set(cB);
        data.positions[m_indexB].a = aB;

        pool.pushRot(2);
        pool.pushVec2(7);

        return linearError < JBoxSettings.linearSlop;
    }
}
