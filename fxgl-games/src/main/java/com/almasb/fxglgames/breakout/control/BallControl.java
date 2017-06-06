/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxglgames.breakout.control;

import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.ecs.AbstractControl;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.physics.PhysicsComponent;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class BallControl extends AbstractControl {

    private PhysicsComponent physics;

//    @Override
//    public void onAdded(Entity entity) {
//        physics = entity.getComponentUnsafe(PhysicsComponent.class);
//    }

    @Override
    public void onUpdate(Entity entity, double tpf) {
        limitVelocity();
    }

    private void limitVelocity() {
        if (Math.abs(physics.getLinearVelocity().getX()) < 5 * 60) {
            physics.setLinearVelocity(Math.signum(physics.getLinearVelocity().getX()) * 5 * 60,
                    physics.getLinearVelocity().getY());
        }

        if (Math.abs(physics.getLinearVelocity().getY()) < 5 * 60) {
            physics.setLinearVelocity(physics.getLinearVelocity().getX(),
                    Math.signum(physics.getLinearVelocity().getY()) * 5 * 60);
        }
    }

    public void release() {
        physics.setBodyLinearVelocity(new Vec2(5, 5));
    }
}
