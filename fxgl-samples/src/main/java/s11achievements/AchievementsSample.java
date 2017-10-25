/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package s11achievements;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.settings.MenuItem;
import common.PlayerControl;
import javafx.scene.input.KeyCode;
import javafx.scene.shape.Rectangle;

import java.util.EnumSet;
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
        settings.setEnabledMenuItems(EnumSet.of(MenuItem.EXTRA));
        settings.setProfilingEnabled(false);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("moved", 0);
        vars.put("playerX", 0.0);
    }

    @Override
    protected void initInput() {
        Input input = getInput();

        input.addAction(new UserAction("Move Left") {
            @Override
            protected void onAction() {
                playerControl.left();
                getGameState().increment("moved", (int) (300 * tpf()));
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
                getGameState().increment("moved", (int) (300 * tpf()));
            }
        }, KeyCode.W);

        input.addAction(new UserAction("Move Down") {
            @Override
            protected void onAction() {
                playerControl.down();
                getGameState().increment("moved", (int) (300 * tpf()));
            }
        }, KeyCode.S);
    }

    @Override
    protected void initGame() {
        playerControl = new PlayerControl();
        
        Entity player = Entities.builder()
                .at(100, 100)
                .viewFromNode(new Rectangle(40, 40))
                .with(playerControl)
                .buildAndAttach(getGameWorld());

        getGameState().doubleProperty("playerX").bind(player.getPositionComponent().xProperty());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
