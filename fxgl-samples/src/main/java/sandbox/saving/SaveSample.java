/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.saving;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.MenuItem;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.saving.DataFile;
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
        settings.setMenuEnabled(true);
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

    // 2. override and specify how to serialize
    @Override
    public DataFile saveState() {

        Bundle bundlePlayer = new Bundle("Player");
        Bundle bundleEnemy = new Bundle("Enemy");

        //player.save(bundlePlayer);
        //enemy.save(bundleEnemy);

        Bundle bundleRoot = new Bundle("Root");
        bundleRoot.put("player", bundlePlayer);
        bundleRoot.put("enemy", bundleEnemy);

        return new DataFile(bundleRoot);
    }

    // 3. override and specify how to deserialize
    // this will be called on "load" game
    @Override
    public void loadState(DataFile dataFile) {

        // call "new" initGame
        initGame();

        // now load state back
        Bundle bundleRoot = (Bundle) dataFile.getData();

        System.out.println(player);
        System.out.println(enemy);

        //player.load(bundleRoot.get("player"));
        //enemy.load(bundleRoot.get("enemy"));

        System.out.println(player);
        System.out.println(enemy);
    }

    // while this will be called on "new" game
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
