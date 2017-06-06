/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxglgames.pong;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.ui.UIController;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.util.Duration;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class AppController implements UIController {

    @FXML
    private Label labelScorePlayer;

    @FXML
    private Label labelScoreEnemy;

    public Label getLabelScoreEnemy() {
        return labelScoreEnemy;
    }

    public Label getLabelScorePlayer() {
        return labelScorePlayer;
    }

    @Override
    public void init() {
        labelScorePlayer.setFont(FXGL.getUIFactory().newFont(72));
        labelScoreEnemy.setFont(FXGL.getUIFactory().newFont(72));

        labelScoreEnemy.layoutBoundsProperty().addListener((observable, oldValue, newBounds) -> {
            double width = newBounds.getWidth();
            labelScoreEnemy.setTranslateX(800 - 100 - width);
        });

        labelScorePlayer.textProperty().addListener((observable, oldValue, newValue) -> {
            animateLabel(labelScorePlayer);
        });

        labelScoreEnemy.textProperty().addListener((observable, oldValue, newValue) -> {
            animateLabel(labelScoreEnemy);
        });
    }

    private void animateLabel(Label label) {
        FadeTransition ft = new FadeTransition(Duration.seconds(0.33), label);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }
}
