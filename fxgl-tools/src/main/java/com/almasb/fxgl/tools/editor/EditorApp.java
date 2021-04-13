/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.tools.editor;

import com.almasb.fxgl.app.GameApplication;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
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

        var scene = new Scene(root);
        stage.setScene(scene);
        stage.setWidth(1600);
        stage.setHeight(950);
        stage.setTitle("FXGL Editor");
        stage.show();

        // this is the JavaFX app timer since we are running outside of the engine.
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                root.onUpdate(0.016);
            }
        };
        timer.start();
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
