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

package org.jbox2d.collision;

import com.almasb.fxgl.core.math.Vec2;

// updated to rev 100

/**
 * A manifold point is a contact point belonging to a contact
 * manifold. It holds details related to the geometry and dynamics
 * of the contact points.
 * The local point usage depends on the manifold type:
 * <ul><li>e_circles: the local center of circleB</li>
 * <li>e_faceA: the local center of cirlceB or the clip point of polygonB</li>
 * <li>e_faceB: the clip point of polygonA</li></ul>
 * This structure is stored across time steps, so we keep it small.<br/>
 * Note: the impulses are used for internal caching and may not
 * provide reliable contact forces, especially for high speed collisions.
 */
public class ManifoldPoint {
    /** usage depends on manifold type */
    public final Vec2 localPoint;
    /** the non-penetration impulse */
    public float normalImpulse;
    /** the friction impulse */
    public float tangentImpulse;
    /** uniquely identifies a contact point between two shapes */
    public final ContactID id;

    /**
     * Blank manifold point with everything zeroed out.
     */
    public ManifoldPoint() {
        localPoint = new Vec2();
        normalImpulse = tangentImpulse = 0f;
        id = new ContactID();
    }

    /**
     * Creates a manifold point as a copy of the given point
     * @param cp point to copy from
     */
    public ManifoldPoint(final ManifoldPoint cp) {
        localPoint = cp.localPoint.clone();
        normalImpulse = cp.normalImpulse;
        tangentImpulse = cp.tangentImpulse;
        id = new ContactID(cp.id);
    }

    /**
     * Sets this manifold point form the given one
     * @param cp the point to copy from
     */
    public void set(final ManifoldPoint cp) {
        localPoint.set(cp.localPoint);
        normalImpulse = cp.normalImpulse;
        tangentImpulse = cp.tangentImpulse;
        id.set(cp.id);
    }
}
