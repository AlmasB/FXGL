/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package com.almasb.fxgl.extra.entity.controls;

import com.almasb.fxgl.entity.Component;
import com.almasb.fxgl.entity.Entity;

/**
 * Control that moves entity in a circle.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public final class CircularMovementControl extends Component {

    private double radius;
    private double speed;
    private double t = 0.0;

    public CircularMovementControl(double speed, double radius) {
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
