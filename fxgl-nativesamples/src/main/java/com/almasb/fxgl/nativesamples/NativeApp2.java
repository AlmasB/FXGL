/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.nativesamples;

import com.almasb.fxgl.app.GameApplication;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class NativeApp2 extends Application {

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
