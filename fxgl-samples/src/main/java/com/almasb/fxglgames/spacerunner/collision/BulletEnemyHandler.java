/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxglgames.spacerunner.collision;

import com.almasb.fxgl.annotation.AddCollisionHandler;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.ecs.component.UserDataComponent;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.PositionComponent;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxglgames.spacerunner.SpaceRunnerFactory;
import com.almasb.fxglgames.spacerunner.SpaceRunnerType;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@AddCollisionHandler
public class BulletEnemyHandler extends CollisionHandler {

    public BulletEnemyHandler() {
        super(SpaceRunnerType.BULLET, SpaceRunnerType.ENEMY);
    }

    @Override
    protected void onCollisionBegin(Entity bullet, Entity enemy) {
        SpaceRunnerType ownerType = (SpaceRunnerType) bullet.getComponent(UserDataComponent.class).getValue();

        if (!Entities.getType(enemy).isType(ownerType)) {
            // TODO: refactor
//            PositionComponent position = Entities.getPosition(enemy);
//            enemy.getWorld().addEntity(FXGL.getInstance(SpaceRunnerFactory.class).newEnemy(new SpawnData(position.getX() + 500, 300)));
//
//            bullet.removeFromWorld();
//            enemy.removeFromWorld();
        }
    }
}
