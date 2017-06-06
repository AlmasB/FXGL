/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxglgames.spaceinvaders.control;

import com.almasb.fxgl.ecs.AbstractControl;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.ecs.component.Required;
import com.almasb.fxgl.entity.component.PositionComponent;
import com.almasb.fxglgames.spaceinvaders.SpaceInvadersType;
import com.almasb.fxglgames.spaceinvaders.component.OwnerComponent;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
@Required(OwnerComponent.class)
public class BulletControl extends AbstractControl {

    private PositionComponent position;
    private OwnerComponent owner;

    private double speed;

    public BulletControl(double speed) {
        this.speed = speed;
    }

    @Override
    public void onAdded(Entity entity) {
        owner = entity.getComponentUnsafe(OwnerComponent.class);
        position = entity.getComponentUnsafe(PositionComponent.class);
    }

    @Override
    public void onUpdate(Entity entity, double tpf) {
        position.translateY(owner.getValue() == (SpaceInvadersType.PLAYER) ? -tpf * speed : tpf * speed);
    }
}
