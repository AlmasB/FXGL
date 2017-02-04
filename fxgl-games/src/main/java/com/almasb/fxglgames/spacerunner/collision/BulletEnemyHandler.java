/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
        SpaceRunnerType ownerType = (SpaceRunnerType) bullet.getComponentUnsafe(UserDataComponent.class).getValue();

        if (!Entities.getType(enemy).isType(ownerType)) {
            PositionComponent position = Entities.getPosition(enemy);
            enemy.getWorld().addEntity(FXGL.getInstance(SpaceRunnerFactory.class).newEnemy(new SpawnData(position.getX() + 500, 300)));

            bullet.removeFromWorld();
            enemy.removeFromWorld();
        }
    }
}
