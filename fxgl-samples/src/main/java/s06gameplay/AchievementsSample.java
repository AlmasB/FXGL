/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package s06gameplay;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.entity.component.PositionComponent;
import com.almasb.fxgl.gameplay.Achievement;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.settings.GameSettings;
import common.PlayerControl;
import javafx.scene.input.KeyCode;
import javafx.scene.shape.Rectangle;

import java.util.Map;

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
        settings.setProfilingEnabled(false);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    private Achievement achievement;

    // 1. Override initAchievements()
    // create and register achievement
    @Override
    protected void initAchievements() {

        achievement = new Achievement("Move", "Move 500 pixels");

        Achievement a = new Achievement("World Traveller", "Get to the other side of the screen.");
        getGameplay().getAchievementManager().registerAchievement(a);
        getGameplay().getAchievementManager().registerAchievement(achievement);
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("moved", 0);
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
                getGameState().increment("moved", (int) (300 * tpf()));
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
        getGameplay().getAchievementManager().getAchievementByName("World Traveller")
                .bind(player.getComponentUnsafe(PositionComponent.class).xProperty().greaterThan(600));

        achievement.bind(getGameState().intProperty("moved"), 500);
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
