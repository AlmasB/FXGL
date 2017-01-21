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

package org.jbox2d.dynamics.joints;

import com.almasb.fxgl.core.math.Vec2;
import org.jbox2d.dynamics.Body;

//Updated to rev 56->130->142 of b2DistanceJoint.cpp/.h

/**
 * Distance joint definition. This requires defining an anchor point on both bodies and the non-zero
 * length of the distance joint. The definition uses local anchor points so that the initial
 * configuration can violate the constraint slightly. This helps when saving and loading a game.
 *
 * @warning Do not use a zero or short length.
 */
public class DistanceJointDef extends JointDef {
    /** The local anchor point relative to body1's origin. */
    public final Vec2 localAnchorA;

    /** The local anchor point relative to body2's origin. */
    public final Vec2 localAnchorB;

    /** The equilibrium length between the anchor points. */
    public float length;

    /**
     * The mass-spring-damper frequency in Hertz.
     */
    public float frequencyHz;

    /**
     * The damping ratio. 0 = no damping, 1 = critical damping.
     */
    public float dampingRatio;

    public DistanceJointDef() {
        super(JointType.DISTANCE);
        localAnchorA = new Vec2(0.0f, 0.0f);
        localAnchorB = new Vec2(0.0f, 0.0f);
        length = 1.0f;
        frequencyHz = 0.0f;
        dampingRatio = 0.0f;
    }

    /**
     * Initialize the bodies, anchors, and length using the world anchors.
     *
     * @param b1 First body
     * @param b2 Second body
     * @param anchor1 World anchor on first body
     * @param anchor2 World anchor on second body
     */
    public void initialize(final Body b1, final Body b2, final Vec2 anchor1, final Vec2 anchor2) {
        bodyA = b1;
        bodyB = b2;
        localAnchorA.set(bodyA.getLocalPoint(anchor1));
        localAnchorB.set(bodyB.getLocalPoint(anchor2));
        Vec2 d = anchor2.sub(anchor1);
        length = d.length();
    }
}
