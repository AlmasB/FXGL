/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package s04physics;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.entity.component.CollidableComponent;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsWorld;
import com.almasb.fxgl.settings.GameSettings;
import common.PlayerControl;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Shows how to use collision handlers and define hitboxes for entities.
 * When app is running, use WASD to control player and see how collisions are fired
 * when player and enemy overlap.
 */
public class PhysicsSample extends GameApplication {

    private enum Type {
        PLAYER, ENEMY
    }

    private Entity player, enemy;
    private PlayerControl playerControl;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("PhysicsSample");
        settings.setVersion("0.1");
        settings.setFullScreen(false);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setProfilingEnabled(true);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    @Override
    protected void initInput() {
        Input input = getInput();

        input.addAction(new UserAction("Move Left") {
            @Override
            protected void onAction() {
                playerControl.left();
            }
        }, KeyCode.A);

        input.addAction(new UserAction("Move Right") {
            @Override
            protected void onAction() {
                playerControl.right();
            }
        }, KeyCode.D);

        input.addAction(new UserAction("Move Up") {
            @Override
            protected void onAction() {
                playerControl.up();
            }
        }, KeyCode.W);

        input.addAction(new UserAction("Move Down") {
            @Override
            protected void onAction() {
                playerControl.down();
            }
        }, KeyCode.S);
    }

    @Override
    protected void initGame() {
        playerControl = new PlayerControl();

        player = Entities.builder()
                .type(Type.PLAYER)
                .at(100, 100)
                // 1. define hit boxes manually
                .bbox(new HitBox("PLAYER_BODY", BoundingShape.box(40, 40)))
                .viewFromNode(new Rectangle(40, 40, Color.BLUE))
                .with(playerControl)
                .build();

        enemy = Entities.builder()
                .type(Type.ENEMY)
                .at(200, 100)
                // OR let the view generate it from view data
                .viewFromNodeWithBBox(new Rectangle(40, 40, Color.RED))
                .build();

        // 2. we need to add Collidable component and set its value to true
        // so that collision system can 'see' our entities
        player.addComponent(new CollidableComponent(true));
        enemy.addComponent(new CollidableComponent(true));

        getGameWorld().addEntities(player, enemy);
    }

    @Override
    protected void initPhysics() {
        // 3. get physics world and register a collision handler
        // between Type.PLAYER and Type.ENEMY

        PhysicsWorld physics = getPhysicsWorld();

        physics.addCollisionHandler(new CollisionHandler(Type.PLAYER, Type.ENEMY) {
            @Override
            protected void onHitBoxTrigger(Entity player, Entity enemy, HitBox playerBox, HitBox enemyBox) {
                System.out.println(playerBox.getName() + " X " + enemyBox.getName());
            }

            // the order of entities is determined by
            // the order of their types passed into constructor
            @Override
            protected void onCollisionBegin(Entity player, Entity enemy) {
                System.out.println("On Collision Begin");
            }

            @Override
            protected void onCollision(Entity player, Entity enemy) {
                System.out.println("On Collision");
            }

            @Override
            protected void onCollisionEnd(Entity player, Entity enemy) {
                System.out.println("On Collision End");
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
