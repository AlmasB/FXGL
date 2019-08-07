/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.input.virtual.*;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class VirtualControllerSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(850);
        settings.setHeight(500);
        settings.setMenuEnabled(false);
    }

    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("test") {
            @Override
            protected void onActionBegin() {
                System.out.println("start f");
            }

            @Override
            protected void onAction() {
                System.out.println("f");
            }

            @Override
            protected void onActionEnd() {
                System.out.println("end f");
            }
        }, KeyCode.F, VirtualButton.Y);
    }

    @Override
    protected void initGame() {
        getGameScene().setBackgroundColor(
                new LinearGradient(0.5, 0, 0.5, 1, true, CycleMethod.NO_CYCLE,
                        new Stop(0.1, Color.WHITE),
                        new Stop(0.39, Color.GRAY),
                        new Stop(0.95, Color.BLACK)
                )
        );
    }

    @Override
    protected void initUI() {
        Node dpad = getInput().createVirtualDpad(new CustomDpad());
        Node buttons = getInput().createPSVirtualController();

        addUINode(dpad, 50, 20);
        addUINode(buttons, 200, 150);

        addUINode(getInput().createXboxVirtualController(), 350, 20);

        addUINode(getInput().createVirtualDpad(), 500, 150);

        addUINode(new VirtualPauseButtonOverlay(getInput(), getSettings().getMenuKey(), getSettings().isMenuEnabled()), 650, 50);
    }

    public static class CustomDpad extends VirtualDpad {

        public CustomDpad() {
            super(getInput());
        }

        @Override
        public Node createView(VirtualButton dpadButton) {
            return new Rectangle(40, 40, Color.RED);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
