/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package s7saving;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.EntityView;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.entity.component.PositionComponent;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.io.DataFile;
import com.almasb.fxgl.settings.GameSettings;
import common.PlayerControl;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.Serializable;

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
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(true);
        settings.setShowFPS(true);
        settings.setApplicationMode(ApplicationMode.DEBUG);
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

    // 2. override and specify how to serialize
    @Override
    public DataFile saveState() {
        PositionComponent playerComponent = Entities.getPosition(player);
        PositionComponent enemyComponent = Entities.getPosition(enemy);

        String data = "";
        data += playerComponent.getX() + "," + playerComponent.getY();
        data += ",";
        data += enemyComponent.getX() + "," + enemyComponent.getY();
        return new DataFile(data);
    }

    // 3. override and specify how to deserialize
    // this will be called on "load" game
    @Override
    public void loadState(DataFile dataFile) {
        String data = (String) dataFile.getData();
        String[] values = data.split(",");

        initGame(new Point2D(Double.parseDouble(values[0]), Double.parseDouble(values[1])),
                new Point2D(Double.parseDouble(values[2]), Double.parseDouble(values[3])));
    }

    @Override
    protected void initAssets() {}

    // while this will be called on "new" game
    @Override
    protected void initGame() {
        initGame(new Point2D(100, 100), new Point2D(200, 100));
    }

    private void initGame(Point2D playerPos, Point2D enemyPos) {
        playerPosition = playerPos;
        enemyPosition = enemyPos;

        player = new GameEntity();
        player.getPositionComponent().setValue(playerPosition);
        player.getMainViewComponent().setView(new EntityView(new Rectangle(40, 40, Color.BLUE)));

        playerControl = new PlayerControl();
        player.addControl(playerControl);

        enemy = new GameEntity();
        enemy.getPositionComponent().setValue(enemyPosition);
        enemy.getMainViewComponent().setView(new EntityView(new Rectangle(40, 40, Color.RED)));

        getGameWorld().addEntities(player, enemy);
    }

    @Override
    protected void initPhysics() {}

    @Override
    protected void initUI() {}

    @Override
    protected void onUpdate(double tpf) {}

    public static void main(String[] args) {
        launch(args);
    }
}
