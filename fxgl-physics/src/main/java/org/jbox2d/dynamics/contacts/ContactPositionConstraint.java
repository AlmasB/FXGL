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
package org.jbox2d.dynamics.contacts;

import com.almasb.fxgl.core.math.Vec2;
import org.jbox2d.collision.Manifold.ManifoldType;
import org.jbox2d.common.JBoxSettings;

public class ContactPositionConstraint {
    Vec2[] localPoints = new Vec2[JBoxSettings.maxManifoldPoints];
    final Vec2 localNormal = new Vec2();
    final Vec2 localPoint = new Vec2();
    int indexA;
    int indexB;
    float invMassA, invMassB;
    final Vec2 localCenterA = new Vec2();
    final Vec2 localCenterB = new Vec2();
    float invIA, invIB;
    ManifoldType type;
    float radiusA, radiusB;
    int pointCount;

    public ContactPositionConstraint() {
        for (int i = 0; i < localPoints.length; i++) {
            localPoints[i] = new Vec2();
        }
    }
}
