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
 * Created at 12:11:41 PM Jan 23, 2011
 */
package org.jbox2d.dynamics.joints;

import com.almasb.fxgl.core.math.Vec2;
import org.jbox2d.common.JBoxSettings;
import org.jbox2d.dynamics.Body;

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
        assert (ratio > JBoxSettings.EPSILON);
    }
}
