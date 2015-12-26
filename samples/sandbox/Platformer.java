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

package sandbox;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.control.LiftControl;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.search.Maze;
import com.almasb.fxgl.search.MazeCell;
import com.almasb.fxgl.settings.GameSettings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class Platformer extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(true);
        settings.setShowFPS(false);
        settings.setApplicationMode(ApplicationMode.DEBUG);
    }

    private Entity player;

    @Override
    protected void initInput() {
        getAudioPlayer().setGlobalSoundVolume(0);

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

    @Override
    protected void initAssets() {

    }

    private IntegerProperty score;

    @Override
    protected void initGame() {
        score = new SimpleIntegerProperty(133);

        Entity bg = Entity.noType();
        bg.setPosition(0, getHeight() - 40);
        bg.setSceneView(new Rectangle(1200, 40, Color.BLACK));

        Entity block = Entity.noType();
        block.setPosition(0, getHeight() - 80);
        block.setSceneView(new Rectangle(40, 40, Color.RED));

        Entity block2 = Entity.noType();
        block2.setPosition(1160, getHeight() - 80);
        block2.setSceneView(new Rectangle(40, 40, Color.RED));

        Entity block3 = Entity.noType();
        block3.setPosition(getWidth() - 80, getHeight());
        block3.setSceneView(new Rectangle(40, 40, Color.YELLOW));
        block3.addControl(new LiftControl(Duration.seconds(2), 80, true));

        player = Entity.noType();
        player.setPosition(40, getHeight() - 80);
        player.setSceneView(new Rectangle(40, 40, Color.BLUE));

        getGameWorld().addEntities(bg, block, block2, block3, player);

        getGameScene().getViewport().setBounds(0, 0, 1200, (int)getHeight() + 80);
        getGameScene().getViewport().bindToEntity(player, getWidth() / 2, getHeight() / 2);

        Maze maze = new Maze(10, 10);
        for (int y = 0; y < maze.getHeight(); y++) {
            for (int x = 0; x < maze.getWidth(); x++) {
                MazeCell cell = maze.getMaze()[x][y];

                if (cell.hasLeftWall()) {
                    Entity e = Entity.noType();
                    e.setPosition(x*40, y*40);
                    e.setSceneView(new Line(0, 0, 0, 40));

                    getGameWorld().addEntity(e);
                }

                if (cell.hasTopWall()) {
                    Entity e = Entity.noType();
                    e.setPosition(x*40, y*40);
                    e.setSceneView(new Line(0, 0, 40, 0));

                    getGameWorld().addEntity(e);
                }
            }
        }

        Entity e = Entity.noType();
        e.setPosition(10*40, 0*40);
        e.setSceneView(new Line(0, 0, 0, 40*10));

        getGameWorld().addEntity(e);

        Entity e2 = Entity.noType();
        e2.setPosition(0*40, 10*40);
        e2.setSceneView(new Line(0, 0, 40*10, 0));

        getGameWorld().addEntity(e2);
    }

    @Override
    protected void initPhysics() {

    }

    @Override
    protected void initUI() {
        Text text = new Text();
        text.setTranslateX(50);
        text.setTranslateY(100);
        text.setFont(Font.font(18));
        text.textProperty().bind(score.asString());

        getGameScene().addUINode(text);
    }

    @Override
    protected void onUpdate() {

    }
}
