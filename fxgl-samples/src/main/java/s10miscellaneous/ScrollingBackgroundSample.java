/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s10miscellaneous;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.entity.ScrollingBackgroundView;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.settings.GameSettings;
import javafx.geometry.Orientation;
import javafx.scene.input.KeyCode;

/**
 * This is an example of a basic FXGL game application.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class ScrollingBackgroundSample extends GameApplication {

    private Entity player;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("ScrollingBackgroundSample");
        settings.setVersion("0.1");
        settings.setFullScreen(false);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setProfilingEnabled(true);
        settings.setCloseConfirmation(false);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    @Override
    protected void initInput() {

        getInput().addAction(new UserAction("Move Right") {
            @Override
            protected void onAction() {

                player.translateX(10);
            }
        }, KeyCode.D);

        getInput().addAction(new UserAction("Move Left") {
            @Override
            protected void onAction() {

                if (player.getX() >= 10)
                    player.translateX(-10);
            }
        }, KeyCode.A);
    }

    @Override
    protected void initGame() {
        player = Entities.builder()
                .buildAndAttach(getGameWorld());

        getGameScene().getViewport().bindToEntity(player, 0, 0);

        // 1. load texture to be the background and specify orientation (horizontal or vertical)
        getGameScene().addGameView(new ScrollingBackgroundView(getAssetLoader().loadTexture("bg_wrap.png", 1066, 600),
                Orientation.HORIZONTAL));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
