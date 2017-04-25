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

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class PointMass {

    private Vec2 position;
    private Vec2 velocity = new Vec2();
    private Vec2 acceleration = new Vec2();

    private final float initialDamping;
    private float damping;
    private float inverseMass;

    public PointMass(Vec2 position, double damping, double inverseMass) {
        this.position = position;
        this.damping = (float) damping;
        this.initialDamping = (float) damping;
        this.inverseMass = (float) inverseMass;
    }

    public void applyForce(Vec2 force) {
        acceleration.addLocal(force.mul(inverseMass));
    }

    public void increaseDamping(double factor) {
        damping *= factor;
    }

    public void update() {
        applyAcceleration();
        applyVelocity();

        damping = initialDamping;
    }

    public Vec2 getPosition() {
        return position;
    }

    public Vec2 getVelocity() {
        return velocity;
    }

    private void applyAcceleration() {
        velocity = velocity.add(acceleration);
        acceleration.setZero();
    }

    private void applyVelocity() {
        position.addLocal(velocity.mul(0.6f));

        if (velocity.lengthSquared() < 0.0001) {
            velocity.setZero();
        }

        velocity.mulLocal(damping);
    }
}
