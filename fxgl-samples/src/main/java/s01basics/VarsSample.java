/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s01basics;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.settings.SceneDimension;

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
    }

    @Override
    protected void initGame() {
        getGameState().<SceneDimension>addListener("dim", ((prev, now) -> System.out.println(prev + " " + now)));

        System.out.println(getGameState().getInt("test"));

        System.out.println(getGameState().<SceneDimension>getObject("dim").getWidth());

        System.out.println(getGameState().<SceneDimension>objectProperty("dim").get().getWidth());

        getGameState().setValue("dim", new SceneDimension(300, 300));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
