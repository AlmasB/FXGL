/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxglgames.towerfall;

import com.almasb.fxgl.ai.AIControl;
import com.almasb.fxgl.annotation.SetEntityFactory;
import com.almasb.fxgl.annotation.SpawnSymbol;
import com.almasb.fxgl.annotation.Spawns;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.UserDataComponent;
import com.almasb.fxgl.entity.*;
import com.almasb.fxgl.entity.component.CollidableComponent;
import com.almasb.fxgl.entity.control.ExpireCleanControl;
import com.almasb.fxgl.entity.control.OffscreenCleanControl;
import com.almasb.fxgl.entity.view.EntityView;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * Arrow sprite from https://www.spriters-resource.com/game_boy_advance/justiceleagueheroestheflash/sheet/18563/
 *
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@SetEntityFactory
public class TowerfallFactory implements TextEntityFactory {

    @SpawnSymbol('B')
    public Entity newBackground(SpawnData data) {
        Rectangle rect = new Rectangle(1280, 720);
        rect.setFill(new LinearGradient(0, 0, 0, 720, false, CycleMethod.REFLECT,
                new Stop(0, Color.BLACK),
                new Stop(0.5, Color.GRAY)));

        return Entities.builder()
                .viewFromNode(new EntityView(rect, RenderLayer.BACKGROUND))
                .build();
    }

    @SpawnSymbol('1')
    public Entity newPlatform(SpawnData data) {
        return Entities.builder()
                .type(EntityType.PLATFORM)
                .at(data.getX(), data.getY())
                .viewFromNodeWithBBox(FXGL.getAssetLoader().loadTexture("towerfall/brick.png", 40, 40))
                .with(new CollidableComponent(true))
                .with(new PhysicsComponent())
                .build();
    }

    @SpawnSymbol('P')
    public Entity newPlayer(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.DYNAMIC);

        return Entities.builder()
                .type(EntityType.PLAYER)
                .at(data.getX(), data.getY())
                .viewFromNode(new Rectangle(36, 36, Color.BLUE))
                .bbox(new HitBox("Main", BoundingShape.circle(18)))
                .with(physics)
                .with(new CharacterControl())
                .build();
    }

    @Spawns("Enemy")
    @SpawnSymbol('E')
    public Entity newEnemy(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.DYNAMIC);

        return Entities.builder()
                .type(EntityType.ENEMY)
                .at(data.getX(), data.getY())
                .viewFromNode(new Rectangle(36, 36, Color.RED))
                .bbox(new HitBox("Main", BoundingShape.circle(18)))
                .with(physics, new CollidableComponent(true))
                .with(new CharacterControl(), new AIControl("towerfall/enemy_easy.tree"))
                .build();
    }

    @Spawns("Arrow")
    public Entity newArrow(SpawnData data) {
        return Entities.builder()
                .type(EntityType.ARROW)
                .at(data.getX(), data.getY())
                .viewFromNode(FXGL.getAssetLoader().loadTexture("towerfall/arrow.png", 35, 9))
                .bbox(new HitBox("MAIN", BoundingShape.box(28, 8)))
                .with(new CollidableComponent(true), new UserDataComponent(data.get("shooter")))
                .with(new OffscreenCleanControl(), new ExpireCleanControl(Duration.seconds(7)),
                        new ArrowControl(data.<Point2D>get("velocity").normalize()))
                .build();
    }

    @Override
    public char emptyChar() {
        return '0';
    }

    @Override
    public int blockWidth() {
        return 40;
    }

    @Override
    public int blockHeight() {
        return 40;
    }
}
