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

package com.almasb.fxgl.physics

import com.almasb.fxgl.core.math.Vec2
import com.almasb.fxgl.ecs.Entity
import org.jbox2d.callbacks.RayCastCallback
import org.jbox2d.dynamics.Fixture

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class EdgeCallback : RayCastCallback {

    var fixture: Fixture? = null
        private set

    var point: Vec2? = null
        private set

    var bestFraction = 1.0f
        private set

    override fun reportFixture(fixture: Fixture, point: Vec2, normal: Vec2?, fraction: Float): Float {
        val e = fixture.getBody().userData as Entity
        if (e.getComponentUnsafe(PhysicsComponent::class.java).isRaycastIgnored)
            return 1.0f

        if (fraction < bestFraction) {
            this.fixture = fixture
            this.point = point.clone()
            bestFraction = fraction
        }

        return bestFraction
    }

    fun reset() {
        fixture = null
        point = null
        bestFraction = 1.0f
    }
}