/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxglgames.breakout;

import com.almasb.fxgl.annotation.SpawnSymbol;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.TextEntityFactory;
import com.almasb.fxgl.entity.component.CollidableComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.physics.box2d.dynamics.FixtureDef;
import com.almasb.fxglgames.breakout.control.BallControl;
import com.almasb.fxglgames.breakout.control.BatControl;
import com.almasb.fxglgames.breakout.control.BrickControl;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class BreakoutFactory implements TextEntityFactory {

    @SpawnSymbol('1')
    public Entity newBrick(SpawnData data) {
        return Entities.builder()
                .from(data)
                .type(BreakoutType.BRICK)
                .viewFromNodeWithBBox(FXGL.getAssetLoader().loadTexture("breakout/brick_blue.png", 232 / 3, 104 / 3))
                .with(new PhysicsComponent(), new CollidableComponent(true))
                .with(new BrickControl())
                .build();
    }

    @SpawnSymbol('9')
    public Entity newBat(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.KINEMATIC);

        return Entities.builder()
                .from(data)
                .type(BreakoutType.BAT)
                .at(FXGL.getSettings().getWidth() / 2 - 50, 30)
                .viewFromNodeWithBBox(FXGL.getAssetLoader().loadTexture("breakout/bat.png", 464 / 3, 102 / 3))
                .with(physics, new CollidableComponent(true))
                .with(new BatControl())
                .build();
    }

    @SpawnSymbol('2')
    public Entity newBall(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.DYNAMIC);

        FixtureDef fd = new FixtureDef();
        fd.setRestitution(1f);
        fd.setDensity(0.03f);
        physics.setFixtureDef(fd);

        return Entities.builder()
                .from(data)
                .type(BreakoutType.BALL)
                .bbox(new HitBox("Main", BoundingShape.circle(20)))
                .viewFromNode(FXGL.getAssetLoader().loadTexture("breakout/ball.png", 40, 40))
                .with(physics, new CollidableComponent(true))
                .with(new BallControl())
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
