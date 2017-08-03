/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxglgames.pong;

import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.entity.component.CollidableComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.physics.box2d.dynamics.FixtureDef;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class PongFactory {

    private final GameMode mode;

    public PongFactory(GameMode mode) {
        this.mode = mode;
    }

    public GameEntity newBall(double x, double y) {
        GameEntity ball = Entities.builder()
                .at(x, y)
                .type(EntityType.BALL)
                .bbox(new HitBox("BODY", BoundingShape.circle(5)))
                .viewFromNode(new Circle(5, Color.LIGHTGRAY))
                .build();

        if (mode == GameMode.SP || mode == GameMode.MP_HOST) {
            PhysicsComponent ballPhysics = new PhysicsComponent();
            ballPhysics.setBodyType(BodyType.DYNAMIC);

            FixtureDef def = new FixtureDef();
            def.setDensity(0.3f);
            def.setRestitution(1.0f);

            ballPhysics.setFixtureDef(def);
            ballPhysics.setOnPhysicsInitialized(() -> ballPhysics.setLinearVelocity(5 * 60, -5 * 60));

            ball.addComponent(ballPhysics);
            ball.addComponent(new CollidableComponent(true));
            ball.addControl(new BallControl());
        }

        return ball;
    }

    public GameEntity newBat(double x, double y, boolean isPlayer) {
        GameEntity bat = Entities.builder()
                .at(x, y)
                .type(isPlayer ? EntityType.PLAYER_BAT : EntityType.ENEMY_BAT)
                .viewFromNodeWithBBox(new Rectangle(20, 60, Color.LIGHTGRAY))
                .build();

        if (mode == GameMode.SP || mode == GameMode.MP_HOST) {
            PhysicsComponent batPhysics = new PhysicsComponent();
            batPhysics.setBodyType(BodyType.KINEMATIC);
            bat.addComponent(batPhysics);

            bat.addControl(isPlayer || mode == GameMode.MP_HOST ? new BatControl() : new EnemyBatControl());
        }

        return bat;
    }
}
