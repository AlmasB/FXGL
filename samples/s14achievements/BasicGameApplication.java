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
package s14achievements;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityType;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.gameplay.Achievement;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.app.ApplicationMode;
import javafx.scene.input.KeyCode;
import javafx.scene.shape.Rectangle;

public class BasicGameApplication extends GameApplication {

    private enum Type implements EntityType {
        PLAYER
    }

    private Entity player;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("Basic FXGL Application");
        settings.setVersion("0.1developer");
        settings.setFullScreen(false);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setShowFPS(true);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    // 1. Override initAchievements()..
    // create and register achievement
    @Override
    protected void initAchievements() {
        Achievement a = new Achievement("World Traveller", "Get to the other side of the screen.");
        getAchievementManager().registerAchievement(a);
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

    @Override
    protected void initAssets() {}

    @Override
    protected void initGame() {
        player = new Entity(Type.PLAYER);
        player.setPosition(100, 100);

        Rectangle graphics = new Rectangle(40, 40);
        player.setSceneView(graphics);

        getGameWorld().addEntity(player);

        // 2. bind achievedProperty() to the condition
        getAchievementManager().getAchievementByName("World Traveller")
                .achievedProperty().bind(player.xProperty().greaterThan(600));
    }

    @Override
    protected void initPhysics() {}

    @Override
    protected void initUI() {}

    @Override
    public void onUpdate() {}

    public static void main(String[] args) {
        launch(args);
    }
}
