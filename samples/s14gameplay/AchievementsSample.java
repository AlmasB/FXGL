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
package s14gameplay;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.entity.component.PositionComponent;
import com.almasb.fxgl.gameplay.Achievement;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.settings.GameSettings;
import common.PlayerControl;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.input.KeyCode;
import javafx.scene.shape.Rectangle;

/**
 * Shows how to register and unlock achievements.
 */
public class AchievementsSample extends GameApplication {

    private PlayerControl playerControl;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("AchievementsSample");
        settings.setVersion("0.1");
        settings.setFullScreen(false);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setShowFPS(false);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    private Achievement achievement;

    // 1. Override initAchievements()
    // create and register achievement
    @Override
    protected void initAchievements() {

        achievement = new Achievement("Move", "Move 500 pixels");

        // might be a JavaFX bug, but sometimes certain parts of the screen are not updated when screen is empty
        Achievement a = new Achievement("World Traveller", "Get to the other side of the screen.");
        getAchievementManager().registerAchievement(a);
        getAchievementManager().registerAchievement(achievement);
    }

    private IntegerProperty moved = new SimpleIntegerProperty(0);

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
                moved.set(moved.get() + (int)(5 * 60 * 0.01667));
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

    @Override
    protected void initAssets() {}

    @Override
    protected void initGame() {
        playerControl = new PlayerControl();
        
        GameEntity player = Entities.builder()
                .at(100, 100)
                .viewFromNode(new Rectangle(40, 40))
                .with(playerControl)
                .buildAndAttach(getGameWorld());

        // 2. bind achievement to the condition
        getAchievementManager().getAchievementByName("World Traveller")
                .bind(player.getComponentUnsafe(PositionComponent.class).xProperty().greaterThan(600));

        achievement.bind(moved, 500);
    }

    @Override
    protected void initPhysics() {}

    @Override
    protected void initUI() {}

    @Override
    public void onUpdate(double tpf) {}

    public static void main(String[] args) {
        launch(args);
    }
}
