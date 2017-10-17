/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxglgames.spaceinvaders.collision;

import com.almasb.fxgl.annotation.AddCollisionHandler;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxglgames.spaceinvaders.BonusType;
import com.almasb.fxglgames.spaceinvaders.SpaceInvadersType;
import com.almasb.fxglgames.spaceinvaders.component.SubTypeComponent;
import com.almasb.fxglgames.spaceinvaders.event.BonusPickupEvent;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
@AddCollisionHandler
public class BonusPlayerHandler extends CollisionHandler {

    public BonusPlayerHandler() {
        super(SpaceInvadersType.BONUS, SpaceInvadersType.PLAYER);
    }

    @Override
    protected void onCollisionBegin(Entity bonus, Entity player) {
        BonusType type = (BonusType) bonus.getComponent(SubTypeComponent.class).getValue();
        bonus.removeFromWorld();

        FXGL.getEventBus().fireEvent(new BonusPickupEvent(BonusPickupEvent.ANY, type));
    }
}
