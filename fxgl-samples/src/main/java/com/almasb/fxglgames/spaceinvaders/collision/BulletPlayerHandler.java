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
import com.almasb.fxglgames.spaceinvaders.SpaceInvadersType;
import com.almasb.fxglgames.spaceinvaders.component.InvincibleComponent;
import com.almasb.fxglgames.spaceinvaders.component.OwnerComponent;
import com.almasb.fxglgames.spaceinvaders.event.GameEvent;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
@AddCollisionHandler
public class BulletPlayerHandler extends CollisionHandler {

    public BulletPlayerHandler() {
        super(SpaceInvadersType.BULLET, SpaceInvadersType.PLAYER);
    }

    @Override
    protected void onCollisionBegin(Entity bullet, Entity player) {
        Object owner = bullet.getComponent(OwnerComponent.class).getValue();

        // player shot that bullet so no need to handle collision
        if (owner == SpaceInvadersType.PLAYER
                || player.getComponent(InvincibleComponent.class).getValue()) {
            return;
        }

        bullet.removeFromWorld();

        FXGL.getEventBus().fireEvent(new GameEvent(GameEvent.PLAYER_GOT_HIT));
    }
}
