/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package com.almasb.fxgl.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;

/**
 * JavaFX Button styled with FXGL CSS.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class FXGLButton extends Button {
    public FXGLButton() {
        this("");
    }

    public FXGLButton(String text) {
        super(text);
        getStyleClass().setAll("fxgl_button");
        setFont(FXGLUIConfig.getUIFactory().newFont(22));
        setAlignment(Pos.CENTER);
        setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {


                //FXGL.getAudioPlayer().playSound(FXGL.getSettings().getSoundMenuPress());
                fire();
            }
        });

        // TODO:
        //setOnMouseEntered(e -> FXGL.getAudioPlayer().playSound(FXGL.getSettings().getSoundMenuSelect()));
        //setOnMouseClicked(e -> FXGL.getAudioPlayer().playSound(FXGL.getSettings().getSoundMenuPress()));
    }

    @Override
    protected double computePrefWidth(double height) {
        return 200;
    }
}
