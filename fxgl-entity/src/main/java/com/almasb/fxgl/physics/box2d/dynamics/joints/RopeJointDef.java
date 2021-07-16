/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics.box2d.dynamics.joints;

import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.physics.box2d.dynamics.World;

/**
 * Requires two body anchor points and a maximum length.
 *
 * @author Daniel Murphy
 */
public class RopeJointDef extends JointDef<RopeJoint> {

    /**
     * The local anchor point relative to bodyA's origin.
     */
    public final Vec2 localAnchorA = new Vec2(-1, 0);

    /**
     * The local anchor point relative to bodyB's origin.
     */
    public final Vec2 localAnchorB = new Vec2(1, 0);

    /**
     * The maximum length of the rope.
     * Note: this must be larger than JBoxSettings.linearSlop or the joint will have no effect.
     */
    public float maxLength = 0f;

    @Override
    protected RopeJoint createJoint(World world) {
        return new RopeJoint(world.getPool(), this);
    }
}
