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

package org.jbox2d.callbacks;

import com.almasb.fxgl.core.math.Vec2;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.World;

/**
 * Callback class for ray casts.
 * See {@link World#raycast(RayCastCallback, Vec2, Vec2)}
 *
 * @author Daniel Murphy
 */
public interface RayCastCallback {

    /**
     * Called for each fixture found in the query. You control how the ray cast
     * proceeds by returning a float:
     * <ul>
     *     <li>return -1: ignore this fixture and continue</li>
     *     <li>return 0: terminate the ray cast</li>
     *     <li>return fraction: clip the ray to this point</li>
     *     <li>return 1: don't clip the ray and continue</li>
     * </ul>
     *
     * @param fixture the fixture hit by the ray
     * @param point the point of initial intersection
     * @param normal the normal vector at the point of intersection
     * @param fraction fraction
     * @return -1 to filter, 0 to terminate, fraction to clip the ray for
     * closest hit, 1 to continue
     */
    float reportFixture(Fixture fixture, Vec2 point, Vec2 normal, float fraction);
}
