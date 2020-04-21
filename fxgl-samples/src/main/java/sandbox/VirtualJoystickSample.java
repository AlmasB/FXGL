/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.input.virtual.joystick.FXGLVirtualJoystick;

/**
 *
 */
public class VirtualJoystickSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
    }

    @Override
    protected void initGame() {
        FXGLVirtualJoystick joystick = FXGLVirtualJoystick.createDefault();

        FXGL.addUINode(joystick, 100, 400);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
