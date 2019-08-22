/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.animation.Animation;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import dev.DeveloperWASDControl;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Shows how to use input service and bind actions to triggers.
 */
public class InputSample3 extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("InputSample3");
        settings.setVersion("0.1");
    }

    private Animation<?> a;

    @Override
    protected void initInput() {
        FXGL.onKeyDown(KeyCode.Q, "Shake", () -> FXGL.getGameScene().getViewport().shake(5, 0));

        FXGL.onKeyDown(KeyCode.E, "Lazy", () -> {
            FXGL.getGameScene().getViewport().setLazy(!FXGL.getGameScene().getViewport().isLazy());
        });

        FXGL.onKeyDown(KeyCode.F, "Flash", () -> {
            FXGL.getGameScene().getViewport().flash(() -> System.out.println("Flash finished"));
        });

        FXGL.onKeyDown(KeyCode.G, "Fade", () -> {
            FXGL.getGameScene().getViewport().fade(() -> {
                System.out.println("Fade finished");
            });
        });
    }

    @Override
    protected void initGame() {
        FXGL.entityBuilder()
                .view("background.png")
                .buildAndAttach();

        var e = FXGL.entityBuilder()
                .at(150, 150)
                .view(new Rectangle(10, 10, Color.BLUE))
                .with(new DeveloperWASDControl())
                .buildAndAttach();

        FXGL.getGameScene().getViewport().setBounds(30, 30, 1600 - 30, 1200 - 30);
        FXGL.getGameScene().getViewport().bindToEntity(e, FXGL.getAppWidth() / 2, FXGL.getAppHeight() / 2);

        //a = translateAnim(getGameScene().getViewport().getCamera(), new Point2D(0, 0), new Point2D(400, 400), Duration.ZERO, Duration.seconds(1.5), () -> {}, Interpolators.EXPONENTIAL.EASE_OUT());

        a.setAutoReverse(true);
        a.setCycleCount(2);
    }

    @Override
    public void onUpdate(double tpf) {
        a.onUpdate(tpf);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
