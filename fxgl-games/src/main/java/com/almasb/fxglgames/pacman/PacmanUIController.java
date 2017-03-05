/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
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

package com.almasb.fxglgames.pacman;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.time.UpdateEvent;
import com.almasb.fxgl.time.UpdateEventListener;
import com.almasb.fxgl.ui.UIController;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class PacmanUIController implements UIController, UpdateEventListener {

    @FXML
    private Label labelTitle;

    @FXML
    private Label labelScore;

    @FXML
    private Label labelTeleport;

    @FXML
    private Canvas canvas;

    private GraphicsContext g;

    public Label getLabelScore() {
        return labelScore;
    }

    public Label getLabelTeleport() {
        return labelTeleport;
    }

    @Override
    public void init() {
        labelTitle.setFont(Font.font(48));
        labelTitle.setText("Reverse\nPac-man");
        labelTitle.setEffect(new DropShadow(0.5, 0.5, 1.0, Color.BLACK));
        labelTitle.setTextAlignment(TextAlignment.CENTER);

        labelScore.setFont(FXGL.getUIFactory().newFont(24));
        labelTeleport.setFont(FXGL.getUIFactory().newFont(24));

        g = canvas.getGraphicsContext2D();
        g.setFill(Color.GREENYELLOW);
    }

    private double y = 50;

    @Override
    public void onUpdateEvent(UpdateEvent event) {
//        y += event.tpf() * 60;
//
//        if (y >= 300)
//            y = 50;
//
//        g.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
//        g.fillOval(50, y, 3, 3);
    }
}
