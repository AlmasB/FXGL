/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package sandbox;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.logging.Logger;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsWorld;
import dev.DeveloperWASDControl;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Shows how to use collision handlers and define hitboxes for entities.
 *
 * For collisions to work, entities must have:
 * 1. a type
 * 2. a hit box
 * 3. a CollidableComponent (added by calling collidable() on entity builder)
 * 4. a collision handler
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class PhysicsCollisionSample extends GameApplication {

    private Entity player;

    private enum Type {
        PLAYER, ENEMY
    }

    private enum Scale {
        UP,
        DOWN
    }

    @Override
    protected void initSettings(GameSettings settings) { }

    @Override
    protected void initGame() {
        this.player = FXGL.entityBuilder()
                .type(Type.PLAYER)
                .at(100, 100)
                // 1. define hit boxes manually
                .bbox(new HitBox(BoundingShape.box(40, 40)))
                .view(new Rectangle(40, 40, Color.BLUE))
                // 2. make it collidable
                .collidable()
                // Note: in case you are copy-pasting, this class is in dev.DeveloperWASDControl
                // and enables WASD movement for testing
                .with(new DeveloperWASDControl())
                .buildAndAttach();

        FXGL.entityBuilder()
                .type(Type.ENEMY)
                .at(200, 100)
                // 1. OR let the view generate it from view data
                .viewWithBBox(new Rectangle(40, 40, Color.RED))
                // 2. make it collidable
                .collidable()
                .buildAndAttach();

        FXGL.entityBuilder()
                .type(Type.ENEMY)
                .at(300, 100)
                // 1. OR let the view generate it from view data
                .viewWithBBox(new Rectangle(40, 60, Color.RED))
                .rotate(35)
                // 2. make it collidable
                .collidable()
                .buildAndAttach();
    }

    private void scalePlayer(Scale scale, double amount) {
        double newPlayerScaleX = player.getScaleX();
        double newPlayerScaleY = player.getScaleY();

        switch(scale){
            case UP:
                newPlayerScaleX += amount;
                newPlayerScaleY += amount;
                break;
            case DOWN:
                newPlayerScaleX -= amount;
                newPlayerScaleY -= amount;
                break;
            default:
                System.out.println("UNKNOWN Scale Case Provided");
                break;
        }
        player.setScaleX(newPlayerScaleX);
        player.setScaleY(newPlayerScaleY);
    }

    @Override
    protected void initInput() {
        Input input = FXGL.getInput();

        input.addAction(new UserAction("Scale Up") {
            @Override
            protected void onActionBegin() {
                System.out.println("Scaling Up Player.");
            }

            @Override
            protected void onAction() {
                scalePlayer(Scale.UP, 0.20);
            }

            @Override
            protected void onActionEnd() {
            }
        }, KeyCode.RIGHT);

        input.addAction(new UserAction("Scale Down") {
            @Override
            protected void onActionBegin() {
                System.out.println("Scaling Down Player.");
            }

            @Override
            protected void onAction() {
                scalePlayer(Scale.DOWN, 0.20);
            }

            @Override
            protected void onActionEnd() {
            }
        }, KeyCode.LEFT);

        input.addAction(new UserAction("Rotate Right") {
            @Override
            protected void onActionBegin() {
                System.out.println("Rotating Player Right.");
            }

            @Override
            protected void onAction() {
                double currentPlayerRotation = player.getRotation();
                player.setRotation(currentPlayerRotation - 1.5);
            }

            @Override
            protected void onActionEnd() {
            }
        }, KeyCode.Q);

        input.addAction(new UserAction("Rotate Left") {
            @Override
            protected void onActionBegin() {
                System.out.println("Rotating Player Right.");
            }

            @Override
            protected void onAction() {
                double currentPlayerRotation = player.getRotation();
                player.setRotation(currentPlayerRotation + 1.5);
            }

            @Override
            protected void onActionEnd() {
            }
        }, KeyCode.E);
    }
    @Override
    protected void initPhysics() {

        PhysicsWorld physics = FXGL.getPhysicsWorld();

        physics.addCollisionHandler(new CollisionHandler(Type.PLAYER, Type.ENEMY) {
            @Override
            protected void onHitBoxTrigger(Entity player, Entity enemy, HitBox playerBox, HitBox enemyBox) {
                System.out.println(playerBox.getName() + " X " + enemyBox.getName());
            }

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
