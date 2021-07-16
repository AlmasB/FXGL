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
 * Requires two ground anchors, two dynamic body anchor points, and a pulley ratio.
 *
 * @author Daniel Murphy
 */
public class PulleyJointDef extends JointDef<PulleyJoint> {

    /**
     * The first ground anchor in world coordinates. This point never moves.
     */
    public Vec2 groundAnchorA = new Vec2(-1.0f, 1.0f);

    /**
     * The second ground anchor in world coordinates. This point never moves.
     */
    public Vec2 groundAnchorB = new Vec2(1.0f, 1.0f);

    /**
     * The local anchor point relative to bodyA's origin.
     */
    public Vec2 localAnchorA = new Vec2(-1.0f, 0.0f);

    /**
     * The local anchor point relative to bodyB's origin.
     */
    public Vec2 localAnchorB = new Vec2(1.0f, 0.0f);

    /**
     * The a reference length for the segment attached to bodyA.
     */
    public float lengthA = 0f;

    /**
     * The a reference length for the segment attached to bodyB.
     */
    public float lengthB = 0f;

    /**
     * The pulley ratio, used to simulate a block-and-tackle.
     * Must be > than JBoxSettings.EPSILON.
     */
    public float ratio = 1f;

    public PulleyJointDef() {
        setBodyCollisionAllowed(true);
    }

    /**
     * Initialize the bodies, anchors, lengths, max lengths, and ratio using the world anchors.
     */
    public void initialize(Body b1, Body b2, Vec2 ga1, Vec2 ga2, Vec2 anchor1, Vec2 anchor2, float r) {
        setBodyA(b1);
        setBodyB(b2);
        groundAnchorA = ga1;
        groundAnchorB = ga2;
        localAnchorA = b1.getLocalPoint(anchor1);
        localAnchorB = b2.getLocalPoint(anchor2);
        Vec2 d1 = anchor1.sub(ga1);
        lengthA = d1.length();
        Vec2 d2 = anchor2.sub(ga2);
        lengthB = d2.length();
        ratio = r;
    }

    @Override
    protected PulleyJoint createJoint(World world) {
        return new PulleyJoint(world.getPool(), this);
    }
}
