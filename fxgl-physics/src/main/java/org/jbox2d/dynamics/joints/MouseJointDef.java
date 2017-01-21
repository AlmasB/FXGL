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
package org.jbox2d.dynamics.joints;

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
