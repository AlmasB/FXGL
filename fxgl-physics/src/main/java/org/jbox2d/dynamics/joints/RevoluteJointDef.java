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
public class RevoluteJointDef extends JointDef {

    /**
     * The local anchor point relative to body1's origin.
     */
    public Vec2 localAnchorA;

    /**
     * The local anchor point relative to body2's origin.
     */
    public Vec2 localAnchorB;

    /**
     * The body2 angle minus body1 angle in the reference state (radians).
     */
    public float referenceAngle;

    /**
     * A flag to enable joint limits.
     */
    public boolean enableLimit;

    /**
     * The lower angle for the joint limit (radians).
     */
    public float lowerAngle;

    /**
     * The upper angle for the joint limit (radians).
     */
    public float upperAngle;

    /**
     * A flag to enable the joint motor.
     */
    public boolean enableMotor;

    /**
     * The desired motor speed. Usually in radians per second.
     */
    public float motorSpeed;

    /**
     * The maximum motor torque used to achieve the desired motor speed. Usually in N-m.
     */
    public float maxMotorTorque;

    public RevoluteJointDef() {
        super(JointType.REVOLUTE);
        localAnchorA = new Vec2(0.0f, 0.0f);
        localAnchorB = new Vec2(0.0f, 0.0f);
        referenceAngle = 0.0f;
        lowerAngle = 0.0f;
        upperAngle = 0.0f;
        maxMotorTorque = 0.0f;
        motorSpeed = 0.0f;
        enableLimit = false;
        enableMotor = false;
    }

    /**
     * Initialize the bodies, anchors, and reference angle using the world anchor.
     *
     * @param b1
     * @param b2
     * @param anchor
     */
    public void initialize(final Body b1, final Body b2, final Vec2 anchor) {
        bodyA = b1;
        bodyB = b2;
        bodyA.getLocalPointToOut(anchor, localAnchorA);
        bodyB.getLocalPointToOut(anchor, localAnchorB);
        referenceAngle = bodyB.getAngle() - bodyA.getAngle();
    }
}
