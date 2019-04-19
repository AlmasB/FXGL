/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.UserAction;
import javafx.scene.input.KeyCode;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class SpriteSheetAnimationApp extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) { }

    private Entity player;

    @Override
    protected void initInput() {
        FXGL.getInput().addAction(new UserAction("Right") {
            @Override
            protected void onAction() {
                player.getComponent(AnimationComponent.class).moveRight();
            }
        }, KeyCode.D);

        FXGL.getInput().addAction(new UserAction("Left") {
            @Override
            protected void onAction() {
                player.getComponent(AnimationComponent.class).moveLeft();
            }
        }, KeyCode.A);
    }

    @Override
    protected void initGame() {
        player = FXGL.entityBuilder()
                .at(200, 200)
                .with(new AnimationComponent())
                .buildAndAttach();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
