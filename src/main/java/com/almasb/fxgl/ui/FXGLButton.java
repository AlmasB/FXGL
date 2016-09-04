/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2016 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
