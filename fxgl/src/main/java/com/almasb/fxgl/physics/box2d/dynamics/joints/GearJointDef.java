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
 * Created at 5:20:39 AM Jan 22, 2011
 */
package com.almasb.fxgl.physics.box2d.dynamics.joints;

/**
 * Gear joint definition. This definition requires two existing revolute or prismatic joints (any
 * combination will work). The provided joints must attach a dynamic body to a static body.
 *
 * @author Daniel Murphy
 */
public class GearJointDef extends JointDef {
    /**
     * The first revolute/prismatic joint attached to the gear joint.
     */
    public Joint joint1;

    /**
     * The second revolute/prismatic joint attached to the gear joint.
     */
    public Joint joint2;

    /**
     * Gear ratio.
     *
     * @see GearJoint
     */
    public float ratio;

    public GearJointDef() {
        super(JointType.GEAR);
        joint1 = null;
        joint2 = null;
    }
}
