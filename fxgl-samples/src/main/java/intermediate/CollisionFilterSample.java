/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package intermediate;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.components.OffscreenCleanComponent;
import com.almasb.fxgl.dsl.components.ProjectileComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * Shows how to filter (ignore) certain collisions.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class CollisionFilterSample extends GameApplication {

    private enum EType {
        PLAYER, ENEMY, BULLET
    }

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setDeveloperMenuEnabled(true);
    }

    private int i = 0;

    @Override
    protected void initGame() {
        entityBuilder()
                .type(EType.PLAYER)
                .viewWithBBox(new Rectangle(40, 40, Color.BLUE))
                .view(new Text("PLAYER"))
                .at(100, 100)
                .with(new CollidableComponent(true), new PhysicsComponent())
                .buildAndAttach();

        entityBuilder()
                .type(EType.ENEMY)
                .viewWithBBox(new Rectangle(40, 40, Color.RED))
                .view(new Text("ENEMY"))
                .at(400, 100)
                .with(new CollidableComponent(true), new PhysicsComponent())
                .buildAndAttach();

        run(() -> {
            CollidableComponent collidable = new CollidableComponent(true);
            CollidableComponent collidable2 = new CollidableComponent(true);

            if (i == 0) {
                debug("No collision with player");

                collidable.addIgnoredType(EType.PLAYER);
                collidable2.addIgnoredType(EType.PLAYER);
            } else if (i == 1) {
                debug("No collision with enemy");

                collidable.addIgnoredType(EType.ENEMY);
                collidable2.addIgnoredType(EType.ENEMY);
            } else if (i == 2) {
                debug("No collision with player and enemy");

                collidable.addIgnoredType(EType.PLAYER);
                collidable.addIgnoredType(EType.ENEMY);

                collidable2.addIgnoredType(EType.PLAYER);
                collidable2.addIgnoredType(EType.ENEMY);
            }

            i++;
            if (i == 3)
                i = 0;

            // a bullet with no physics
            entityBuilder()
                    .type(EType.BULLET)
                    .viewWithBBox(new Rectangle(20, 10, Color.BLACK))
                    .at(0, 100)
                    .with(new ProjectileComponent(new Point2D(1, 0), 100))
                    .with(new OffscreenCleanComponent())
                    .with(collidable)
                    .buildAndAttach();

            // a bullet with physics
            PhysicsComponent physics = new PhysicsComponent();
            physics.setBodyType(BodyType.DYNAMIC);
            physics.setOnPhysicsInitialized(() -> physics.setLinearVelocity(100, 0));

            entityBuilder()
                    .type(EType.BULLET)
                    .viewWithBBox(new Rectangle(20, 10, Color.BLACK))
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
