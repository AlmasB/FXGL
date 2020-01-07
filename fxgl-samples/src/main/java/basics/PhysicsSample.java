/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package basics;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import dev.DeveloperWASDControl;
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
public class PhysicsSample extends GameApplication {

    private enum Type {
        PLAYER, ENEMY
    }

    @Override
    protected void initSettings(GameSettings settings) { }

    @Override
    protected void initGame() {
        FXGL.entityBuilder()
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
    }

    @Override
    protected void initPhysics() {
        // the order of entities is determined by
        // the order of their types passed into this method
        FXGL.onCollision(Type.PLAYER, Type.ENEMY, (player, enemy) -> {
            System.out.println("On Collision");
        });

        // the above call uses DSL
        // if you need more fine-tuned control, see below

//        PhysicsWorld physics = FXGL.getPhysicsWorld();
//
//        physics.addCollisionHandler(new CollisionHandler(Type.PLAYER, Type.ENEMY) {
//            @Override
//            protected void onHitBoxTrigger(Entity player, Entity enemy, HitBox playerBox, HitBox enemyBox) {
//                System.out.println(playerBox.getName() + " X " + enemyBox.getName());
//            }
//
//            @Override
//            protected void onCollisionBegin(Entity player, Entity enemy) {
//                System.out.println("On Collision Begin");
//            }
//
//            @Override
//            protected void onCollision(Entity player, Entity enemy) {
//                System.out.println("On Collision");
//            }
//
//            @Override
//            protected void onCollisionEnd(Entity player, Entity enemy) {
//                System.out.println("On Collision End");
//            }
//        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
