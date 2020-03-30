/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.saving;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.MenuItem;
import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.profile.DataFile;
import com.almasb.fxgl.profile.SaveLoadHandler;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.EnumSet;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * Shows how to save and load game state.
 */
public class SaveSample extends GameApplication {

    private enum Type {
        PLAYER, ENEMY
    }

    private Entity player, enemy;
    private PlayerControl playerControl;

    // 1. the data to save/load
    private Point2D playerPosition, enemyPosition;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("SaveSample");
        settings.setVersion("0.1");
        settings.setMainMenuEnabled(true);
        settings.setUserProfileEnabled(true);
        settings.setEnabledMenuItems(EnumSet.allOf(MenuItem.class));
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

        input.addAction(new UserAction("Rotate") {
            @Override
            protected void onAction() {
                player.rotateBy(1);
            }
        }, KeyCode.F);

        input.addAction(new UserAction("Switch Types") {
            @Override
            protected void onAction() {
                if (player.getTypeComponent().isType(Type.PLAYER)) {
                    player.getTypeComponent().setValue(Type.ENEMY);
                    enemy.getTypeComponent().setValue(Type.PLAYER);
                } else {
                    player.getTypeComponent().setValue(Type.PLAYER);
                    enemy.getTypeComponent().setValue(Type.ENEMY);
                }
            }
        }, KeyCode.G);
    }

    @Override
    protected void onPreInit() {
        getSaveLoadService().addHandler(new SaveLoadHandler() {
            @Override
            public void onSave(DataFile data) {
                Bundle bundlePlayer = new Bundle("Player");
                Bundle bundleEnemy = new Bundle("Enemy");

                bundlePlayer.put("id", 3);

                data.putBundle(bundlePlayer);
                data.putBundle(bundleEnemy);
            }

            @Override
            public void onLoad(DataFile data) {
                Bundle bundlePlayer = data.getBundle("Player");
                Bundle bundleEnemy = data.getBundle("Enemy");

                System.out.println(bundlePlayer);
                System.out.println(bundleEnemy);
            }
        });
    }

    @Override
    protected void initGame() {
        initGame(new Point2D(100, 100), new Point2D(200, 100));
    }

    private void initGame(Point2D playerPos, Point2D enemyPos) {
        playerPosition = playerPos;
        enemyPosition = enemyPos;

        player = entityBuilder()
                .type(Type.PLAYER)
                .at(playerPosition)
                .view(new Rectangle(40, 40, Color.BLUE))
                .build();

        playerControl = new PlayerControl();
        player.addComponent(playerControl);

        enemy = new Entity();
//        enemy.getTypeComponent().setValue(Type.ENEMY);
//        enemy.getPositionComponent().setValue(enemyPosition);
//        enemy.getViewComponent().setView(new EntityView(new Rectangle(40, 40, Color.RED)));

        getGameWorld().addEntities(player, enemy);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
