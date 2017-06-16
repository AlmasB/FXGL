/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package com.almasb.fxgl.physics.box2d.dynamics.joints;

import com.almasb.fxgl.core.math.Vec2;

/**
 * Mouse joint definition. This requires a world target point, tuning parameters, and the time step.
 *
 * @author Daniel
 */
public class MouseJointDef extends JointDef {
    /**
     * The initial world target point. This is assumed to coincide with the body anchor initially.
     */
    public final Vec2 target = new Vec2();

    /**
     * The maximum constraint force that can be exerted to move the candidate body. Usually you will
     * express as some multiple of the weight (multiplier * mass * gravity).
     */
    public float maxForce;

    /**
     * The response speed.
     */
    public float frequencyHz;

    /**
     * The damping ratio. 0 = no damping, 1 = critical damping.
     */
    public float dampingRatio;

    public MouseJointDef() {
        super(JointType.MOUSE);
        target.set(0, 0);
        maxForce = 0;
        frequencyHz = 5;
        dampingRatio = .7f;
    }
}
