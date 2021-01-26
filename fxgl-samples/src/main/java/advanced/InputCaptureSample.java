/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package advanced;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.input.InputCapture;
import javafx.scene.input.KeyCode;

import static com.almasb.fxgl.dsl.FXGL.getInput;
import static com.almasb.fxgl.dsl.FXGL.onKeyDown;

/**
 * Shows how to capture input and then re-apply.
 */
public class InputCaptureSample extends GameApplication {
    @Override
    protected void initSettings(GameSettings settings) { }

    private InputCapture capture;

    @Override
    protected void initInput() {
        // press F to start capturing
        // then press Q, W or E
        // press G to stop capturing
        // press I to apply what you captured with Q, W and E
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
