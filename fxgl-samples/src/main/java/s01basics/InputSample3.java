/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s01basics;

import com.almasb.fxgl.animation.Animation;
import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.DSLKt;
import dev.DeveloperWASDControl;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.DSLKt.*;

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
        onKeyDown(KeyCode.Q, "Shake", () -> getGameScene().getViewport().shake(5, 0));

        onKeyDown(KeyCode.E, "Lazy", () -> {
            getGameScene().getViewport().setLazy(!getGameScene().getViewport().isLazy());
        });

        onKeyDown(KeyCode.F, "Target", () -> {

            a.start();
        });
    }

    @Override
    protected void initGame() {
        entityBuilder()
                .view("background.png")
                .buildAndAttach();

        var e = entityBuilder()
                .at(150, 150)
                .view(new Rectangle(10, 10, Color.BLUE))
                .with(new DeveloperWASDControl())
                .buildAndAttach();

        getGameScene().getViewport().setBounds(30, 30, 1600 - 30, 1200 - 30);
        //getGameScene().getViewport().bindToEntity(e, FXGL.getAppWidth() / 2, FXGL.getAppHeight() / 2);

        a = translateAnim(getGameScene().getViewport().getCamera(), new Point2D(0, 0), new Point2D(400, 400), Duration.ZERO, Duration.seconds(1.5),
                () -> {}, Interpolators.EXPONENTIAL.EASE_OUT());

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
