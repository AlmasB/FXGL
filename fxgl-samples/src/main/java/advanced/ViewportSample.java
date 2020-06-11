/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package advanced;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import dev.DeveloperWASDControl;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * Shows how to apply viewport effects.
 */
public class ViewportSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) { }

    @Override
    protected void initInput() {
        onKeyDown(KeyCode.Q, "Shake", () -> getGameScene().getViewport().shake(5, 0));

        onKeyDown(KeyCode.E, "Lazy", () -> {
            getGameScene().getViewport().setLazy(!getGameScene().getViewport().isLazy());

            System.out.println("isLazy: " + getGameScene().getViewport().isLazy());
        });

        onKeyDown(KeyCode.F, "Flash", () -> {
            getGameScene().getViewport().flash(() -> System.out.println("Flash finished"));
        });

        onKeyDown(KeyCode.G, "Fade", () -> {
            getGameScene().getViewport().fade(() -> System.out.println("Fade finished"));
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
        getGameScene().getViewport().bindToEntity(e, getAppWidth() / 2, getAppHeight() / 2);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
