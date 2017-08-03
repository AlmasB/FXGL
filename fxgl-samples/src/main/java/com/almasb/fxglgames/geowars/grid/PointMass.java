/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
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
