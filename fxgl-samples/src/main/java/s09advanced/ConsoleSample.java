/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s09advanced;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.Map;

/**
 * Shows how to init a basic game object and attach it to the world
 * using predefined Entity.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class ConsoleSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("ConsoleSample");
        settings.setVersion("0.1");






    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("score", 0);
    }

    @Override
    protected void initGame() {
        Entities.builder()
                .at(100, 100)
                .viewFromNodeWithBBox(new Rectangle(40, 40))
                .buildAndAttach(getGameWorld());
    }

    @Override
    protected void initUI() {
        Text scoreText = getUIFactory().newText("", Color.BLACK, 14);
        scoreText.setTranslateX(300);
        scoreText.setTranslateY(50);
        scoreText.textProperty().bind(getGameState().intProperty("score").asString("Score: %d"));

        getGameScene().addUINode(scoreText);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
