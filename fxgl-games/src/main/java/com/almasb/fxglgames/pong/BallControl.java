/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxglgames.pong;

import com.almasb.fxgl.ecs.AbstractControl;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.physics.PhysicsComponent;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class BallControl extends AbstractControl {

    private PhysicsComponent ball;

    @Override
    public void onAdded(Entity entity) {
        ball = entity.getComponentUnsafe(PhysicsComponent.class);
    }

    @Override
    public void onUpdate(Entity entity, double tpf) {
        limitVelocity();
    }

    private void limitVelocity() {
        if (Math.abs(ball.getLinearVelocity().getX()) < 5 * 60) {
            ball.setLinearVelocity(Math.signum(ball.getLinearVelocity().getX()) * 5 * 60,
                    ball.getLinearVelocity().getY());
        }

        if (Math.abs(ball.getLinearVelocity().getY()) > 5 * 60 * 2) {
            ball.setLinearVelocity(ball.getLinearVelocity().getX(),
                    Math.signum(ball.getLinearVelocity().getY()) * 5 * 60);
        }
    }
}
