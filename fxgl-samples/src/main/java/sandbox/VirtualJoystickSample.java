/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.input.virtual.VirtualJoystick;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

/**
 *
 */
public class VirtualJoystickSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) { }

    private VirtualJoystick joystick;
    private Text debug;

    @Override
    protected void initGame() {
        joystick = FXGL.getInput().createVirtualJoystick();
        debug = FXGL.getUIFactoryService().newText("", Color.BLACK, 18.0);

        FXGL.addUINode(joystick, 50, 400);
        FXGL.addUINode(debug, 500, 60);
    }

    @Override
    protected void onUpdate(double tpf) {
        debug.setText("Vector: " + joystick.getVector());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
