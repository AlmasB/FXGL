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

package com.almasb.fxglgames.spacerunner;

import com.almasb.fxgl.annotation.SetEntityFactory;
import com.almasb.fxgl.annotation.Spawns;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.ecs.component.UserDataComponent;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.CollidableComponent;
import com.almasb.fxgl.entity.control.OffscreenCleanControl;
import com.almasb.fxgl.entity.control.ProjectileControl;
import com.almasb.fxglgames.spacerunner.control.EnemyControl;
import com.almasb.fxglgames.spacerunner.control.KeepOnScreenControl;
import com.almasb.fxglgames.spacerunner.control.PlayerControl;
import com.google.inject.Singleton;
import javafx.geometry.Point2D;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@SetEntityFactory
@Singleton
public class SpaceRunnerFactory implements EntityFactory {

    @Spawns("Player")
    public GameEntity newPlayer(SpawnData data) {
        return Entities.builder()
                .type(SpaceRunnerType.PLAYER)
                .at(data.getX(), data.getY())
                .viewFromNodeWithBBox(FXGL.getAssetLoader().loadTexture("spacerunner/sprite_player.png", 40, 40))
                .with(new CollidableComponent(true))
                .with(new PlayerControl(), new KeepOnScreenControl(false, true))
                .build();
    }

    public GameEntity newBullet(double x, double y, SpaceRunnerType ownerType) {
        return Entities.builder()
                .type(SpaceRunnerType.BULLET)
                .at(x, y - 5.5)
                .viewFromNodeWithBBox(FXGL.getAssetLoader().loadTexture("spacerunner/sprite_bullet.png", 22, 11))
                .with(new CollidableComponent(true), new UserDataComponent(ownerType))
                .with(new ProjectileControl(new Point2D(ownerType == SpaceRunnerType.PLAYER ? 1 : -1, 0), 250),
                        new OffscreenCleanControl())
                .build();
    }

    @Spawns("Enemy1")
    public GameEntity newEnemy(SpawnData data) {
        return Entities.builder()
                .type(SpaceRunnerType.ENEMY)
                .at(data.getX(), data.getY())
                .viewFromNodeWithBBox(FXGL.getAssetLoader().loadTexture("spacerunner/sprite_enemy_1.png", 27, 33))
                .with(new CollidableComponent(true))
                .with(new EnemyControl())
                .build();
    }
}
