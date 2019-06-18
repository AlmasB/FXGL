/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.nativesamples;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.ui.FXGLButton;
import javafx.application.Application;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class NativeApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        GameApplication.customLaunch(new GameApp(), stage);
    }

    public static void main(String[] args) {
//        System.setProperty("os.target", "ios");
//        System.setProperty("os.name", "iOS");
//        System.setProperty("glass.platform", "ios");
//        System.setProperty("targetos.name", "iOS");

        launch(args);
    }
}
