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

import org.jbox2d.dynamics.Body;

/**
 * Joint definitions are used to construct joints.
 * @author Daniel Murphy
 */
public class JointDef {

    public JointDef(JointType type) {
        this.type = type;
        userData = null;
        bodyA = null;
        bodyB = null;
        collideConnected = false;
    }

    /**
     * The joint type is set automatically for concrete joint types.
     */
    public JointType type;

    /**
     * Use this to attach application specific data to your joints.
     */
    public Object userData;

    /**
     * The first attached body.
     */
    public Body bodyA;

    /**
     * The second attached body.
     */
    public Body bodyB;

    /**
     * Set this flag to true if the attached bodies should collide.
     */
    public boolean collideConnected;
}
