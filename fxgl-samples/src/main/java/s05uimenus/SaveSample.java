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

package s05uimenus;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.EntityView;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.io.serialization.Bundle;
import com.almasb.fxgl.saving.DataFile;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.settings.MenuItem;
import common.PlayerControl;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.EnumSet;

/**
 * Shows how to save and load game state.
 */
public class SaveSample extends GameApplication {

    private enum Type {
        PLAYER, ENEMY
    }

    private GameEntity player, enemy;
    private PlayerControl playerControl;

    // 1. the data to save/load
    private Point2D playerPosition, enemyPosition;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("SaveSample");
        settings.setVersion("0.1");
        settings.setFullScreen(false);
        settings.setIntroEnabled(true);
        settings.setMenuEnabled(true);
        settings.setEnabledMenuItems(EnumSet.of(MenuItem.SAVE_LOAD));
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

        input.addAction(new UserAction("Rotate") {
            @Override
            protected void onAction() {
                player.getRotationComponent().rotateBy(1);
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

        player.save(bundlePlayer);
        enemy.save(bundleEnemy);

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

        player.load(bundleRoot.get("player"));
        enemy.load(bundleRoot.get("enemy"));

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

        player = new GameEntity();
        player.getTypeComponent().setValue(Type.PLAYER);
        player.getPositionComponent().setValue(playerPosition);
        player.getViewComponent().setView(new EntityView(new Rectangle(40, 40, Color.BLUE)));

        playerControl = new PlayerControl();
        player.addControl(playerControl);

        enemy = new GameEntity();
        enemy.getTypeComponent().setValue(Type.ENEMY);
        enemy.getPositionComponent().setValue(enemyPosition);
        enemy.getViewComponent().setView(new EntityView(new Rectangle(40, 40, Color.RED)));

        getGameWorld().addEntities(player, enemy);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
