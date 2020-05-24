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

        setAlignment(Pos.CENTER);
        setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                fire();
            }
        });
    }

    @Override
    protected double computePrefWidth(double height) {
        return 200;
    }
}
