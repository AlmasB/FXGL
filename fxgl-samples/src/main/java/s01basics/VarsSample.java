/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s01basics;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.app.GameSettings;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;

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
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("test", -1);
        vars.put("vector", new Vec2(1, 1));

        vars.put("score", 0);
        vars.put("lives", 3);
    }

    @Override
    protected void initGame() {
        getGameState().<Vec2>addListener("vector", ((prev, now) -> System.out.println(prev + " " + now)));

        System.out.println(getGameState().getInt("test"));

        System.out.println(getGameState().<Vec2>getObject("vector").x);

        System.out.println(getGameState().<Vec2>objectProperty("vector").get().y);

        getGameState().setValue("vector", new Vec2(300, 300));
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
