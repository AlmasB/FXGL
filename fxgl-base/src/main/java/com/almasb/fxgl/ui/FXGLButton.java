/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package com.almasb.fxgl.ui;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.asset.FXGLAssets;
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
        setFont(FXGL.getUIFactory().newFont(22));
        setAlignment(Pos.CENTER);
        setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                FXGL.getAudioPlayer().playSound(FXGLAssets.SOUND_MENU_PRESS);
                fire();
            }
        });
        setOnMouseEntered(e -> FXGL.getAudioPlayer().playSound(FXGLAssets.SOUND_MENU_SELECT));
        setOnMouseClicked(e -> FXGL.getAudioPlayer().playSound(FXGLAssets.SOUND_MENU_PRESS));
    }

    @Override
    protected double computePrefWidth(double height) {
        return 200;
    }
}
