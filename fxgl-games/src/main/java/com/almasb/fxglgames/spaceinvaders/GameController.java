/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxglgames.spaceinvaders;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.scene.GameScene;
import com.almasb.fxgl.scene.Viewport;
import com.almasb.fxgl.texture.Texture;
import com.almasb.fxgl.ui.UIController;
import javafx.animation.Animation;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class GameController implements UIController {

    @FXML
    private Label labelScore;

    @FXML
    private Label labelHighScore;

    @FXML
    private double livesX;

    @FXML
    private double livesY;

    private List<Texture> lives = new ArrayList<>();

    private GameScene gameScene;

    public GameController(GameScene gameScene) {
        this.gameScene = gameScene;
    }

    @Override
    public void init() {
        labelScore.setFont(FXGL.getUIFactory().newFont(18));
        labelHighScore.setFont(FXGL.getUIFactory().newFont(18));
    }

    public Label getLabelScore() {
        return labelScore;
    }

    public Label getLabelHighScore() {
        return labelHighScore;
    }

    public void addLife() {
        int numLives = lives.size();

        Texture texture = FXGL.getAssetLoader().loadTexture("life.png", 16, 16);
        texture.setTranslateX(livesX + 32 * numLives);
        texture.setTranslateY(livesY);

        lives.add(texture);
        gameScene.addUINode(texture);
    }

    public void loseLife() {
        Texture t = lives.get(lives.size() - 1);

        Animation animation = getAnimationLoseLife(t);
        animation.setOnFinished(e -> lives.remove(t));
        animation.play();

        Viewport viewport = gameScene.getViewport();

        Node flash = new Rectangle(viewport.getWidth(), viewport.getHeight(), Color.rgb(190, 10, 15, 0.5));

        gameScene.addUINode(flash);

        FXGL.getMasterTimer().runOnceAfter(() -> gameScene.removeUINode(flash), Duration.seconds(1));
    }

    private Animation getAnimationLoseLife(Texture texture) {
        texture.setFitWidth(64);
        texture.setFitHeight(64);

        Viewport viewport = gameScene.getViewport();

        TranslateTransition tt = new TranslateTransition(Duration.seconds(0.66), texture);
        tt.setToX(viewport.getWidth() / 2 - texture.getFitWidth() / 2);
        tt.setToY(viewport.getHeight() / 2 - texture.getFitHeight() / 2);

        ScaleTransition st = new ScaleTransition(Duration.seconds(0.66), texture);
        st.setToX(0);
        st.setToY(0);

        return new SequentialTransition(tt, st);
    }
}
