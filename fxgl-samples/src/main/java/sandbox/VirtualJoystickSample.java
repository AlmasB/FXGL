/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.components.AutoRotationComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.virtual.VirtualJoystick;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 *
 */
public class VirtualJoystickSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(2400);
        settings.setHeight(1320);
    }

    private VirtualJoystick joystick;
    private Text debug;
    private Entity entity;

    @Override
    protected void initGame() {
        getGameScene().setBackgroundColor(Color.LIGHTGRAY);

        joystick = getInput().createVirtualJoystick();
        debug = getUIFactoryService().newText("", Color.BLACK, 18.0);

        entity = entityBuilder()
                .at(300, 300)
                .viewWithBBox(texture("player2rot.png").brighter().outline(Color.BLACK))
                .with(new AutoRotationComponent().withSmoothing())
                .buildAndAttach();

        joystick.setMaxForce(6.0);

        addUINode(joystick, 50, 400);
        addUINode(debug, 50, 60);
    }

    @Override
    protected void onUpdate(double tpf) {
        debug.setText("Vector: " + joystick.getVector() + "\n"
                + "VectorForce: " + joystick.getVectorWithForce());

        if (!joystick.getVector().equals(Point2D.ZERO)) {
            entity.translate(joystick.getVectorWithForce());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
