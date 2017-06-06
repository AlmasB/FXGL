/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxglgames.spaceinvaders.control;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.ecs.AbstractControl;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.entity.component.PositionComponent;
import com.almasb.fxgl.entity.component.RotationComponent;
import javafx.geometry.Point2D;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class MeteorControl extends AbstractControl {

    private RotationComponent rotation;
    private PositionComponent position;

    private Point2D velocity;

    @Override
    public void onAdded(Entity entity) {
        rotation = entity.getComponentUnsafe(RotationComponent.class);
        position = entity.getComponentUnsafe(PositionComponent.class);

        double w = FXGL.getSettings().getWidth();
        double h = FXGL.getSettings().getHeight();

        velocity = new Point2D(position.getX() < w / 2 ? 1 : -1, position.getY() < h / 2 ? 1 : -1)
            .normalize().multiply(Math.random() * 5 + 50);
    }

    @Override
    public void onUpdate(Entity entity, double tpf) {
        rotation.rotateBy(tpf * 10);

        position.translate(velocity.multiply(tpf));
    }
}
