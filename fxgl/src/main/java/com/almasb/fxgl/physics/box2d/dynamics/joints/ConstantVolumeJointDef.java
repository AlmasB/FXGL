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
package com.almasb.fxgl.physics.box2d.dynamics.joints;

import com.almasb.fxgl.physics.box2d.dynamics.Body;

import java.util.ArrayList;

/**
 * Definition for a {@link ConstantVolumeJoint}, which connects a group a bodies together so they
 * maintain a constant volume within them.
 */
public class ConstantVolumeJointDef extends JointDef {
    public float frequencyHz;
    public float dampingRatio;

    ArrayList<Body> bodies;
    ArrayList<DistanceJoint> joints;

    public ConstantVolumeJointDef() {
        super(JointType.CONSTANT_VOLUME);
        bodies = new ArrayList<Body>();
        joints = null;
        collideConnected = false;
        frequencyHz = 0.0f;
        dampingRatio = 0.0f;
    }

    /**
     * Adds a body to the group
     *
     * @param argBody
     */
    public void addBody(Body argBody) {
        bodies.add(argBody);
        if (bodies.size() == 1) {
            bodyA = argBody;
        }
        if (bodies.size() == 2) {
            bodyB = argBody;
        }
    }

    /**
     * Adds a body and the pre-made distance joint. Should only be used for deserialization.
     */
    public void addBodyAndJoint(Body argBody, DistanceJoint argJoint) {
        addBody(argBody);
        if (joints == null) {
            joints = new ArrayList<DistanceJoint>();
        }
        joints.add(argJoint);
    }
}
