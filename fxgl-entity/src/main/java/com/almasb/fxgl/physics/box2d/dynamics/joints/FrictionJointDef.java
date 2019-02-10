/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
/**
 * Created at 7:23:39 AM Jan 20, 2011
 */
package com.almasb.fxgl.physics.box2d.dynamics.joints;

import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.physics.box2d.dynamics.Body;

/**
 * Friction joint definition.
 *
 * @author Daniel Murphy
 */
public class FrictionJointDef extends JointDef {


    /**
     * The local anchor point relative to bodyA's origin.
     */
    public final Vec2 localAnchorA;

    /**
     * The local anchor point relative to bodyB's origin.
     */
    public final Vec2 localAnchorB;

    /**
     * The maximum friction force in N.
     */
    public float maxForce;

    /**
     * The maximum friction torque in N-m.
     */
    public float maxTorque;

    public FrictionJointDef() {
        super(JointType.FRICTION);
        localAnchorA = new Vec2();
        localAnchorB = new Vec2();
        maxForce = 0f;
        maxTorque = 0f;
    }

    /**
     * Initialize the bodies, anchors, axis, and reference angle using the world anchor and world
     * axis.
     */
    public void initialize(Body bA, Body bB, Vec2 anchor) {
        bodyA = bA;
        bodyB = bB;
        bA.getLocalPointToOut(anchor, localAnchorA);
        bB.getLocalPointToOut(anchor, localAnchorB);
    }
}
