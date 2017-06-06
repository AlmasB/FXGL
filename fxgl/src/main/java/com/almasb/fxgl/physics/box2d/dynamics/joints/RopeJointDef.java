/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics.box2d.dynamics.joints;

import com.almasb.fxgl.core.math.Vec2;

/**
 * Rope joint definition. This requires two body anchor points and a maximum lengths. Note: by
 * default the connected objects will not collide. see collideConnected in b2JointDef.
 *
 * @author Daniel Murphy
 */
public class RopeJointDef extends JointDef {

    /**
     * The local anchor point relative to bodyA's origin.
     */
    public final Vec2 localAnchorA = new Vec2();

    /**
     * The local anchor point relative to bodyB's origin.
     */
    public final Vec2 localAnchorB = new Vec2();

    /**
     * The maximum length of the rope. Warning: this must be larger than b2_linearSlop or the joint
     * will have no effect.
     */
    public float maxLength;

    public RopeJointDef() {
        super(JointType.ROPE);
        localAnchorA.set(-1.0f, 0.0f);
        localAnchorB.set(1.0f, 0.0f);
    }
}
