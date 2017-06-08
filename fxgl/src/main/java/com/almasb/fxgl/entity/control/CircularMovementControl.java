/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package com.almasb.fxgl.entity.control;

import com.almasb.fxgl.ecs.Control;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.ecs.component.Required;
import com.almasb.fxgl.entity.component.PositionComponent;

/**
 * Control that moves entity in a circle.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
@Required(PositionComponent.class)
public final class CircularMovementControl extends Control {

    private double radius;
    private double speed;
    private double t = 0.0;

    private PositionComponent position;

    public CircularMovementControl(double speed, double radius) {
        this.radius = radius;
        this.speed = speed;
    }

    @Override
    public void onAdded(Entity entity) {
        position = entity.getComponentUnsafe(PositionComponent.class);
    }

    @Override
    public void onUpdate(Entity entity, double tpf) {
        double x = position.getX() - Math.cos(t) * radius;
        double y = position.getY() - Math.sin(t) * radius;

        t += tpf * speed;

        position.setX(x + Math.cos(t) * radius);
        position.setY(y + Math.sin(t) * radius);
    }
}
