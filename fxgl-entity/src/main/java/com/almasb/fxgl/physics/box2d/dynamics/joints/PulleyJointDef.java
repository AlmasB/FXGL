/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
/**
 * Created at 12:11:41 PM Jan 23, 2011
 */
package com.almasb.fxgl.physics.box2d.dynamics.joints;

import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.physics.box2d.common.JBoxSettings;
import com.almasb.fxgl.physics.box2d.dynamics.Body;

/**
 * Pulley joint definition. This requires two ground anchors, two dynamic body anchor points, and a
 * pulley ratio.
 *
 * @author Daniel Murphy
 */
public class PulleyJointDef extends JointDef {

    /**
     * The first ground anchor in world coordinates. This point never moves.
     */
    public Vec2 groundAnchorA;

    /**
     * The second ground anchor in world coordinates. This point never moves.
     */
    public Vec2 groundAnchorB;

    /**
     * The local anchor point relative to bodyA's origin.
     */
    public Vec2 localAnchorA;

    /**
     * The local anchor point relative to bodyB's origin.
     */
    public Vec2 localAnchorB;

    /**
     * The a reference length for the segment attached to bodyA.
     */
    public float lengthA;

    /**
     * The a reference length for the segment attached to bodyB.
     */
    public float lengthB;

    /**
     * The pulley ratio, used to simulate a block-and-tackle.
     */
    public float ratio;

    public PulleyJointDef() {
        super(JointType.PULLEY);
        groundAnchorA = new Vec2(-1.0f, 1.0f);
        groundAnchorB = new Vec2(1.0f, 1.0f);
        localAnchorA = new Vec2(-1.0f, 0.0f);
        localAnchorB = new Vec2(1.0f, 0.0f);
        lengthA = 0.0f;
        lengthB = 0.0f;
        ratio = 1.0f;
        collideConnected = true;
    }

    /**
     * Initialize the bodies, anchors, lengths, max lengths, and ratio using the world anchors.
     */
    public void initialize(Body b1, Body b2, Vec2 ga1, Vec2 ga2, Vec2 anchor1, Vec2 anchor2, float r) {
        bodyA = b1;
        bodyB = b2;
        groundAnchorA = ga1;
        groundAnchorB = ga2;
        localAnchorA = bodyA.getLocalPoint(anchor1);
        localAnchorB = bodyB.getLocalPoint(anchor2);
        Vec2 d1 = anchor1.sub(ga1);
        lengthA = d1.length();
        Vec2 d2 = anchor2.sub(ga2);
        lengthB = d2.length();
        ratio = r;
        assert ratio > JBoxSettings.EPSILON;
    }
}
