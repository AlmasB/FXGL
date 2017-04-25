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

package com.almasb.fxglgames.geowars.grid;

import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.core.pool.Pools;
import com.almasb.fxgl.ecs.Entity;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class Spring {
    private final PointMass end1;
    private final PointMass end2;

    private final double lengthAtRest;

    private final float stiffness;
    private final float damping;

    public Spring(PointMass end1, PointMass end2, double stiffness, double damping, boolean visible, Entity gridEntity) {
        this.end1 = end1;
        this.end2 = end2;
        this.stiffness = (float) stiffness;
        this.damping = (float) damping / 10;
        lengthAtRest = end1.getPosition().distance(end2.getPosition().x, end2.getPosition().y) * 0.95f;

        if (visible) {
            gridEntity.getControlUnsafe(GridControl.class).addControl(new LineControl(end1, end2));
        }
    }

    public void update() {
        Vec2 current = Pools.obtain(Vec2.class)
                .set(end1.getPosition())
                .subLocal(end2.getPosition());

        float currentLength = current.length();

        if (currentLength > lengthAtRest) {
            Vec2 dv = Pools.obtain(Vec2.class)
                    .set(end2.getVelocity())
                    .subLocal(end1.getVelocity())
                    .mulLocal(damping);

            Vec2 force = current.normalizeLocal()
                    .mulLocal(currentLength - lengthAtRest)
                    .mulLocal(stiffness)
                    .subLocal(dv);

            end2.applyForce(force);
            end1.applyForce(force.negateLocal());

            Pools.free(dv);
        }

        Pools.free(current);
    }
}
