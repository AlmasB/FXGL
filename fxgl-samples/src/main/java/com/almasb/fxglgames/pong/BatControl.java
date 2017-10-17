/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxglgames.pong;

import com.almasb.fxgl.entity.Control;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.BoundingBoxComponent;
import com.almasb.fxgl.entity.component.PositionComponent;
import com.almasb.fxgl.physics.PhysicsComponent;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class BatControl extends Control {

    protected PositionComponent position;
    protected PhysicsComponent bat;
    protected BoundingBoxComponent bbox;

    @Override
    public void onAdded(Entity entity) {
        bat = entity.getComponent(PhysicsComponent.class);
        position = entity.getComponent(PositionComponent.class);
        bbox = entity.getComponent(BoundingBoxComponent.class);
    }

    @Override
    public void onUpdate(Entity entity, double tpf) {}

    public void up() {
        if (position.getY() >= 5)
            bat.setLinearVelocity(0, -5 * 60);
        else
            stop();
    }

    public void down() {
        if (bbox.getMaxYWorld() <= 600 - 5)
            bat.setLinearVelocity(0, 5 * 60);
        else
            stop();
    }

    public void stop() {
        bat.setLinearVelocity(0, 0);
    }
}
