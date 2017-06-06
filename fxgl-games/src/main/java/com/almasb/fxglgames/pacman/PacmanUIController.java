/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxglgames.pacman;

import com.almasb.fxgl.app.FXGL;
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
public class PacmanUIController implements UIController {

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
}
