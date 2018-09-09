/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
/**
 * Created at 7:27:31 AM Jan 21, 2011
 */
package com.almasb.fxgl.physics.box2d.dynamics.joints;

import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.physics.box2d.dynamics.Body;

/**
 * Wheel joint definition. This requires defining a line of motion using an axis and an anchor
 * point. The definition uses local anchor points and a local axis so that the initial configuration
 * can violate the constraint slightly. The joint translation is zero when the local anchor points
 * coincide in world space. Using local anchors and a local axis helps when saving and loading a
 * game.
 *
 * @author Daniel Murphy
 */
public class WheelJointDef extends JointDef {

    /**
     * The local anchor point relative to body1's origin.
     */
    public final Vec2 localAnchorA = new Vec2();

    /**
     * The local anchor point relative to body2's origin.
     */
    public final Vec2 localAnchorB = new Vec2();

    /**
     * The local translation axis in body1.
     */
    public final Vec2 localAxisA = new Vec2();

    /**
     * Enable/disable the joint motor.
     */
    public boolean enableMotor;

    /**
     * The maximum motor torque, usually in N-m.
     */
    public float maxMotorTorque;

    /**
     * The desired motor speed in radians per second.
     */
    public float motorSpeed;

    /**
     * Suspension frequency, zero indicates no suspension
     */
    public float frequencyHz;

    /**
     * Suspension damping ratio, one indicates critical damping
     */
    public float dampingRatio;

    public WheelJointDef() {
        super(JointType.WHEEL);
        localAxisA.set(1, 0);
        enableMotor = false;
        maxMotorTorque = 0f;
        motorSpeed = 0f;
    }

    public void initialize(Body b1, Body b2, Vec2 anchor, Vec2 axis) {
        bodyA = b1;
        bodyB = b2;
        b1.getLocalPointToOut(anchor, localAnchorA);
        b2.getLocalPointToOut(anchor, localAnchorB);
        bodyA.getLocalVectorToOut(axis, localAxisA);
    }
}
