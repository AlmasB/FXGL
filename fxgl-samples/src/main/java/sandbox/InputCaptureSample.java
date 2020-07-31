/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.input.InputCapture;
import javafx.scene.input.KeyCode;

import static com.almasb.fxgl.dsl.FXGL.getInput;
import static com.almasb.fxgl.dsl.FXGL.onKeyDown;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class InputCaptureSample extends GameApplication {
    @Override
    protected void initSettings(GameSettings settings) {

    }

    private InputCapture capture;

    @Override
    protected void initInput() {
        onKeyDown(KeyCode.F, () -> {
            capture = getInput().startCapture();
        });

        onKeyDown(KeyCode.G, () -> {
            getInput().stopCapture();
        });

        onKeyDown(KeyCode.I, () -> {
            getInput().applyCapture(capture);
        });

        onKeyDown(KeyCode.Q, () -> {
            System.out.println("Q");
        });

        onKeyDown(KeyCode.W, () -> {
            System.out.println("W");
        });

        onKeyDown(KeyCode.E, () -> {
            System.out.println("E");
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
