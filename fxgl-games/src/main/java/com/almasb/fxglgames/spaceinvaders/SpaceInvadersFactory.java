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

package com.almasb.fxglgames.spaceinvaders;

import com.almasb.fxgl.annotation.SetEntityFactory;
import com.almasb.fxgl.annotation.Spawns;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.entity.*;
import com.almasb.fxgl.entity.component.CollidableComponent;
import com.almasb.fxgl.entity.control.ExpireCleanControl;
import com.almasb.fxgl.entity.control.OffscreenCleanControl;
import com.almasb.fxgl.entity.control.ProjectileControl;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.service.AssetLoader;
import com.almasb.fxgl.texture.Texture;
import com.almasb.fxglgames.spaceinvaders.component.HPComponent;
import com.almasb.fxglgames.spaceinvaders.component.InvincibleComponent;
import com.almasb.fxglgames.spaceinvaders.component.OwnerComponent;
import com.almasb.fxglgames.spaceinvaders.component.SubTypeComponent;
import com.almasb.fxglgames.spaceinvaders.control.*;
import com.google.inject.Singleton;
import javafx.geometry.Point2D;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

import java.util.Random;

import static com.almasb.fxglgames.spaceinvaders.Config.LEVEL_START_DELAY;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
@SetEntityFactory
@Singleton
public final class SpaceInvadersFactory implements EntityFactory {

    private static final AssetLoader assetLoader = FXGL.getAssetLoader();

    private static final Random random = new Random();

    @Spawns("Background")
    public Entity newBackground(SpawnData data) {
        return Entities.builder()
                .viewFromNode(assetLoader.loadTexture("background/background.png", Config.WIDTH, Config.HEIGHT))
                .renderLayer(RenderLayer.BACKGROUND)
                .build();
    }

    @Spawns("Meteor")
    public Entity newMeteor(SpawnData data) {
        double w = FXGL.getSettings().getWidth();
        double h = FXGL.getSettings().getHeight();
        double x = 0, y = 0;

        // these are deliberately arbitrary to create illusion of randomness
        if (random.nextBoolean()) {
            // left or right
            if (random.nextBoolean()) {
                x = -50;
            } else {
                x = w + 50;
            }

            y = random.nextInt((int)h);
        } else {
            // top or bot
            if (random.nextBoolean()) {
                y = -50;
            } else {
                y = h + 50;
            }

            x = random.nextInt((int) w);
        }

        GameEntity meteor = new GameEntity();
        meteor.getPositionComponent().setValue(x, y);

        String textureName = "background/meteor" + (random.nextInt(4) + 1) + ".png";

        meteor.getViewComponent().setTexture(textureName);
        meteor.getViewComponent().setRenderLayer(new RenderLayer() {
            @Override
            public String name() {
                return "METEORS";
            }

            @Override
            public int index() {
                return 1001;
            }
        });

        meteor.addControl(new MeteorControl());

        // add offscreen clean a bit later so that they are not cleaned from start
        FXGL.getMasterTimer()
                .runOnceAfter(() -> {
                    meteor.addControl(new OffscreenCleanControl());
                }, Duration.seconds(5));

        return meteor;
    }

    @Spawns("Player")
    public GameEntity newPlayer(SpawnData data) {
        Texture texture = assetLoader.loadTexture("player2.png");
        texture.setPreserveRatio(true);
        texture.setFitHeight(40);

        return Entities.builder()
                .from(data)
                .type(SpaceInvadersType.PLAYER)
                .viewFromNodeWithBBox(texture)
                .with(new CollidableComponent(true), new InvincibleComponent())
                .with(new PlayerControl())
                .build();
    }

    @Spawns("Enemy")
    public Entity newEnemy(SpawnData data) {
        return Entities.builder()
                .from(data)
                .type(SpaceInvadersType.ENEMY)
                .viewFromNodeWithBBox(assetLoader
                        .loadTexture("enemy" + ((int)(Math.random() * 3) + 1) + ".png")
                        .toAnimatedTexture(2, Duration.seconds(2)))
                .with(new CollidableComponent(true), new HPComponent(2))
                .with(new EnemyControl())
                .build();
    }

    @Spawns("Bullet")
    public Entity newBullet(SpawnData data) {
        GameEntity owner = data.get("owner");

        GameEntity bullet = new GameEntity();
        bullet.getTypeComponent().setValue(SpaceInvadersType.BULLET);

        Point2D center = Entities.getBBox(owner)
                .getCenterWorld()
                .add(-8, 20 * (owner.isType(SpaceInvadersType.PLAYER) ? -1 : 1));

        bullet.getPositionComponent().setValue(center);

        bullet.addComponent(new CollidableComponent(true));
        bullet.getViewComponent().setView(new EntityView(assetLoader.loadTexture("tank_bullet.png")), true);
        bullet.addControl(new ProjectileControl(new Point2D(0, owner.isType(SpaceInvadersType.PLAYER) ? -1 : 1), 10 * 60));
        bullet.addComponent(new OwnerComponent(Entities.getType(owner).getValue()));
        bullet.addControl(new OffscreenCleanControl());

        bullet.setProperty("dead", false);

        return bullet;
    }

    @Spawns("Laser")
    public Entity newLaser(SpawnData data) {
        GameEntity owner = data.get("owner");

        GameEntity bullet = new GameEntity();
        bullet.getTypeComponent().setValue(SpaceInvadersType.BULLET);

        Point2D center = Entities.getBBox(owner)
                .getCenterWorld()
                .add(-4.5, -20);

        bullet.getPositionComponent().setValue(center);

        bullet.getBoundingBoxComponent().addHitBox(new HitBox("HIT", BoundingShape.box(9, 20)));
        bullet.addComponent(new CollidableComponent(true));
        bullet.addComponent(new OwnerComponent(Entities.getType(owner).getValue()));
        bullet.addControl(new OffscreenCleanControl());
        bullet.addControl(new BulletControl(500));

        DropShadow shadow = new DropShadow(22, Color.DARKBLUE);
        shadow.setInput(new Glow(0.8));

        EntityView view = new EntityView();
        view.addNode(assetLoader.loadTexture("laser1.png"));

        Texture t = assetLoader.loadTexture("laser2.png");
        t.relocate(-2, -20);

        view.addNode(t);
        view.setEffect(shadow);

        bullet.getViewComponent().setView(view);

        return bullet;
    }

    @Spawns("LaserHit")
    public Entity newLaserHit(SpawnData data) {
        return Entities.builder()
                .at(data.getX() - 15, data.getY() - 15)
                .viewFromNode(assetLoader.loadTexture("laser_hit.png", 15, 15))
                .with(new LaserHitControl())
                .build();
    }

    @Spawns("Wall")
    public Entity newWall(SpawnData data) {
        return Entities.builder()
                .from(data)
                .type(SpaceInvadersType.WALL)
                .viewFromTextureWithBBox("wall.png")
                .with(new CollidableComponent(true), new HPComponent(7))
                .build();
    }

    @Spawns("Bonus")
    public Entity newBonus(SpawnData data) {
        BonusType type = data.get("type");

        return Entities.builder()
                .from(data)
                .type(SpaceInvadersType.BONUS)
                .viewFromTextureWithBBox(type.textureName)
                .with(new SubTypeComponent(type), new CollidableComponent(true))
                .with(new BonusControl())
                .build();
    }

    @Spawns("Explosion")
    public Entity newExplosion(SpawnData data) {
        GameEntity explosion = Entities.builder()
                .at(data.getX() - 40, data.getY() - 40)
                // texture is 256x256, we want smaller, 80x80
                // it has 48 frames, hence 80 * 48
                .viewFromNode(assetLoader.loadTexture("explosion.png", 80 * 48, 80).toAnimatedTexture(48, Duration.seconds(2)))
                .with(new ExpireCleanControl(Duration.seconds(1.8)))
                .build();

        // slightly better looking effect
        explosion.getView().setBlendMode(BlendMode.ADD);

        return explosion;
    }

    @Spawns("LevelInfo")
    public Entity newLevelInfo(SpawnData data) {
        Text levelText = FXGL.getUIFactory().newText("Level " + FXGL.getApp().getGameState().getInt("level"), Color.AQUAMARINE, 44);

        PhysicsComponent pComponent = new PhysicsComponent();
        pComponent.setBodyType(BodyType.DYNAMIC);
        pComponent.setOnPhysicsInitialized(() -> pComponent.setLinearVelocity(0, 5));

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.setDensity(0.05f);
        fixtureDef.setRestitution(0.3f);

        pComponent.setFixtureDef(fixtureDef);

        GameEntity levelInfo = Entities.builder()
                .at(FXGL.getAppWidth() / 2 - levelText.getLayoutBounds().getWidth() / 2, 0)
                .viewFromNodeWithBBox(levelText)
                .with(pComponent)
                .with(new ExpireCleanControl(Duration.seconds(LEVEL_START_DELAY)))
                .build();

        levelInfo.setOnActive(() -> {
            Entities.builder()
                    .at(0, FXGL.getAppHeight() / 2)
                    .bbox(new HitBox("ground", BoundingShape.box(FXGL.getAppWidth(), 100)))
                    .with(new PhysicsComponent())
                    .with(new ExpireCleanControl(Duration.seconds(LEVEL_START_DELAY)))
                    .buildAndAttach(FXGL.getApp().getGameWorld());
        });

        return levelInfo;
    }
}
