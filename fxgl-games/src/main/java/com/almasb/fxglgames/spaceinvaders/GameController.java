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
