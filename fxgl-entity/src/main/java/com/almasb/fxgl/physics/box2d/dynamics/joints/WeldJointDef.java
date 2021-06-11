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
 * Created at 3:38:52 AM Jan 15, 2011
 */

/**
 * @author Daniel Murphy
 */
public class WeldJointDef extends JointDef<WeldJoint> {
    /**
     * The local anchor point relative to body1's origin.
     */
    public final Vec2 localAnchorA;

    /**
     * The local anchor point relative to body2's origin.
     */
    public final Vec2 localAnchorB;

    /**
     * The body2 angle minus body1 angle in the reference state (radians).
     */
    public float referenceAngle;

    /**
     * The mass-spring-damper frequency in Hertz. Rotation only. Disable softness with a value of 0.
     */
    public float frequencyHz;

    /**
     * The damping ratio. 0 = no damping, 1 = critical damping.
     */
    public float dampingRatio;

    public WeldJointDef() {
        localAnchorA = new Vec2();
        localAnchorB = new Vec2();
        referenceAngle = 0.0f;
    }

    /**
     * Initialize the bodies, anchors, and reference angle using a world anchor point.
     *
     * @param bA
     * @param bB
     * @param anchor
     */
    public void initialize(Body bA, Body bB, Vec2 anchor) {
        setBodyA(bA);
        setBodyB(bB);
        bA.getLocalPointToOut(anchor, localAnchorA);
        bB.getLocalPointToOut(anchor, localAnchorB);
        referenceAngle = bB.getAngle() - bA.getAngle();
    }

    @Override
    protected WeldJoint createJoint(World world) {
        return new WeldJoint(world.getPool(), this);
    }
}
