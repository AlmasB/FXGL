/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics.box2d.dynamics.joints;

import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.physics.box2d.dynamics.Body;
import com.almasb.fxgl.physics.box2d.dynamics.World;

/**
 * Friction joint definition.
 *
 * @author Daniel Murphy
 */
public class FrictionJointDef extends JointDef<FrictionJoint> {

    /**
     * The local anchor point relative to bodyA's origin.
     */
    public final Vec2 localAnchorA = new Vec2();

    /**
     * The local anchor point relative to bodyB's origin.
     */
    public final Vec2 localAnchorB = new Vec2();

    /**
     * The maximum friction force in N.
     */
    public float maxForce = 0f;

    /**
     * The maximum friction torque in N-m.
     */
    public float maxTorque = 0f;

    /**
     * Initialize the bodies, anchors, axis, and reference angle using the world anchor and world
     * axis.
     */
    public void initialize(Body bA, Body bB, Vec2 anchor) {
        setBodyA(bA);
        setBodyB(bB);
        bA.getLocalPointToOut(anchor, localAnchorA);
        bB.getLocalPointToOut(anchor, localAnchorB);
    }

    @Override
    protected FrictionJoint createJoint(World world) {
        return new FrictionJoint(world.getPool(), this);
    }
}
