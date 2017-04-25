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

package com.almasb.fxglgames.geowars;

import com.almasb.fxgl.annotation.SetEntityFactory;
import com.almasb.fxgl.annotation.Spawns;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.CollidableComponent;
import com.almasb.fxgl.entity.control.ExpireCleanControl;
import com.almasb.fxgl.entity.control.OffscreenCleanControl;
import com.almasb.fxgl.entity.control.ProjectileControl;
import com.almasb.fxglgames.geowars.component.HPComponent;
import com.almasb.fxglgames.geowars.component.OldPositionComponent;
import com.almasb.fxglgames.geowars.control.*;
import javafx.geometry.Point2D;
import javafx.util.Duration;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@SetEntityFactory
public class GeoWarsFactory implements EntityFactory {

    private GeoWarsConfig config;

    public GeoWarsFactory() {
        try {
            config = FXGL.getAssetLoader().loadKV("config.kv").to(GeoWarsConfig.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to parse KV file: " + e);
        }
    }

    private static final int SPAWN_DISTANCE = 50;

    /**
     * These correspond to top-left, top-right, bottom-right, bottom-left.
     */
    private Point2D[] spawnPoints = new Point2D[] {
            new Point2D(SPAWN_DISTANCE, SPAWN_DISTANCE),
            new Point2D(FXGL.getApp().getWidth() - SPAWN_DISTANCE, SPAWN_DISTANCE),
            new Point2D(FXGL.getApp().getWidth() - SPAWN_DISTANCE, FXGL.getApp().getHeight() - SPAWN_DISTANCE),
            new Point2D(SPAWN_DISTANCE, FXGL.getApp().getHeight() - SPAWN_DISTANCE)
    };

    private Point2D getRandomSpawnPoint() {
        return spawnPoints[FXGLMath.random(3)];
    }

    @Spawns("Player")
    public GameEntity spawnPlayer(SpawnData data) {
        // TODO: move this to proper PlayerControl
        OldPositionComponent oldPosition = new OldPositionComponent();
        oldPosition.valueProperty().addListener((obs, old, newPos) -> {
            Entities.getRotation(oldPosition.getEntity()).rotateToVector(newPos.subtract(old));
        });

        return Entities.builder()
                .type(GeoWarsType.PLAYER)
                .at(FXGL.getApp().getWidth() / 2, FXGL.getApp().getHeight() / 2)
                .viewFromTextureWithBBox("Player.png")
                .with(new CollidableComponent(true), oldPosition)
                .with(new PlayerControl(), new KeepOnScreenControl(true, true))
                .build();
    }

    @Spawns("Bullet")
    public GameEntity spawnBullet(SpawnData data) {
        FXGL.getAudioPlayer().playSound("shoot" + (int) (Math.random() * 8 + 1) + ".wav");

        return Entities.builder()
                .type(GeoWarsType.BULLET)
                .from(data)
                .viewFromTextureWithBBox("Bullet.png")
                .with(new CollidableComponent(true))
                .with(new ProjectileControl(data.get("direction"), 600),
                        new BulletControl(FXGL.<GeoWarsApp>getAppCast().getGrid()),
                        new OffscreenCleanControl())
                .build();
    }

    @Spawns("Wanderer")
    public GameEntity spawnWanderer(SpawnData data) {
        boolean red = FXGLMath.randomBoolean((float)config.getRedEnemyChance());

        int moveSpeed = red ? config.getRedEnemyMoveSpeed()
                : FXGLMath.random(100, config.getWandererMaxMoveSpeed());

        return Entities.builder()
                .type(GeoWarsType.WANDERER)
                .at(getRandomSpawnPoint())
                .viewFromTextureWithBBox(red ? "RedWanderer.png" : "Wanderer.png")
                .with(new HPComponent(red ? config.getRedEnemyHealth() : config.getEnemyHealth()),
                        new CollidableComponent(true))
                .with(new WandererControl(moveSpeed))
                .build();
    }

    @Spawns("Seeker")
    public GameEntity spawnSeeker(SpawnData data) {
        boolean red = FXGLMath.randomBoolean((float)config.getRedEnemyChance());

        int moveSpeed = red ? config.getRedEnemyMoveSpeed()
                : FXGLMath.random(150, config.getSeekerMaxMoveSpeed());

        return Entities.builder()
                .type(GeoWarsType.SEEKER)
                .at(getRandomSpawnPoint())
                .viewFromTextureWithBBox(red ? "RedSeeker.png" : "Seeker.png")
                .with(new HPComponent(red ? config.getRedEnemyHealth() : config.getEnemyHealth()),
                        new CollidableComponent(true))
                .with(new SeekerControl(FXGL.<GeoWarsApp>getAppCast().getPlayer(), moveSpeed))
                .build();
    }

    @Spawns("Explosion")
    public GameEntity spawnExplosion(SpawnData data) {
        FXGL.getAudioPlayer().playSound("explosion-0" + (int) (Math.random() * 8 + 1) + ".wav");

        return Entities.builder()
                .at(data.getX() - 40, data.getY() - 40)
                .viewFromNode(FXGL.getAssetLoader().loadTexture("explosion.png", 80 * 48, 80).toAnimatedTexture(48, Duration.seconds(2)))
                .with(new ExpireCleanControl(Duration.seconds(1.8)))
                .build();
    }

    @Spawns("Crystal")
    public GameEntity spawnCrystal(SpawnData data) {
        return Entities.builder()
                .type(GeoWarsType.CRYSTAL)
                .from(data)
                .viewFromNodeWithBBox(FXGL.getAssetLoader().loadTexture("YellowCrystal.png").toAnimatedTexture(8, Duration.seconds(1)))
                .with(new CollidableComponent(true))
                .with(new ExpireCleanControl(Duration.seconds(10)))
                .build();
    }
}
