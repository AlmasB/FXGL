/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxglgames.spacerunner;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.view.ScrollingBackgroundView;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxglgames.spacerunner.control.PlayerControl;
import javafx.geometry.Orientation;
import javafx.scene.input.KeyCode;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class SpaceRunnerApp extends GameApplication {

    private PlayerControl playerControl;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("Space Runner");
        settings.setVersion("0.1");
        settings.setWidth(500);
        settings.setHeight(500);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setCloseConfirmation(false);
        settings.setProfilingEnabled(false);
    }

    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("Move Up") {
            @Override
            protected void onAction() {
                playerControl.up();
            }
        }, KeyCode.W);

        getInput().addAction(new UserAction("Move Down") {
            @Override
            protected void onAction() {
                playerControl.down();
            }
        }, KeyCode.S);

        getInput().addAction(new UserAction("Shoot") {
            @Override
            protected void onActionBegin() {
                playerControl.shoot();
            }
        }, KeyCode.F);
    }

    @Override
    protected void initAssets() {}

    @Override
    protected void initGame() {
        getGameScene().addGameView(new ScrollingBackgroundView(getAssetLoader().loadTexture("spacerunner/bg_0.png"),
                Orientation.HORIZONTAL));

        Entity player = getGameWorld().spawn("Player", 50, getHeight() / 2);

        playerControl = player.getControl(PlayerControl.class);

        getGameScene().getViewport().setBounds(0, 0, Integer.MAX_VALUE, (int) getHeight());
        getGameScene().getViewport().bindToEntity(player, 50, getHeight() / 2);

        getGameWorld().spawn("Enemy1", 500, 300);
    }

    @Override
    protected void initPhysics() {
//        getPhysicsWorld().addCollisionHandler(new CollisionHandler(SpaceRunnerType.BULLET, SpaceRunnerType.ENEMY) {
//            @Override
//            protected void onCollisionBegin(Entity bullet, Entity enemy) {
//                SpaceRunnerType ownerType = (SpaceRunnerType) bullet.getComponent(UserDataComponent.class).getValue();
//
//                if (!Entities.getType(enemy).isType(ownerType)) {
//                    PositionComponent position = Entities.getPosition(enemy);
//                    getGameWorld().addEntity(FXGL.getInstance(SpaceRunnerFactory.class).newEnemy(position.getX() + 500, 300));
//
//                    bullet.removeFromWorld();
//                    enemy.removeFromWorld();
//                }
//            }
//        });
    }

    @Override
    protected void initUI() {

    }

    @Override
    protected void onUpdate(double tpf) {}

    public static void main(String[] args) {
        launch(args);
    }
}
