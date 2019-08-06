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
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class GameApp extends GameApplication {
    @Override
    protected void initSettings(GameSettings settings) {
        settings.setApplicationMode(ApplicationMode.DEBUG);
    }

    @Override
    protected void initGame() {
//        FXGL.entityBuilder()
//                .at(200, 200)
//                .view(new Circle(15, Color.BLUE))
//                .buildAndAttach();
    }

    @Override
    protected void initUI() {
//        Button btn = new FXGLButton("HELLO");
//
//        btn.setOnAction(e -> {
//            FXGL.getGameWorld().getEntities().get(0).translate(5, 5);
//        });
//
//        FXGL.addUINode(btn, 100, 100);
    }
}
