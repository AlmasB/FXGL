/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics.box2d.dynamics.joints;

import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.physics.box2d.common.JBoxUtils;
import com.almasb.fxgl.physics.box2d.common.Mat22;
import com.almasb.fxgl.physics.box2d.common.Rotation;
import com.almasb.fxgl.physics.box2d.dynamics.SolverData;
import com.almasb.fxgl.physics.box2d.pooling.IWorldPool;

//Point-to-point constraint
//Cdot = v2 - v1
//   = v2 + cross(w2, r2) - v1 - cross(w1, r1)
//J = [-I -r1_skew I r2_skew ]
//Identity used:
//w k % (rx i + ry j) = w * (-ry i + rx j)

//Angle constraint
//Cdot = w2 - w1
//J = [0 0 -1 0 0 1]
//K = invI1 + invI2

/**
 * A motor joint is used to control the relative motion between two bodies. A typical usage is to
 * control the movement of a dynamic body with respect to the ground.
 *
 * @author dmurph
 */
public class MotorJoint extends Joint {

    // Solver shared
    private final Vec2 linearOffset = new Vec2();
    private float angularOffset;
    private final Vec2 linearImpulse = new Vec2();
    private float angularImpulse;
    private float maxForce;
    private float maxTorque;
    private float correctionFactor;

    // Solver temp
    private int m_indexA;
    private int m_indexB;
    private final Vec2 m_rA = new Vec2();
    private final Vec2 m_rB = new Vec2();
    private final Vec2 m_localCenterA = new Vec2();
    private final Vec2 m_localCenterB = new Vec2();
    private final Vec2 m_linearError = new Vec2();
    private float m_angularError;
    private float m_invMassA;
    private float m_invMassB;
    private float m_invIA;
    private float m_invIB;
    private final Mat22 m_linearMass = new Mat22();
    private float m_angularMass;

    public MotorJoint(IWorldPool pool, MotorJointDef def) {
        super(pool, def);
        linearOffset.set(def.linearOffset);
        angularOffset = def.angularOffset;

        angularImpulse = 0.0f;

        maxForce = def.maxForce;
        maxTorque = def.maxTorque;
        correctionFactor = def.correctionFactor;
    }

    @Override
    public void getAnchorA(Vec2 out) {
        out.set(m_bodyA.getPosition());
    }

    @Override
    public void getAnchorB(Vec2 out) {
        out.set(m_bodyB.getPosition());
    }

    public void getReactionForce(float inv_dt, Vec2 out) {
        out.set(linearImpulse).mulLocal(inv_dt);
    }

    public float getReactionTorque(float inv_dt) {
        return angularImpulse * inv_dt;
    }

    public float getCorrectionFactor() {
        return correctionFactor;
    }

    public void setCorrectionFactor(float correctionFactor) {
        this.correctionFactor = correctionFactor;
    }

    /**
     * Set the target linear offset, in frame A, in meters.
     */
    public void setLinearOffset(Vec2 linearOffset) {
        if (linearOffset.x != this.linearOffset.x || linearOffset.y != this.linearOffset.y) {
            m_bodyA.setAwake(true);
            m_bodyB.setAwake(true);
            this.linearOffset.set(linearOffset);
        }
    }

    /**
     * Get the target linear offset, in frame A, in meters.
     */
    public void getLinearOffset(Vec2 out) {
        out.set(linearOffset);
    }

    /**
     * Get the target linear offset, in frame A, in meters. Do not modify.
     */
    public Vec2 getLinearOffset() {
        return linearOffset;
    }

    /**
     * Set the target angular offset, in radians.
     *
     * @param angularOffset
     */
    public void setAngularOffset(float angularOffset) {
        if (angularOffset != this.angularOffset) {
            m_bodyA.setAwake(true);
            m_bodyB.setAwake(true);
            this.angularOffset = angularOffset;
        }
    }

    public float getAngularOffset() {
        return angularOffset;
    }

    /**
     * Set the maximum friction force in N.
     *
     * @param force
     */
    public void setMaxForce(float force) {
        assert force >= 0.0f;
        maxForce = force;
    }

    /**
     * Get the maximum friction force in N.
     */
    public float getMaxForce() {
        return maxForce;
    }

    /**
     * Set the maximum friction torque in N*m.
     */
    public void setMaxTorque(float torque) {
        assert torque >= 0.0f;
        maxTorque = torque;
    }

    /**
     * Get the maximum friction torque in N*m.
     */
    public float getMaxTorque() {
        return maxTorque;
    }

    @Override
    public void initVelocityConstraints(SolverData data) {
        m_indexA = m_bodyA.m_islandIndex;
        m_indexB = m_bodyB.m_islandIndex;
        m_localCenterA.set(m_bodyA.m_sweep.localCenter);
        m_localCenterB.set(m_bodyB.m_sweep.localCenter);
        m_invMassA = m_bodyA.m_invMass;
        m_invMassB = m_bodyB.m_invMass;
        m_invIA = m_bodyA.m_invI;
        m_invIB = m_bodyB.m_invI;

        final Vec2 cA = data.positions[m_indexA].c;
        float aA = data.positions[m_indexA].a;
        final Vec2 vA = data.velocities[m_indexA].v;
        float wA = data.velocities[m_indexA].w;

        final Vec2 cB = data.positions[m_indexB].c;
        float aB = data.positions[m_indexB].a;
        final Vec2 vB = data.velocities[m_indexB].v;
        float wB = data.velocities[m_indexB].w;

        final Rotation qA = pool.popRot();
        final Rotation qB = pool.popRot();
        final Vec2 temp = pool.popVec2();
        Mat22 K = pool.popMat22();

        qA.set(aA);
        qB.set(aB);

        // Compute the effective mass matrix.
        // m_rA = b2Mul(qA, -m_localCenterA);
        // m_rB = b2Mul(qB, -m_localCenterB);
        m_rA.x = qA.c * -m_localCenterA.x - qA.s * -m_localCenterA.y;
        m_rA.y = qA.s * -m_localCenterA.x + qA.c * -m_localCenterA.y;
        m_rB.x = qB.c * -m_localCenterB.x - qB.s * -m_localCenterB.y;
        m_rB.y = qB.s * -m_localCenterB.x + qB.c * -m_localCenterB.y;

        // J = [-I -r1_skew I r2_skew]
        // [ 0 -1 0 1]
        // r_skew = [-ry; rx]

        // Matlab
        // K = [ mA+r1y^2*iA+mB+r2y^2*iB, -r1y*iA*r1x-r2y*iB*r2x, -r1y*iA-r2y*iB]
        // [ -r1y*iA*r1x-r2y*iB*r2x, mA+r1x^2*iA+mB+r2x^2*iB, r1x*iA+r2x*iB]
        // [ -r1y*iA-r2y*iB, r1x*iA+r2x*iB, iA+iB]
        float mA = m_invMassA, mB = m_invMassB;
        float iA = m_invIA, iB = m_invIB;

        K.ex.x = mA + mB + iA * m_rA.y * m_rA.y + iB * m_rB.y * m_rB.y;
        K.ex.y = -iA * m_rA.x * m_rA.y - iB * m_rB.x * m_rB.y;
        K.ey.x = K.ex.y;
        K.ey.y = mA + mB + iA * m_rA.x * m_rA.x + iB * m_rB.x * m_rB.x;

        K.invertToOut(m_linearMass);

        m_angularMass = iA + iB;
        if (m_angularMass > 0.0f) {
            m_angularMass = 1.0f / m_angularMass;
        }

        // m_linearError = cB + m_rB - cA - m_rA - b2Mul(qA, m_linearOffset);
        Rotation.mulToOutUnsafe(qA, linearOffset, temp);
        m_linearError.x = cB.x + m_rB.x - cA.x - m_rA.x - temp.x;
        m_linearError.y = cB.y + m_rB.y - cA.y - m_rA.y - temp.y;
        m_angularError = aB - aA - angularOffset;

        if (data.step.warmStarting) {
            // Scale impulses to support a variable time step.
            linearImpulse.x *= data.step.dtRatio;
            linearImpulse.y *= data.step.dtRatio;
            angularImpulse *= data.step.dtRatio;

            final Vec2 P = linearImpulse;
            vA.x -= mA * P.x;
            vA.y -= mA * P.y;
            wA -= iA * (m_rA.x * P.y - m_rA.y * P.x + angularImpulse);
            vB.x += mB * P.x;
            vB.y += mB * P.y;
            wB += iB * (m_rB.x * P.y - m_rB.y * P.x + angularImpulse);
        } else {
            linearImpulse.setZero();
            angularImpulse = 0.0f;
        }

        pool.pushVec2(1);
        pool.pushMat22(1);
        pool.pushRot(2);

        // data.velocities[m_indexA].v = vA;
        data.velocities[m_indexA].w = wA;
        // data.velocities[m_indexB].v = vB;
        data.velocities[m_indexB].w = wB;
    }

    @Override
    public void solveVelocityConstraints(SolverData data) {
        final Vec2 vA = data.velocities[m_indexA].v;
        float wA = data.velocities[m_indexA].w;
        final Vec2 vB = data.velocities[m_indexB].v;
        float wB = data.velocities[m_indexB].w;

        float mA = m_invMassA;
        float mB = m_invMassB;
        float iA = m_invIA;
        float iB = m_invIB;

        float h = data.step.dt;
        float inv_h = data.step.inv_dt;

        final Vec2 temp = pool.popVec2();

        // Solve angular friction
        {
            float Cdot = wB - wA + inv_h * correctionFactor * m_angularError;
            float impulse = -m_angularMass * Cdot;

            float oldImpulse = angularImpulse;
            float maxImpulse = h * maxTorque;
            angularImpulse = JBoxUtils.clamp(angularImpulse + impulse, -maxImpulse, maxImpulse);
            impulse = angularImpulse - oldImpulse;

            wA -= iA * impulse;
            wB += iB * impulse;
        }

        final Vec2 Cdot = pool.popVec2();

        // Solve linear friction
        {
            // Cdot = vB + b2Cross(wB, m_rB) - vA - b2Cross(wA, m_rA) + inv_h * m_correctionFactor *
            // m_linearError;
            Cdot.x =
                    vB.x + -wB * m_rB.y - vA.x - -wA * m_rA.y + inv_h * correctionFactor * m_linearError.x;
            Cdot.y =
                    vB.y + wB * m_rB.x - vA.y - wA * m_rA.x + inv_h * correctionFactor * m_linearError.y;

            final Vec2 impulse = temp;
            Mat22.mulToOutUnsafe(m_linearMass, Cdot, impulse);
            impulse.negateLocal();
            final Vec2 oldImpulse = pool.popVec2();
            oldImpulse.set(linearImpulse);
            linearImpulse.addLocal(impulse);

            float maxImpulse = h * maxForce;

            if (linearImpulse.lengthSquared() > maxImpulse * maxImpulse) {
                linearImpulse.getLengthAndNormalize();
                linearImpulse.mulLocal(maxImpulse);
            }

            impulse.x = linearImpulse.x - oldImpulse.x;
            impulse.y = linearImpulse.y - oldImpulse.y;

            vA.x -= mA * impulse.x;
            vA.y -= mA * impulse.y;
            wA -= iA * (m_rA.x * impulse.y - m_rA.y * impulse.x);

            vB.x += mB * impulse.x;
            vB.y += mB * impulse.y;
            wB += iB * (m_rB.x * impulse.y - m_rB.y * impulse.x);
        }

        pool.pushVec2(3);

        // data.velocities[m_indexA].v.set(vA);
        data.velocities[m_indexA].w = wA;
        // data.velocities[m_indexB].v.set(vB);
        data.velocities[m_indexB].w = wB;
    }

    @Override
    public boolean solvePositionConstraints(SolverData data) {
        return true;
    }
}
