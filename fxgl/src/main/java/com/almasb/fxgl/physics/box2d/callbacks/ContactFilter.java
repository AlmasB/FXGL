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

package com.almasb.fxgl.physics.box2d.callbacks;

import com.almasb.fxgl.physics.box2d.dynamics.Filter;
import com.almasb.fxgl.physics.box2d.dynamics.Fixture;

/**
 * Implement this class to provide collision filtering. In other words, you can implement
 * this class if you want finer control over contact creation.
 * @author Daniel Murphy
 */
public class ContactFilter {

    /**
     * @warning for performance reasons this is only called when the AABBs begin to overlap.
     * @param fixtureA first fixture
     * @param fixtureB second fixture
     * @return true if contact calculations should be performed between these two shapes
     */
    public boolean shouldCollide(Fixture fixtureA, Fixture fixtureB) {
        Filter filterA = fixtureA.getFilterData();
        Filter filterB = fixtureB.getFilterData();

        if (filterA.groupIndex == filterB.groupIndex && filterA.groupIndex != 0) {
            return filterA.groupIndex > 0;
        }

        return (filterA.maskBits & filterB.categoryBits) != 0 &&
                (filterA.categoryBits & filterB.maskBits) != 0;
    }
}
