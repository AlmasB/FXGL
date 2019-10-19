/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.app.GameSettings;
import javafx.scene.input.KeyCode;
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
    }

    @Override
    protected void initInput() {
        onKeyDown(KeyCode.Q, "boolean", () -> {
            set("testBoolean", !getb("testBoolean"));
        });

        onKeyDown(KeyCode.W, "int", () -> {
            inc("lives", +2);
        });

        onKeyDown(KeyCode.E, "double", () -> {
            inc("testDouble", +1.5);
        });
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("testDouble", -1.5);
        vars.put("testBoolean", true);
        vars.put("vector", new Vec2(1, 1));

        vars.put("score", 0);
        vars.put("lives", 3);
    }

    @Override
    protected void initGame() {
        getGameState().<Vec2>addListener("vector", (prev, now) -> System.out.println(prev + " " + now));

        System.out.println(getGameState().getDouble("testDouble"));

        System.out.println(getGameState().getBoolean("testBoolean"));

        System.out.println(getGameState().<Vec2>getObject("vector").x);

        System.out.println(getGameState().<Vec2>objectProperty("vector").get().y);

        getGameState().setValue("vector", new Vec2(300, 300));
    }

    @Override
    protected void initUI() {
        Text uiScore = getUIFactory().newText("", Color.BLACK, 16.0);
        uiScore.textProperty().bind(getip("score").asString());
        uiScore.translateXProperty().bind(getInput().mouseXUIProperty());
        uiScore.translateYProperty().bind(getInput().mouseYUIProperty());

        getGameScene().addUINode(uiScore);
    }

    @Override
    protected void onUpdate(double tpf) {
        inc("score", +1);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
