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
 * Revolute joint definition. This requires defining an anchor point where the bodies are joined.
 * The definition uses local anchor points so that the initial configuration can violate the
 * constraint slightly. You also need to specify the initial relative angle for joint limits. This
 * helps when saving and loading a game. The local anchor points are measured from the body's origin
 * rather than the center of mass because:<br/>
 * <ul>
 * <li>you might not know where the center of mass will be.</li>
 * <li>if you add/remove shapes from a body and recompute the mass, the joints will be broken.</li>
 * </ul>
 */
public final class RevoluteJointDef extends JointDef<RevoluteJoint> {

    /**
     * The local anchor point relative to body1's origin.
     */
    public Vec2 localAnchorA = new Vec2();

    /**
     * The local anchor point relative to body2's origin.
     */
    public Vec2 localAnchorB = new Vec2();

    /**
     * The body2 angle minus body1 angle in the reference state (radians).
     */
    public float referenceAngle = 0f;

    /**
     * A flag to enable joint limits.
     */
    public boolean enableLimit = false;

    /**
     * The lower angle for the joint limit (radians).
     */
    public float lowerAngle = 0f;

    /**
     * The upper angle for the joint limit (radians).
     */
    public float upperAngle = 0f;

    /**
     * A flag to enable the joint motor.
     */
    public boolean enableMotor = false;

    /**
     * The desired motor speed. Usually in radians per second.
     */
    public float motorSpeed = 0f;

    /**
     * The maximum motor torque used to achieve the desired motor speed. Usually in N-m.
     */
    public float maxMotorTorque = 0f;

    /**
     * Initialize the bodies, anchors and reference angle using the anchor in world coordinates.
     */
    public void initialize(Body b1, Body b2, Vec2 anchor) {
        setBodyA(b1);
        setBodyB(b2);
        b1.getLocalPointToOut(anchor, localAnchorA);
        b2.getLocalPointToOut(anchor, localAnchorB);

        referenceAngle = b2.getAngle() - b1.getAngle();
    }

    @Override
    protected RevoluteJoint createJoint(World world) {
        return new RevoluteJoint(world.getPool(), this);
    }
}
