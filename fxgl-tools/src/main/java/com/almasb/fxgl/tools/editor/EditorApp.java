/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.tools.editor;

import com.almasb.fxgl.app.GameApplication;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import static com.almasb.fxgl.dsl.FXGL.getGameController;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class EditorApp extends Application {

    private EditorMainUI root;

    @Override
    public void start(Stage stage) throws Exception {
        root = createContent();

        var fxglPane = GameApplication.embeddedLaunch(new EditorGameApplication(root));

        // TODO: allow notifying when FXGL is ready? so we can build UI for example
        root.addPane(fxglPane);

        stage.setScene(new Scene(root));
        stage.setWidth(1600);
        stage.setHeight(950);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        getGameController().exit();
    }

    private EditorMainUI createContent() {
        return new EditorMainUI();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
