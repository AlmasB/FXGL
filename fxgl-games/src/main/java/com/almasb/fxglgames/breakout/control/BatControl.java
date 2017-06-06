/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxglgames.breakout.control;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.ecs.AbstractControl;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.physics.PhysicsComponent;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class BatControl extends AbstractControl {

    private static final float BOUNCE_FACTOR = 1.5f;
    private static final float SPEED_DECAY = 0.66f;

    private GameEntity bat;
    private PhysicsComponent physics;
    private float speed = 0;

    private Vec2 velocity = new Vec2();

    @Override
    public void onAdded(Entity entity) {
        bat = (GameEntity) entity;
        physics = Entities.getPhysics(entity);
    }

    @Override
    public void onUpdate(Entity entity, double tpf) {
        speed = 600 * (float)tpf;

        velocity.mulLocal(SPEED_DECAY);

        if (bat.getX() < 0) {
            velocity.set(BOUNCE_FACTOR * (float) -bat.getX(), 0);
        } else if (bat.getRightX() > FXGL.getApp().getWidth()) {
            velocity.set(BOUNCE_FACTOR * (float) -(bat.getRightX() - FXGL.getApp().getWidth()), 0);
        }

        physics.setBodyLinearVelocity(velocity);
    }

    public void left() {
        velocity.set(-speed, 0);
    }

    public void right() {
        velocity.set(speed, 0);
    }
}
