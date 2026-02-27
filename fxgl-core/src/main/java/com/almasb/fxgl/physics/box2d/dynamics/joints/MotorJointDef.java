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
 * Motor joint definition.
 *
 * @author dmurph
 */
public class MotorJointDef extends JointDef<MotorJoint> {
    /**
     * Position of bodyB minus the position of bodyA, in bodyA's frame, in meters.
     */
    public final Vec2 linearOffset = new Vec2();

    /**
     * The bodyB angle minus bodyA angle in radians.
     */
    public float angularOffset;

    /**
     * The maximum motor force in N.
     */
    public float maxForce;

    /**
     * The maximum motor torque in N-m.
     */
    public float maxTorque;

    /**
     * Position correction factor in the range [0,1].
     */
    public float correctionFactor;

    public MotorJointDef() {
        angularOffset = 0;
        maxForce = 1;
        maxTorque = 1;
        correctionFactor = 0.3f;
    }

    public void initialize(Body bA, Body bB) {
        setBodyA(bA);
        setBodyB(bB);

        Vec2 xB = bB.getPosition();
        bA.getLocalPointToOut(xB, linearOffset);

        float angleA = bA.getAngle();
        float angleB = bB.getAngle();
        angularOffset = angleB - angleA;
    }

    @Override
    protected MotorJoint createJoint(World world) {
        return new MotorJoint(world.getPool(), this);
    }
}
