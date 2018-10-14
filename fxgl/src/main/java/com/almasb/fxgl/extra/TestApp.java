package com.almasb.fxgl.extra;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.settings.GameSettings;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class TestApp extends GameApplication {
    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("TestApp 11");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
