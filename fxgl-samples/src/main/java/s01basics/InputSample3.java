/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s01basics;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import javafx.scene.input.KeyCode;

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

    @Override
    public void onUpdate(double tpf) {

        // 1. you can check if a key is held anytime
        // however bound actions from InputSample2 are preferred
        // to manual checks, because they can be altered via menu controls

        if (getInput().isHeld(KeyCode.F)) {
            System.out.println("F is held");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
