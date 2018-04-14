/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package com.almasb.fxgl.extra.entity.components;

import com.almasb.fxgl.entity.component.Component;

/**
 * Moves entity in a circle.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public final class CircularMovementComponent extends Component {

    private double radius;
    private double speed;
    private double t = 0.0;

    public CircularMovementComponent(double speed, double radius) {
        this.radius = radius;
        this.speed = speed;
    }

    @Override
    public void onUpdate(double tpf) {
        double x = entity.getX() - Math.cos(t) * radius;
        double y = entity.getY() - Math.sin(t) * radius;

        t += tpf * speed;

        entity.setX(x + Math.cos(t) * radius);
        entity.setY(y + Math.sin(t) * radius);
    }
}
