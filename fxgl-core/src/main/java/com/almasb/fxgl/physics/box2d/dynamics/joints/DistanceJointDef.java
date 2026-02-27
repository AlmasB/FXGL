/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
/*
 * JBox2D - A Java Port of Erin Catto's Box2D
 *
 * JBox2D homepage: http://jbox2d.sourceforge.net/
 * Box2D homepage: http://www.box2d.org
 *
 * This software is provided 'as-is', without any express or implied
 * warranty.  In no event will the authors be held liable for any damages
 * arising from the use of this software.
 *
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 *
 * 1. The origin of this software must not be misrepresented; you must not
 * claim that you wrote the original software. If you use this software
 * in a product, an acknowledgment in the product documentation would be
 * appreciated but is not required.
 * 2. Altered source versions must be plainly marked as such, and must not be
 * misrepresented as being the original software.
 * 3. This notice may not be removed or altered from any source distribution.
 */

package com.almasb.fxgl.physics.box2d.dynamics.joints;

import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.physics.box2d.dynamics.Body;
import com.almasb.fxgl.physics.box2d.dynamics.World;

/**
 * Distance joint definition.
 * This requires defining an anchor point on both bodies and the non-zero length of the distance joint.
 * The definition uses local anchor points so that the initial configuration can violate the constraint slightly.
 * This helps when saving and loading a game.
 * Do not use a zero or short length.
 */
public class DistanceJointDef extends JointDef<DistanceJoint> {

    /**
     * The local anchor point relative to body1's origin.
     */
    public final Vec2 localAnchorA = new Vec2();

    /**
     * The local anchor point relative to body2's origin.
     */
    public final Vec2 localAnchorB = new Vec2();

    /**
     * The equilibrium length between the anchor points.
     */
    public float length = 1.0f;

    /**
     * The mass-spring-damper frequency in Hertz.
     */
    public float frequencyHz = 0.0f;

    /**
     * The damping ratio.
     * 0 = no damping, 1 = critical damping.
     */
    public float dampingRatio = 0.0f;

    /**
     * Initialize the bodies, anchors, and length using the world anchors.
     *
     * @param anchor1 World anchor on first body
     * @param anchor2 World anchor on second body
     */
    public void initialize(Body b1, Body b2, Vec2 anchor1, Vec2 anchor2) {
        setBodyA(b1);
        setBodyB(b2);
        localAnchorA.set(b1.getLocalPoint(anchor1));
        localAnchorB.set(b2.getLocalPoint(anchor2));
        Vec2 d = anchor2.sub(anchor1);
        length = d.length();
    }

    @Override
    protected DistanceJoint createJoint(World world) {
        return new DistanceJoint(world.getPool(), this);
    }
}
