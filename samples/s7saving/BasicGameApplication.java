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

import java.io.Serializable;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityType;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.PhysicsWorld;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.app.ApplicationMode;

import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class BasicGameApplication extends GameApplication {

    private enum Type implements EntityType {
        PLAYER, ENEMY
    }

    private Entity player, enemy;

    private Text uiText;

    // 1. the data to save/load
    private Point2D playerPosition, enemyPosition;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("Basic FXGL Application");
        settings.setVersion("0.1developer");
        settings.setFullScreen(false);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(true);
        settings.setShowFPS(true);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    @Override
    protected void initInput() {
        Input input = getInput();

        input.addAction(new UserAction("Move Left") {
            @Override
            protected void onAction() {
                player.translate(-5, 0);
            }
        }, KeyCode.A);

        input.addAction(new UserAction("Move Right") {
            @Override
            protected void onAction() {
                player.translate(5, 0);
            }
        }, KeyCode.D);

        input.addAction(new UserAction("Move Up") {
            @Override
            protected void onAction() {
                player.translate(0, -5);
            }
        }, KeyCode.W);

        input.addAction(new UserAction("Move Down") {
            @Override
            protected void onAction() {
                player.translate(0, 5);
            }
        }, KeyCode.S);
    }

    // 2. override and specify how to serialize
    @Override
    public Serializable saveState() {
        String data = "";
        data += player.getX() + "," + player.getY();
        data += ",";
        data += enemy.getX() + "," + enemy.getY();
        return data;
    }

    // 3. override and specify how to deserialize
    // this will be called on "load" game
    @Override
    public void loadState(Serializable loadData) {
        String data = (String) loadData;
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

        player = new Entity(Type.PLAYER);
        player.setPosition(playerPosition);

        Rectangle graphics = new Rectangle(40, 40);
        player.setSceneView(graphics);

        enemy = new Entity(Type.ENEMY);
        enemy.setPosition(enemyPosition);

        Rectangle enemyGraphics = new Rectangle(40, 40);
        enemyGraphics.setFill(Color.RED);
        enemy.setSceneView(enemyGraphics);

        // we need to set collidable to true
        // so that collision system can 'see' them
        player.setCollidable(true);
        enemy.setCollidable(true);

        getGameWorld().addEntities(player, enemy);
    }

    @Override
    protected void initPhysics() {
        PhysicsWorld physics = getPhysicsWorld();
        physics.addCollisionHandler(new CollisionHandler(Type.PLAYER, Type.ENEMY) {
            // the order of entities determined by
            // the order of their types passed into constructor
            @Override
            protected void onCollisionBegin(Entity player, Entity enemy) {
                player.translate(-10, 0);
                enemy.translate(10, 0);
            }
        });
    }

    @Override
    protected void initUI() {
        uiText = new Text();
        uiText.setFont(Font.font(18));

        uiText.setTranslateX(600);
        uiText.setTranslateY(100);

        uiText.textProperty().bind(player.xProperty().asString());

        getGameScene().addUINodes(uiText);
    }

    @Override
    protected void onUpdate() {}

    public static void main(String[] args) {
        launch(args);
    }
}
