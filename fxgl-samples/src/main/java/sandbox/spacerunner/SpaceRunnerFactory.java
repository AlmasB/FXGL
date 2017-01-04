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

package sandbox.spacerunner;

import com.almasb.ents.component.UserDataComponent;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.entity.component.CollidableComponent;
import com.almasb.fxgl.entity.control.OffscreenCleanControl;
import com.almasb.fxgl.entity.control.ProjectileControl;
import sandbox.spacerunner.control.EnemyControl;
import sandbox.spacerunner.control.KeepOnScreenControl;
import sandbox.spacerunner.control.PlayerControl;
import com.google.inject.Singleton;
import javafx.geometry.Point2D;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@Singleton
public class SpaceRunnerFactory {

    public GameEntity newPlayer(double x, double y) {
        return Entities.builder()
                .type(EntityType.PLAYER)
                .at(x, y)
                .viewFromNodeWithBBox(FXGL.getAssetLoader().loadTexture("sprite_player.png", 40, 40))
                .with(new CollidableComponent(true))
                .with(new PlayerControl(), new KeepOnScreenControl(false, true))
                .build();
    }

    public GameEntity newBullet(double x, double y, EntityType ownerType) {
        return Entities.builder()
                .type(EntityType.BULLET)
                .at(x, y - 5.5)
                .viewFromNodeWithBBox(FXGL.getAssetLoader().loadTexture("sprite_bullet.png", 22, 11))
                .with(new CollidableComponent(true), new UserDataComponent(ownerType))
                .with(new ProjectileControl(new Point2D(ownerType == EntityType.PLAYER ? 1 : -1, 0), 250),
                        new OffscreenCleanControl())
                .build();
    }

    public GameEntity newEnemy(double x, double y) {
        return Entities.builder()
                .type(EntityType.ENEMY)
                .at(x, y)
                .viewFromNodeWithBBox(FXGL.getAssetLoader().loadTexture("sprite_enemy_1.png", 27, 33))
                .with(new CollidableComponent(true))
                .with(new EnemyControl())
                .build();
    }
}
