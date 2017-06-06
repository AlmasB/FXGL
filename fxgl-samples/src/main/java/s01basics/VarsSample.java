/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s01basics;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.settings.SceneDimension;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.Map;

/**
 * Shows how to use global property variables.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class VarsSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("VarsSample");
        settings.setVersion("0.1");
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setCloseConfirmation(false);
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("test", -1);
        vars.put("dim", new SceneDimension(100, 50));

        vars.put("score", 0);
        vars.put("lives", 3);
    }

    @Override
    protected void initGame() {
        getGameState().<SceneDimension>addListener("dim", ((prev, now) -> System.out.println(prev + " " + now)));

        System.out.println(getGameState().getInt("test"));

        System.out.println(getGameState().<SceneDimension>getObject("dim").getWidth());

        System.out.println(getGameState().<SceneDimension>objectProperty("dim").get().getWidth());

        getGameState().setValue("dim", new SceneDimension(300, 300));
    }

    @Override
    protected void initUI() {
        Text uiScore = getUIFactory().newText("", Color.BLACK, 16.0);
        uiScore.setTranslateX(100);
        uiScore.setTranslateY(100);
        uiScore.textProperty().bind(getGameState().intProperty("score").asString());

        getGameScene().addUINode(uiScore);
    }

    @Override
    protected void onUpdate(double tpf) {
        getGameState().increment("score", +1);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
