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

    public Joint m_prev = null;
    public Joint m_next = null;
    public final JointEdge m_edgeA = new JointEdge();
    public final JointEdge m_edgeB = new JointEdge();
    protected Body m_bodyA;
    protected Body m_bodyB;

    public boolean m_islandFlag = false;
    private final boolean m_collideConnected;

    private Object m_userData;

    protected final IWorldPool pool;

    protected Joint(IWorldPool worldPool, JointDef def) {
        pool = worldPool;

        m_bodyA = def.getBodyA();
        m_bodyB = def.getBodyB();
        m_collideConnected = def.isBodyCollisionAllowed();
        m_userData = def.getUserData();
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
