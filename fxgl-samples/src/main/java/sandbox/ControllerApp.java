/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
//import com.almasb.fxgl.entity.Entity;
//import com.almasb.fxgl.input.UserAction;
//import com.almasb.fxgl.input.virtual.VirtualButton;
//import com.studiohartman.jamepad.ControllerManager;
//import com.studiohartman.jamepad.ControllerState;
//import javafx.scene.input.KeyCode;
//import javafx.scene.paint.Color;
//import javafx.scene.shape.Rectangle;
//import javafx.scene.text.Text;
//
//import static com.almasb.fxgl.dsl.FXGL.*;


public class ControllerApp extends GameApplication {
    @Override
    protected void initSettings(GameSettings settings) {

    }

//    private ControllerManager controllers;
//    private Entity block;
//
//    private Text text;
//
//    @Override
//    protected void initInput() {
//        getInput().addAction(new UserAction("Move") {
//            @Override
//            protected void onAction() {
//                block.translateX(5);
//            }
//        }, KeyCode.D, VirtualButton.A);
//    }
//
//    @Override
//    protected void initGame() {
//        text = getUIFactory().newText("");
//        text.setFill(Color.BLUE);
//
//        block = entityBuilder()
//                .at(0, 240)
//                .view(new Rectangle(40, 40))
//                .view(text)
//                .buildAndAttach();
//
//        controllers = new ControllerManager();
//        controllers.initSDLGamepad();
//    }
//
//    @Override
//    protected void onUpdate(double tpf) {
//        ControllerState currState = controllers.getState(0);
//
//        text.setText("" + currState.rightTrigger);
//
//        if (!currState.isConnected || currState.b) {
//            return;
//        }
//
//        if (currState.a) {
//            getInput().mockKeyPress(KeyCode.D);
//            //System.out.println("\"A\" on \"" + currState.controllerType + "\" is pressed");
//        } else {
//            getInput().mockKeyRelease(KeyCode.D);
//        }
//    }

    public static void main(String[] args) {
        launch(args);
    }
}
