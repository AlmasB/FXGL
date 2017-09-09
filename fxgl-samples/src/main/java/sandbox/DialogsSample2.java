/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.ui.FXGLButton;
import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;

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
    }

    public static void main(String[] args) {
        launch(args);
    }
}
