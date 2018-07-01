/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.extra.entity.components.OffscreenCleanComponent;
import com.almasb.fxgl.extra.entity.components.ProjectileComponent;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.settings.GameSettings;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class CollisionFilterSample extends GameApplication {

    private enum EType {
        PLAYER, ENEMY, BULLET
    }

    @Override
    protected void initSettings(GameSettings settings) {

    }

    private int i = 0;

    @Override
    protected void initGame() {
        Entities.builder()
                .type(EType.PLAYER)
                .viewFromNodeWithBBox(new Rectangle(40, 40, Color.BLUE))
                .at(100, 100)
                .with(new CollidableComponent(true), new PhysicsComponent())
                .buildAndAttach();

        Entities.builder()
                .type(EType.ENEMY)
                .viewFromNodeWithBBox(new Rectangle(40, 40, Color.RED))
                .at(400, 100)
                .with(new CollidableComponent(true), new PhysicsComponent())
                .buildAndAttach();

        getMasterTimer().runAtInterval(() -> {
            CollidableComponent collidable = new CollidableComponent(true);
            CollidableComponent collidable2 = new CollidableComponent(true);

            System.out.println(i);

            // TODO: integrate with box2d world
            if (i == 0) {
                collidable.addIgnoredType(EType.PLAYER);
                collidable2.addIgnoredType(EType.PLAYER);
            } else if (i == 1) {
                collidable.addIgnoredType(EType.ENEMY);
                collidable2.addIgnoredType(EType.ENEMY);
            } else if (i == 2) {
                collidable.addIgnoredType(EType.PLAYER);
                collidable.addIgnoredType(EType.ENEMY);

                collidable2.addIgnoredType(EType.PLAYER);
                collidable2.addIgnoredType(EType.ENEMY);
            }

            i++;
            if (i == 3)
                i = 0;

            Entities.builder()
                    .type(EType.BULLET)
                    .viewFromNodeWithBBox(new Rectangle(20, 10, Color.BLACK))
                    .at(0, 100)
                    .with(new ProjectileComponent(new Point2D(1, 0), 100))
                    .with(new OffscreenCleanComponent())
                    .with(collidable)
                    .buildAndAttach();

            PhysicsComponent physics = new PhysicsComponent();
            physics.setBodyType(BodyType.DYNAMIC);
            physics.setOnPhysicsInitialized(() -> physics.setLinearVelocity(100, 0));

            Entities.builder()
                    .type(EType.BULLET)
                    .viewFromNodeWithBBox(new Rectangle(20, 10, Color.BLACK))
                    .at(0, 130)
                    .with(physics)
                    .with(new OffscreenCleanComponent())
                    .with(collidable2)
                    .buildAndAttach();
        }, Duration.seconds(4));
    }

    @Override
    protected void initPhysics() {
        getPhysicsWorld().setGravity(0, 0);

        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EType.PLAYER, EType.BULLET) {
            @Override
            protected void onCollisionBegin(Entity a, Entity b) {
                b.removeFromWorld();
            }
        });

        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EType.ENEMY, EType.BULLET) {
            @Override
            protected void onCollisionBegin(Entity a, Entity b) {
                b.removeFromWorld();
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
