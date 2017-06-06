/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxglgames.spaceinvaders.collision;

import com.almasb.fxgl.annotation.AddCollisionHandler;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxglgames.spaceinvaders.SpaceInvadersType;
import com.almasb.fxglgames.spaceinvaders.component.HPComponent;
import com.almasb.fxglgames.spaceinvaders.component.OwnerComponent;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
@AddCollisionHandler
public class BulletWallHandler extends CollisionHandler {

    public BulletWallHandler() {
        super(SpaceInvadersType.BULLET, SpaceInvadersType.WALL);
    }

    @Override
    protected void onCollisionBegin(Entity bullet, Entity wall) {
        Object owner = bullet.getComponentUnsafe(OwnerComponent.class).getValue();

        if (owner == SpaceInvadersType.ENEMY) {
            bullet.removeFromWorld();

            HPComponent hp = wall.getComponentUnsafe(HPComponent.class);
            hp.setValue(hp.getValue() - 1);
            if (hp.getValue() == 0)
                wall.removeFromWorld();
        }
    }
}
