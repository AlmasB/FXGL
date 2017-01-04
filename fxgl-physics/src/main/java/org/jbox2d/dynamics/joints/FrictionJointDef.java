/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
/**
 * Created at 7:23:39 AM Jan 20, 2011
 */
package org.jbox2d.dynamics.joints;

import com.almasb.gameutils.math.Vec2;
import org.jbox2d.dynamics.Body;

/**
 * Friction joint definition.
 *
 * @author Daniel Murphy
 */
public class FrictionJointDef extends JointDef {


    /**
     * The local anchor point relative to bodyA's origin.
     */
    public final Vec2 localAnchorA;

    /**
     * The local anchor point relative to bodyB's origin.
     */
    public final Vec2 localAnchorB;

    /**
     * The maximum friction force in N.
     */
    public float maxForce;

    /**
     * The maximum friction torque in N-m.
     */
    public float maxTorque;

    public FrictionJointDef() {
        super(JointType.FRICTION);
        localAnchorA = new Vec2();
        localAnchorB = new Vec2();
        maxForce = 0f;
        maxTorque = 0f;
    }

    /**
     * Initialize the bodies, anchors, axis, and reference angle using the world anchor and world
     * axis.
     */
    public void initialize(Body bA, Body bB, Vec2 anchor) {
        bodyA = bA;
        bodyB = bB;
        bA.getLocalPointToOut(anchor, localAnchorA);
        bB.getLocalPointToOut(anchor, localAnchorB);
    }
}
