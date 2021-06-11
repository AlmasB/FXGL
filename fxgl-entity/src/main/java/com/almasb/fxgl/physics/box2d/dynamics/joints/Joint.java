/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package com.almasb.fxgl.physics.box2d.dynamics.joints;

import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.physics.box2d.dynamics.Body;
import com.almasb.fxgl.physics.box2d.dynamics.SolverData;
import com.almasb.fxgl.physics.box2d.dynamics.World;
import com.almasb.fxgl.physics.box2d.pooling.IWorldPool;

// updated to rev 100

/**
 * The base joint class. Joints are used to constrain two bodies together in various fashions. Some
 * joints also feature limits and motors.
 *
 * @author Daniel Murphy
 */
public abstract class Joint {

    public static <T extends Joint> T create(World world, JointDef<T> def) {
        return def.createJoint(world);
    }

    public static void destroy(Joint joint) {
        joint.destructor();
    }

    public Joint m_prev;
    public Joint m_next;
    public JointEdge m_edgeA;
    public JointEdge m_edgeB;
    protected Body m_bodyA;
    protected Body m_bodyB;

    public boolean m_islandFlag;
    private boolean m_collideConnected;

    public Object m_userData;

    protected IWorldPool pool;

    // Cache here per time step to reduce cache misses.
    // final Vec2 m_localCenterA, m_localCenterB;
    // float m_invMassA, m_invIA;
    // float m_invMassB, m_invIB;

    protected Joint(IWorldPool worldPool, JointDef def) {
        pool = worldPool;

        m_prev = null;
        m_next = null;
        m_bodyA = def.getBodyA();
        m_bodyB = def.getBodyB();
        m_collideConnected = def.isBodyCollisionAllowed();
        m_islandFlag = false;
        m_userData = def.getUserData();

        m_edgeA = new JointEdge();
        m_edgeA.joint = null;
        m_edgeA.other = null;
        m_edgeA.prev = null;
        m_edgeA.next = null;

        m_edgeB = new JointEdge();
        m_edgeB.joint = null;
        m_edgeB.other = null;
        m_edgeB.prev = null;
        m_edgeB.next = null;

        // m_localCenterA = new Vec2();
        // m_localCenterB = new Vec2();
    }

    /**
     * @return the first body attached to this joint
     */
    public final Body getBodyA() {
        return m_bodyA;
    }

    /**
     * @return the second body attached to this joint
     */
    public final Body getBodyB() {
        return m_bodyB;
    }

    /**
     * Get the anchor point on bodyA in world coordinates
     */
    public abstract void getAnchorA(Vec2 out);

    /**
     * Get the anchor point on bodyB in world coordinates
     */
    public abstract void getAnchorB(Vec2 out);

    /**
     * Get the reaction force on body2 at the joint anchor in Newtons.
     */
    public abstract void getReactionForce(float inv_dt, Vec2 out);

    /**
     * @return the reaction torque on body2 in N*m
     */
    public abstract float getReactionTorque(float inv_dt);

    /**
     * @return the next joint the world joint list.
     */
    public Joint getNext() {
        return m_next;
    }

    /**
     * @return the user data pointer.
     */
    public Object getUserData() {
        return m_userData;
    }

    /**
     * Set the user data pointer.
     */
    public void setUserData(Object data) {
        m_userData = data;
    }

    /**
     * Get collide connected. Note: modifying the collide connect flag won't work correctly because
     * the flag is only checked when fixture AABBs begin to overlap.
     */
    public final boolean getCollideConnected() {
        return m_collideConnected;
    }

    /**
     * Short-cut function to determine if either body is inactive.
     */
    public boolean isActive() {
        return m_bodyA.isActive() && m_bodyB.isActive();
    }

    /** Internal */
    public abstract void initVelocityConstraints(SolverData data);

    /** Internal */
    public abstract void solveVelocityConstraints(SolverData data);

    /**
     * This returns true if the position errors are within tolerance. Internal.
     */
    public abstract boolean solvePositionConstraints(SolverData data);

    /**
     * Override to handle destruction of joint
     */
    public void destructor() {
        // no default implementation
    }
}
