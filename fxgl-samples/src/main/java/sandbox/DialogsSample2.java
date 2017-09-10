/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.DSLKt;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.ui.FXGLButton;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;

import static com.almasb.fxgl.app.DSLKt.onKey;

/**
 * Shows an example of FXGL dialogs.
 * Press F to open a dialog.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class DialogsSample2 extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("DialogsSample2");
        settings.setVersion("0.1");
        settings.setFullScreen(false);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setProfilingEnabled(false);
        settings.setCloseConfirmation(false);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    private DoubleProperty someValue;

    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("Dialog") {
            @Override
            protected void onActionBegin() {

                VBox content = new VBox(50);
                content.setAlignment(Pos.CENTER);
                content.getChildren().addAll(
                        getUIFactory().newText("Line 1"),
                        getUIFactory().newText("Line 2"),
                        getUIFactory().newText("Line 3"),
                        getUIFactory().newText("Line 4"));

                getDisplay().showBox("Dialog Message", content, new FXGLButton("Close"));
            }
        }, KeyCode.F);

        getInput().addAction(new UserAction("Dialog 2") {
            @Override
            protected void onActionBegin() {

                VBox content = new VBox(50);
                content.setAlignment(Pos.CENTER);
                content.getChildren().addAll(
                        getUIFactory().newText("Line 1"));

                getDisplay().showBox("Dialog Message", content, new FXGLButton("Close"));
            }
        }, KeyCode.G);

        getStateMachine().getDialogState().getInput().addAction(new UserAction("decrease") {
            @Override
            protected void onAction() {
                someValue.set(someValue.get() - 0.01);
            }
        }, KeyCode.DIGIT2);

        getStateMachine().getDialogState().getInput().addAction(new UserAction("increase") {
            @Override
            protected void onAction() {
                someValue.set(someValue.get() + 0.01);
            }
        }, KeyCode.DIGIT3);

        getInput().addAction(new UserAction("Dialog 3") {
            @Override
            protected void onActionBegin() {
                getDisplay().showProgressBox("Progress Dialog", someValue, () -> System.out.println("Finished"));
            }
        }, KeyCode.H);
    }

    @Override
    protected void initGame() {
        someValue = new SimpleDoubleProperty(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
