/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.UserAction;
import dev.ScrollingBackgroundView;
import javafx.geometry.Orientation;
import javafx.scene.input.KeyCode;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * This is an example of a basic FXGL game application.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class ScrollingBackgroundSample extends GameApplication {

    private Entity player;

    @Override
    protected void initSettings(GameSettings settings) { }

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
                player.translateX(-10);
            }
        }, KeyCode.A);
    }

    @Override
    protected void initGame() {
        player = entityBuilder()
                .buildAndAttach();

        getGameScene().getViewport().bindToEntity(player, 0, 0);

        entityBuilder()
                .view(new ScrollingBackgroundView(getAssetLoader().loadTexture("bg_1.png", 1066, 600),
                        Orientation.HORIZONTAL))
                .zIndex(-1)
                .buildAndAttach();

        // 1. load texture to be the background and specify orientation (horizontal or vertical)
        //getGameScene().addGameView();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
