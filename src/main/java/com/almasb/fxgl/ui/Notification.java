/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015 AlmasB (almaslvl@gmail.com)
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

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.ServiceType;
import javafx.animation.Transition;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
final class Notification extends Button {

    private Transition in, out;

    Notification(String text, Color bgColor, Transition in, Transition out) {
        super(text);
        this.in = in;
        this.out = out;

        getStyleClass().setAll("fxgl_button");
        setAlignment(Pos.CENTER);
        setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                fire();
            }
        });
        setOnAction(e -> hide());
        setFont(UIFactory.newFont(12));
        setStyle("-fx-background-color: "
                + String.format("rgb(%d,%d,%d);",
                (int)(bgColor.getRed()*255),
                (int)(bgColor.getGreen()*255),
                (int)(bgColor.getBlue()*255)));

        in.setOnFinished(e -> GameApplication.getService(ServiceType.MASTER_TIMER)
                .runOnceAfter(this::hide, Duration.seconds(3)));
    }

    void show() {
        in.play();
    }

    void hide() {
        out.play();
    }
}
