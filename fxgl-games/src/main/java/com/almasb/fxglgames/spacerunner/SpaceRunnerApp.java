/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.almasb.fxglgames.spacerunner;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.entity.ScrollingBackgroundView;
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

        playerControl = player.getControlUnsafe(PlayerControl.class);

        getGameScene().getViewport().setBounds(0, 0, Integer.MAX_VALUE, (int) getHeight());
        getGameScene().getViewport().bindToEntity(player, 50, getHeight() / 2);

        getGameWorld().spawn("Enemy1", 500, 300);
    }

    @Override
    protected void initPhysics() {
//        getPhysicsWorld().addCollisionHandler(new CollisionHandler(SpaceRunnerType.BULLET, SpaceRunnerType.ENEMY) {
//            @Override
//            protected void onCollisionBegin(Entity bullet, Entity enemy) {
//                SpaceRunnerType ownerType = (SpaceRunnerType) bullet.getComponentUnsafe(UserDataComponent.class).getValue();
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
