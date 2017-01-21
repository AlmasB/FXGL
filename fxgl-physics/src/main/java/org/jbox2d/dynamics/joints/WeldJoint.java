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
/**
 * Created at 3:38:38 AM Jan 15, 2011
 */
package org.jbox2d.dynamics.joints;

import com.almasb.fxgl.core.math.Vec2;
import org.jbox2d.common.*;
import org.jbox2d.dynamics.SolverData;
import org.jbox2d.pooling.IWorldPool;

//Point-to-point constraint
//C = p2 - p1
//Cdot = v2 - v1
//   = v2 + cross(w2, r2) - v1 - cross(w1, r1)
//J = [-I -r1_skew I r2_skew ]
//Identity used:
//w k % (rx i + ry j) = w * (-ry i + rx j)

//Angle constraint
//C = angle2 - angle1 - referenceAngle
//Cdot = w2 - w1
//J = [0 0 -1 0 0 1]
//K = invI1 + invI2

/**
 * A weld joint essentially glues two bodies together. A weld joint may distort somewhat because the
 * island constraint solver is approximate.
 *
 * @author Daniel Murphy
 */
public class WeldJoint extends Joint {

    private float m_frequencyHz;
    private float m_dampingRatio;
    private float m_bias;

    // Solver shared
    private final Vec2 m_localAnchorA;
    private final Vec2 m_localAnchorB;
    private float m_referenceAngle;
    private float m_gamma;
    private final Vec3 m_impulse;


    // Solver temp
    private int m_indexA;
    private int m_indexB;
    private final Vec2 m_rA = new Vec2();
    private final Vec2 m_rB = new Vec2();
    private final Vec2 m_localCenterA = new Vec2();
    private final Vec2 m_localCenterB = new Vec2();
    private float m_invMassA;
    private float m_invMassB;
    private float m_invIA;
    private float m_invIB;
    private final Mat33 m_mass = new Mat33();

    protected WeldJoint(IWorldPool argWorld, WeldJointDef def) {
        super(argWorld, def);
        m_localAnchorA = new Vec2(def.localAnchorA);
        m_localAnchorB = new Vec2(def.localAnchorB);
        m_referenceAngle = def.referenceAngle;
        m_frequencyHz = def.frequencyHz;
        m_dampingRatio = def.dampingRatio;

        m_impulse = new Vec3();
        m_impulse.setZero();
    }

    public float getReferenceAngle() {
        return m_referenceAngle;
    }

    public Vec2 getLocalAnchorA() {
        return m_localAnchorA;
    }

    public Vec2 getLocalAnchorB() {
        return m_localAnchorB;
    }

    public float getFrequency() {
        return m_frequencyHz;
    }

    public void setFrequency(float frequencyHz) {
        this.m_frequencyHz = frequencyHz;
    }

    public float getDampingRatio() {
        return m_dampingRatio;
    }

    public void setDampingRatio(float dampingRatio) {
        this.m_dampingRatio = dampingRatio;
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
        argOut.set(m_impulse.x, m_impulse.y);
        argOut.mulLocal(inv_dt);
    }

    @Override
    public float getReactionTorque(float inv_dt) {
        return inv_dt * m_impulse.z;
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

        // Vec2 cA = data.positions[m_indexA].c;
        float aA = data.positions[m_indexA].a;
        Vec2 vA = data.velocities[m_indexA].v;
        float wA = data.velocities[m_indexA].w;

        // Vec2 cB = data.positions[m_indexB].c;
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

        // J = [-I -r1_skew I r2_skew]
        // [ 0 -1 0 1]
        // r_skew = [-ry; rx]

        // Matlab
        // K = [ mA+r1y^2*iA+mB+r2y^2*iB, -r1y*iA*r1x-r2y*iB*r2x, -r1y*iA-r2y*iB]
        // [ -r1y*iA*r1x-r2y*iB*r2x, mA+r1x^2*iA+mB+r2x^2*iB, r1x*iA+r2x*iB]
        // [ -r1y*iA-r2y*iB, r1x*iA+r2x*iB, iA+iB]

        float mA = m_invMassA, mB = m_invMassB;
        float iA = m_invIA, iB = m_invIB;

        final Mat33 K = pool.popMat33();

        K.ex.x = mA + mB + m_rA.y * m_rA.y * iA + m_rB.y * m_rB.y * iB;
        K.ey.x = -m_rA.y * m_rA.x * iA - m_rB.y * m_rB.x * iB;
        K.ez.x = -m_rA.y * iA - m_rB.y * iB;
        K.ex.y = K.ey.x;
        K.ey.y = mA + mB + m_rA.x * m_rA.x * iA + m_rB.x * m_rB.x * iB;
        K.ez.y = m_rA.x * iA + m_rB.x * iB;
        K.ex.z = K.ez.x;
        K.ey.z = K.ez.y;
        K.ez.z = iA + iB;

        if (m_frequencyHz > 0.0f) {
            K.getInverse22(m_mass);

            float invM = iA + iB;
            float m = invM > 0.0f ? 1.0f / invM : 0.0f;

            float C = aB - aA - m_referenceAngle;

            // Frequency
            float omega = 2.0f * JBoxUtils.PI * m_frequencyHz;

            // Damping coefficient
            float d = 2.0f * m * m_dampingRatio * omega;

            // Spring stiffness
            float k = m * omega * omega;

            // magic formulas
            float h = data.step.dt;
            m_gamma = h * (d + h * k);
            m_gamma = m_gamma != 0.0f ? 1.0f / m_gamma : 0.0f;
            m_bias = C * h * k * m_gamma;

            invM += m_gamma;
            m_mass.ez.z = invM != 0.0f ? 1.0f / invM : 0.0f;
        } else {
            K.getSymInverse33(m_mass);
            m_gamma = 0.0f;
            m_bias = 0.0f;
        }

        if (data.step.warmStarting) {
            final Vec2 P = pool.popVec2();
            // Scale impulses to support a variable time step.
            m_impulse.mulLocal(data.step.dtRatio);

            P.set(m_impulse.x, m_impulse.y);

            vA.x -= mA * P.x;
            vA.y -= mA * P.y;
            wA -= iA * (Vec2.cross(m_rA, P) + m_impulse.z);

            vB.x += mB * P.x;
            vB.y += mB * P.y;
            wB += iB * (Vec2.cross(m_rB, P) + m_impulse.z);
            pool.pushVec2(1);
        } else {
            m_impulse.setZero();
        }

//    data.velocities[m_indexA].v.set(vA);
        data.velocities[m_indexA].w = wA;
//    data.velocities[m_indexB].v.set(vB);
        data.velocities[m_indexB].w = wB;

        pool.pushVec2(1);
        pool.pushRot(2);
        pool.pushMat33(1);
    }

    @Override
    public void solveVelocityConstraints(final SolverData data) {
        Vec2 vA = data.velocities[m_indexA].v;
        float wA = data.velocities[m_indexA].w;
        Vec2 vB = data.velocities[m_indexB].v;
        float wB = data.velocities[m_indexB].w;

        float mA = m_invMassA, mB = m_invMassB;
        float iA = m_invIA, iB = m_invIB;

        final Vec2 Cdot1 = pool.popVec2();
        final Vec2 P = pool.popVec2();
        final Vec2 temp = pool.popVec2();
        if (m_frequencyHz > 0.0f) {
            float Cdot2 = wB - wA;

            float impulse2 = -m_mass.ez.z * (Cdot2 + m_bias + m_gamma * m_impulse.z);
            m_impulse.z += impulse2;

            wA -= iA * impulse2;
            wB += iB * impulse2;

            Vec2.crossToOutUnsafe(wB, m_rB, Cdot1);
            Vec2.crossToOutUnsafe(wA, m_rA, temp);
            Cdot1.addLocal(vB).subLocal(vA).subLocal(temp);

            final Vec2 impulse1 = P;
            Mat33.mul22ToOutUnsafe(m_mass, Cdot1, impulse1);
            impulse1.negateLocal();

            m_impulse.x += impulse1.x;
            m_impulse.y += impulse1.y;

            vA.x -= mA * P.x;
            vA.y -= mA * P.y;
            wA -= iA * Vec2.cross(m_rA, P);

            vB.x += mB * P.x;
            vB.y += mB * P.y;
            wB += iB * Vec2.cross(m_rB, P);
        } else {
            Vec2.crossToOutUnsafe(wA, m_rA, temp);
            Vec2.crossToOutUnsafe(wB, m_rB, Cdot1);
            Cdot1.addLocal(vB).subLocal(vA).subLocal(temp);
            float Cdot2 = wB - wA;

            final Vec3 Cdot = pool.popVec3();
            Cdot.set(Cdot1.x, Cdot1.y, Cdot2);

            final Vec3 impulse = pool.popVec3();
            Mat33.mulToOutUnsafe(m_mass, Cdot, impulse);
            impulse.negateLocal();
            m_impulse.addLocal(impulse);

            P.set(impulse.x, impulse.y);

            vA.x -= mA * P.x;
            vA.y -= mA * P.y;
            wA -= iA * (Vec2.cross(m_rA, P) + impulse.z);

            vB.x += mB * P.x;
            vB.y += mB * P.y;
            wB += iB * (Vec2.cross(m_rB, P) + impulse.z);

            pool.pushVec3(2);
        }

//    data.velocities[m_indexA].v.set(vA);
        data.velocities[m_indexA].w = wA;
//    data.velocities[m_indexB].v.set(vB);
        data.velocities[m_indexB].w = wB;

        pool.pushVec2(3);
    }

    @Override
    public boolean solvePositionConstraints(final SolverData data) {
        Vec2 cA = data.positions[m_indexA].c;
        float aA = data.positions[m_indexA].a;
        Vec2 cB = data.positions[m_indexB].c;
        float aB = data.positions[m_indexB].a;
        final Rotation qA = pool.popRot();
        final Rotation qB = pool.popRot();
        final Vec2 temp = pool.popVec2();
        final Vec2 rA = pool.popVec2();
        final Vec2 rB = pool.popVec2();

        qA.set(aA);
        qB.set(aB);

        float mA = m_invMassA, mB = m_invMassB;
        float iA = m_invIA, iB = m_invIB;

        Rotation.mulToOutUnsafe(qA, temp.set(m_localAnchorA).subLocal(m_localCenterA), rA);
        Rotation.mulToOutUnsafe(qB, temp.set(m_localAnchorB).subLocal(m_localCenterB), rB);
        float positionError, angularError;

        final Mat33 K = pool.popMat33();
        final Vec2 C1 = pool.popVec2();
        final Vec2 P = pool.popVec2();

        K.ex.x = mA + mB + rA.y * rA.y * iA + rB.y * rB.y * iB;
        K.ey.x = -rA.y * rA.x * iA - rB.y * rB.x * iB;
        K.ez.x = -rA.y * iA - rB.y * iB;
        K.ex.y = K.ey.x;
        K.ey.y = mA + mB + rA.x * rA.x * iA + rB.x * rB.x * iB;
        K.ez.y = rA.x * iA + rB.x * iB;
        K.ex.z = K.ez.x;
        K.ey.z = K.ez.y;
        K.ez.z = iA + iB;
        if (m_frequencyHz > 0.0f) {
            C1.set(cB).addLocal(rB).subLocal(cA).subLocal(rA);

            positionError = C1.length();
            angularError = 0.0f;

            K.solve22ToOut(C1, P);
            P.negateLocal();

            cA.x -= mA * P.x;
            cA.y -= mA * P.y;
            aA -= iA * Vec2.cross(rA, P);

            cB.x += mB * P.x;
            cB.y += mB * P.y;
            aB += iB * Vec2.cross(rB, P);
        } else {
            C1.set(cB).addLocal(rB).subLocal(cA).subLocal(rA);
            float C2 = aB - aA - m_referenceAngle;

            positionError = C1.length();
            angularError = JBoxUtils.abs(C2);

            final Vec3 C = pool.popVec3();
            final Vec3 impulse = pool.popVec3();
            C.set(C1.x, C1.y, C2);

            K.solve33ToOut(C, impulse);
            impulse.negateLocal();
            P.set(impulse.x, impulse.y);

            cA.x -= mA * P.x;
            cA.y -= mA * P.y;
            aA -= iA * (Vec2.cross(rA, P) + impulse.z);

            cB.x += mB * P.x;
            cB.y += mB * P.y;
            aB += iB * (Vec2.cross(rB, P) + impulse.z);
            pool.pushVec3(2);
        }

//    data.positions[m_indexA].c.set(cA);
        data.positions[m_indexA].a = aA;
//    data.positions[m_indexB].c.set(cB);
        data.positions[m_indexB].a = aB;

        pool.pushVec2(5);
        pool.pushRot(2);
        pool.pushMat33(1);

        return positionError <= JBoxSettings.linearSlop && angularError <= JBoxSettings.angularSlop;
    }
}
