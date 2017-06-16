/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxglgames.towerfall.collision;

import com.almasb.fxgl.annotation.AddCollisionHandler;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.ecs.component.UserDataComponent;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxglgames.towerfall.EntityType;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@AddCollisionHandler
public class ArrowEnemyHandler extends CollisionHandler {

    public ArrowEnemyHandler() {
        super(EntityType.ARROW, EntityType.ENEMY);
    }

    @Override
    protected void onCollisionBegin(Entity arrow, Entity enemy) {
        if (arrow.getComponent(UserDataComponent.class).getValue() == enemy)
            return;

        arrow.removeFromWorld();
        enemy.removeFromWorld();
        FXGL.getApp().getGameState().increment("enemiesKilled", +1);

        FXGL.getApp().getGameWorld().spawn("Enemy", 27 * 40, 6 * 40);
    }
}
