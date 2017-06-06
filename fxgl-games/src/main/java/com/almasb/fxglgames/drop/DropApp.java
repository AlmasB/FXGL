/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxglgames.drop;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.PhysicsWorld;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;

/**
 * This is an FXGL version of the libGDX simple game tutorial which can be found
 * here - https://github.com/libgdx/libgdx/wiki/A-simple-game
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class DropApp extends GameApplication {

    private BucketControl bucketControl;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("Drop");
        settings.setVersion("1.0");
        settings.setWidth(480);
        settings.setHeight(800);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setProfilingEnabled(false);
        settings.setCloseConfirmation(false);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    @Override
    protected void initInput() {
        Input input = getInput();

        input.addAction(new UserAction("Move Left") {
            @Override
            protected void onAction() {
                bucketControl.left();
            }
        }, KeyCode.A);

        input.addAction(new UserAction("Move Right") {
            @Override
            protected void onAction() {
                bucketControl.right();
            }
        }, KeyCode.D);
    }

    @Override
    protected void initGame() {
        Entity bucket = getGameWorld().spawn("Bucket", getWidth() / 2, getHeight() - 200);

        bucketControl = bucket.getControlUnsafe(BucketControl.class);

        getMasterTimer().runAtInterval(() -> {
            getGameWorld().spawn("Droplet", Math.random() * (getWidth() - 64), 0);
        }, Duration.seconds(1));
    }

    @Override
    protected void initPhysics() {
        PhysicsWorld physicsWorld = getPhysicsWorld();

        physicsWorld.addCollisionHandler(new CollisionHandler(DropType.DROPLET, DropType.BUCKET) {
            @Override
            protected void onCollisionBegin(Entity droplet, Entity bucket) {
                droplet.removeFromWorld();

                getAudioPlayer().playSound("drop/drop.wav");
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
