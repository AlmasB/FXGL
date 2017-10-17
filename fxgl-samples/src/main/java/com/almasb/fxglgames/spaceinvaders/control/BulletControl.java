/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxglgames.spaceinvaders.control;

import com.almasb.fxgl.entity.Control;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Required;
import com.almasb.fxgl.entity.component.PositionComponent;
import com.almasb.fxglgames.spaceinvaders.SpaceInvadersType;
import com.almasb.fxglgames.spaceinvaders.component.OwnerComponent;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
@Required(OwnerComponent.class)
public class BulletControl extends Control {

    private PositionComponent position;
    private OwnerComponent owner;

    private double speed;

    public BulletControl(double speed) {
        this.speed = speed;
    }

    @Override
    public void onAdded(Entity entity) {
        owner = entity.getComponent(OwnerComponent.class);
        position = entity.getComponent(PositionComponent.class);
    }

    @Override
    public void onUpdate(Entity entity, double tpf) {
        position.translateY(owner.getValue() == (SpaceInvadersType.PLAYER) ? -tpf * speed : tpf * speed);
    }
}
