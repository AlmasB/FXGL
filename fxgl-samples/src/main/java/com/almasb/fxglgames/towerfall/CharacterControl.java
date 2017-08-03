/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxglgames.towerfall;

import com.almasb.fxgl.ecs.Control;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.ecs.GameWorld;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.PositionComponent;
import com.almasb.fxgl.physics.PhysicsComponent;
import javafx.geometry.Point2D;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class CharacterControl extends Control {

    private PositionComponent position;
    private PhysicsComponent physics;

    @Override
    public void onAdded(Entity entity) {
        position = Entities.getPosition(entity);
        physics = Entities.getPhysics(entity);
    }

    @Override
    public void onUpdate(Entity entity, double tpf) {

    }

    public void left() {
        physics.setVelocityX(-150);
    }

    public void right() {
        physics.setVelocityX(150);
    }

    public void jump() {
        physics.setVelocityY(-350);
    }

    public void stop() {
        physics.setVelocityX(physics.getVelocityX() * 0.7);
    }

    public void shoot(Point2D endPoint) {
        double x = position.getX();
        double y = position.getY();

        Point2D velocity = endPoint
                .subtract(x, y)
                .normalize()
                .multiply(500);

        ((GameWorld) getEntity().getWorld()).spawn("Arrow",
                new SpawnData(x, y)
                        .put("velocity", velocity)
                        .put("shooter", getEntity()));
    }
}
