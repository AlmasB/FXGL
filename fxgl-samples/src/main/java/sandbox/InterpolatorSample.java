/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.animation.BounceInterpolator;
import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.ui.LevelText;
import javafx.animation.Interpolator;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * Shows how to init a basic game object and attach it to the world
 * using fluent API.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class InterpolatorSample extends GameApplication {

    // 1. define types of entities in the game using Enum
    private enum Type {
        PLAYER
    }

    // make the field instance level
    // but do NOT init here for properly functioning save-load system
    private GameEntity player;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("InterpolatorSample");
        settings.setVersion("0.1");
        settings.setFullScreen(false);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setProfilingEnabled(false);
        settings.setCloseConfirmation(false);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("Play animation") {
            @Override
            protected void onActionBegin() {

                Entities.animationBuilder()
                        .interpolator(Interpolators.Bounce.EASE_OUT())
                        .duration(Duration.seconds(2.3))
                        .translate(player)
                        .from(new Point2D(0, -250))
                        .to(new Point2D(0, 300))
                        .buildAndPlay();
            }
        }, KeyCode.F);
    }

    @Override
    protected void initGame() {
        LevelText text = new LevelText("Level 1");
        text.animateIn();

        // 2. create entity and attach to world using fluent API
        player = Entities.builder()
                .type(Type.PLAYER)
                //.at(400, 100)
                .viewFromNode(text)
                .buildAndAttach(getGameWorld());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
